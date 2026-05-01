package com.lhzkml.nowtest

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.app.ApplicationExitInfo
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.exitProcess

object DebugCrashLogInstaller {
    private const val TAG = "NtCrashLog"
    private const val LOG_DIR = "nowtest-crash-logs"
    private const val PREFS_NAME = "debug_crash_log_installer"
    private const val KEY_LAST_EXIT_TIMESTAMP = "last_exit_timestamp"
    private const val ANR_WATCHDOG_INTERVAL_MILLIS = 1_000L
    private const val ANR_WATCHDOG_TIMEOUT_MILLIS = 5_000L
    private const val ANR_WATCHDOG_REPORT_THROTTLE_MILLIS = 30_000L
    private const val MAX_HISTORICAL_EXIT_REASONS = 10

    private val installed = AtomicBoolean(false)

    fun install(application: Application) {
        if (!installed.compareAndSet(false, true)) return

        val appContext = application.applicationContext
        val writer = CrashLogWriter(appContext)

        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching {
                val exportedTo = writer.writeUnhandledException(thread, throwable)
                if (exportedTo != null) {
                    Log.e(TAG, "Crash log exported to $exportedTo", throwable)
                } else {
                    Log.e(TAG, "Crash log export failed", throwable)
                }
            }.onFailure { exportError ->
                Log.e(TAG, "Crash log export failed", exportError)
            }

            if (previousHandler != null) {
                previousHandler.uncaughtException(thread, throwable)
            } else {
                Process.killProcess(Process.myPid())
                exitProcess(10)
            }
        }

        runCatching {
            ActivityStateTracker.install(application)
        }.onFailure { error ->
            Log.e(TAG, "Activity state tracker installation failed", error)
        }

        runCatching {
            writer.writeHistoricalProcessExitReports()
        }.onFailure { error ->
            Log.e(TAG, "Historical process exit export failed", error)
        }

        runCatching {
            MainThreadAnrWatchdog(writer).start()
        }.onFailure { error ->
            Log.e(TAG, "ANR watchdog installation failed", error)
        }
    }

    private object ActivityStateTracker : Application.ActivityLifecycleCallbacks {
        @Volatile
        private var lastActivityState = "No activity lifecycle event recorded"

        fun install(application: Application) {
            application.registerActivityLifecycleCallbacks(this)
        }

        fun snapshot(): String = lastActivityState

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            update(activity, "created")
        }

        override fun onActivityStarted(activity: Activity) {
            update(activity, "started")
        }

        override fun onActivityResumed(activity: Activity) {
            update(activity, "resumed")
        }

        override fun onActivityPaused(activity: Activity) {
            update(activity, "paused")
        }

        override fun onActivityStopped(activity: Activity) {
            update(activity, "stopped")
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

        override fun onActivityDestroyed(activity: Activity) {
            update(activity, "destroyed")
        }

        private fun update(activity: Activity, state: String) {
            lastActivityState = "${activity.javaClass.name}: $state"
        }
    }

    private class MainThreadAnrWatchdog(
        private val writer: CrashLogWriter,
    ) {
        private val mainHandler = Handler(Looper.getMainLooper())
        private val lastMainThreadResponseAt = AtomicLong(SystemClock.uptimeMillis())
        private val lastReportAt = AtomicLong(0)

        fun start() {
            Thread(::watch, "NtAnrWatchdog").apply {
                isDaemon = true
                start()
            }
        }

        private fun watch() {
            try {
                while (true) {
                    mainHandler.post {
                        lastMainThreadResponseAt.set(SystemClock.uptimeMillis())
                    }

                    Thread.sleep(ANR_WATCHDOG_INTERVAL_MILLIS)

                    val now = SystemClock.uptimeMillis()
                    val blockedFor = now - lastMainThreadResponseAt.get()
                    val sinceLastReport = now - lastReportAt.get()

                    if (
                        blockedFor >= ANR_WATCHDOG_TIMEOUT_MILLIS &&
                        sinceLastReport >= ANR_WATCHDOG_REPORT_THROTTLE_MILLIS
                    ) {
                        lastReportAt.set(now)
                        runCatching {
                            writer.writeSuspectedAnr(blockedFor)
                        }.onFailure { error ->
                            Log.e(TAG, "ANR watchdog export failed", error)
                        }
                    }
                }
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    private class CrashLogWriter(
        private val context: Context,
    ) {
        private val prefs: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        fun writeUnhandledException(thread: Thread, throwable: Throwable): String? {
            val timestamp = timestamp()
            val fileName = "nowtest-crash-$timestamp.txt"
            val content = buildUnhandledExceptionLog(thread, throwable, timestamp)

            return write(fileName, content)
        }

        fun writeSuspectedAnr(blockedForMillis: Long): String? {
            val timestamp = timestamp()
            val fileName = "nowtest-anr-suspect-$timestamp.txt"
            val content = buildSuspectedAnrLog(blockedForMillis, timestamp)

            return write(fileName, content)
        }

        fun writeHistoricalProcessExitReports() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return

            val activityManager = context.getSystemService(ActivityManager::class.java)
            val lastRecordedTimestamp = prefs.getLong(KEY_LAST_EXIT_TIMESTAMP, 0)
            val exitReasons = activityManager
                .getHistoricalProcessExitReasons(
                    context.packageName,
                    0,
                    MAX_HISTORICAL_EXIT_REASONS,
                )
                .filter { it.timestamp > lastRecordedTimestamp }
                .sortedBy { it.timestamp }

            var newestTimestamp = lastRecordedTimestamp

            exitReasons.forEachIndexed { index, exitInfo ->
                val timestamp = timestamp(exitInfo.timestamp)
                val fileName = "nowtest-process-exit-$timestamp-$index.txt"
                val content = buildHistoricalProcessExitLog(exitInfo, timestamp)
                val exportedTo = write(fileName, content)

                if (exportedTo != null) {
                    Log.e(TAG, "Previous process exit log exported to $exportedTo")
                }
                newestTimestamp = maxOf(newestTimestamp, exitInfo.timestamp)
            }

            if (newestTimestamp > lastRecordedTimestamp) {
                prefs.edit().putLong(KEY_LAST_EXIT_TIMESTAMP, newestTimestamp).apply()
            }
        }

        private fun write(fileName: String, content: String): String? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                writeWithMediaStore(fileName, content)
            } else {
                writeToPublicDownloads(fileName, content)
            }
        }

        private fun writeWithMediaStore(fileName: String, content: String): String? {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_DOWNLOADS}/$LOG_DIR",
                )
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: return null

            resolver.openOutputStream(uri)?.bufferedWriter().use { writer ->
                writer?.write(content) ?: return null
            }

            values.clear()
            values.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(uri, values, null, null)

            return "${Environment.DIRECTORY_DOWNLOADS}/$LOG_DIR/$fileName"
        }

        private fun writeToPublicDownloads(fileName: String, content: String): String? {
            val directory = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                LOG_DIR,
            )

            if (!directory.exists() && !directory.mkdirs()) return null

            val crashLog = File(directory, fileName)
            crashLog.bufferedWriter().use { writer -> writer.write(content) }
            return crashLog.absolutePath
        }

        private fun buildUnhandledExceptionLog(
            thread: Thread,
            throwable: Throwable,
            timestamp: String,
        ): String =
            buildString {
                appendLine("nowtest debug crash log")
                appendLine("Timestamp: $timestamp")
                appendCommonContext()
                appendLine("Thread: ${thread.name}")
                appendLine()
                appendLine(stackTraceText(throwable))
                appendLine()
                appendAllThreadStacks()
            }

        private fun buildSuspectedAnrLog(blockedForMillis: Long, timestamp: String): String =
            buildString {
                appendLine("nowtest debug suspected ANR log")
                appendLine("Timestamp: $timestamp")
                appendCommonContext()
                appendLine("Main thread blocked for at least: $blockedForMillis ms")
                appendLine()
                appendAllThreadStacks()
            }

        private fun buildHistoricalProcessExitLog(
            exitInfo: ApplicationExitInfo,
            timestamp: String,
        ): String =
            buildString {
                appendLine("nowtest debug historical process exit log")
                appendLine("Exit timestamp: $timestamp")
                appendCommonContext()
                appendLine("Exited process name: ${exitInfo.processName}")
                appendLine("Exit pid: ${exitInfo.pid}")
                appendLine("Exit reason: ${exitInfo.reasonName()} (${exitInfo.reason})")
                appendLine("Exit importance: ${exitInfo.importance}")
                appendLine("Exit status: ${exitInfo.status}")
                appendLine("Exit pss: ${exitInfo.pss} KB")
                appendLine("Exit rss: ${exitInfo.rss} KB")
                appendLine("Exit description: ${exitInfo.description.orEmpty()}")

                val trace = exitInfo.traceText()
                if (trace.isNotBlank()) {
                    appendLine()
                    appendLine("Exit trace:")
                    appendLine(trace)
                }
            }

        private fun StringBuilder.appendCommonContext() {
            appendLine("Package: ${context.packageName}")
            appendLine("Version: ${versionName()} (${versionCode()})")
            appendLine("Process id: ${Process.myPid()}")
            appendLine("Android SDK: ${Build.VERSION.SDK_INT}")
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Last activity state: ${ActivityStateTracker.snapshot()}")

            memoryInfo()?.let { info ->
                appendLine("System low memory: ${info.lowMemory}")
                appendLine("System avail memory: ${info.availMem}")
                appendLine("System threshold memory: ${info.threshold}")
            }
        }

        @Suppress("DEPRECATION")
        private fun StringBuilder.appendAllThreadStacks() {
            appendLine("All thread stack traces:")

            runCatching { Thread.getAllStackTraces() }
                .getOrDefault(emptyMap())
                .entries
                .sortedBy { it.key.name }
                .forEach { (thread, stackTrace) ->
                    appendLine()
                    appendLine(
                        "Thread: ${thread.name}, state=${thread.state}, " +
                            "id=${thread.id}, daemon=${thread.isDaemon}",
                    )
                    stackTrace.forEach { element ->
                        appendLine("\tat $element")
                    }
                }
        }

        private fun memoryInfo(): ActivityManager.MemoryInfo? =
            runCatching {
                ActivityManager.MemoryInfo().also { info ->
                    context.getSystemService(ActivityManager::class.java).getMemoryInfo(info)
                }
            }.getOrNull()

        private fun ApplicationExitInfo.reasonName(): String =
            when (reason) {
                ApplicationExitInfo.REASON_UNKNOWN -> "REASON_UNKNOWN"
                ApplicationExitInfo.REASON_EXIT_SELF -> "REASON_EXIT_SELF"
                ApplicationExitInfo.REASON_SIGNALED -> "REASON_SIGNALED"
                ApplicationExitInfo.REASON_LOW_MEMORY -> "REASON_LOW_MEMORY"
                ApplicationExitInfo.REASON_CRASH -> "REASON_CRASH"
                ApplicationExitInfo.REASON_CRASH_NATIVE -> "REASON_CRASH_NATIVE"
                ApplicationExitInfo.REASON_ANR -> "REASON_ANR"
                ApplicationExitInfo.REASON_INITIALIZATION_FAILURE ->
                    "REASON_INITIALIZATION_FAILURE"
                ApplicationExitInfo.REASON_PERMISSION_CHANGE -> "REASON_PERMISSION_CHANGE"
                ApplicationExitInfo.REASON_EXCESSIVE_RESOURCE_USAGE ->
                    "REASON_EXCESSIVE_RESOURCE_USAGE"
                ApplicationExitInfo.REASON_USER_REQUESTED -> "REASON_USER_REQUESTED"
                ApplicationExitInfo.REASON_USER_STOPPED -> "REASON_USER_STOPPED"
                ApplicationExitInfo.REASON_DEPENDENCY_DIED -> "REASON_DEPENDENCY_DIED"
                ApplicationExitInfo.REASON_OTHER -> "REASON_OTHER"
                else -> "REASON_$reason"
            }

        private fun ApplicationExitInfo.traceText(): String =
            runCatching {
                traceInputStream?.bufferedReader()?.use { reader -> reader.readText() }.orEmpty()
            }.getOrDefault("")

        private fun versionName(): String =
            runCatching { packageInfo().versionName ?: "unknown" }.getOrDefault("unknown")

        @Suppress("DEPRECATION")
        private fun versionCode(): Long =
            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo().longVersionCode
                } else {
                    packageInfo().versionCode.toLong()
                }
            }.getOrDefault(0L)

        @Suppress("DEPRECATION")
        private fun packageInfo() =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    android.content.pm.PackageManager.PackageInfoFlags.of(0),
                )
            } else {
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
    }

    private fun timestamp(): String =
        SimpleDateFormat("yyyyMMdd-HHmmss-SSS", Locale.US).format(Date())

    private fun timestamp(timeMillis: Long): String =
        SimpleDateFormat("yyyyMMdd-HHmmss-SSS", Locale.US).format(Date(timeMillis))

    private fun stackTraceText(throwable: Throwable): String {
        val writer = StringWriter()
        throwable.printStackTrace(PrintWriter(writer))
        return writer.toString()
    }
}
