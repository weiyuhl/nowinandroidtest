package com.lhzkml.nowtest

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Process
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

object DebugCrashLogInstaller {
    private const val TAG = "NtCrashLog"
    private const val LOG_DIR = "nowtest-crash-logs"

    fun install(application: Application) {
        val appContext = application.applicationContext
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching {
                val exportedTo = CrashLogWriter(appContext).write(thread, throwable)
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
    }

    private class CrashLogWriter(
        private val context: Context,
    ) {
        fun write(thread: Thread, throwable: Throwable): String? {
            val timestamp = timestamp()
            val fileName = "nowtest-crash-$timestamp.txt"
            val content = buildCrashLog(thread, throwable, timestamp)

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

        private fun buildCrashLog(thread: Thread, throwable: Throwable, timestamp: String): String =
            buildString {
                appendLine("nowtest debug crash log")
                appendLine("Timestamp: $timestamp")
                appendLine("Package: ${context.packageName}")
                appendLine("Version: ${versionName()} (${versionCode()})")
                appendLine("Process id: ${Process.myPid()}")
                appendLine("Thread: ${thread.name}")
                appendLine("Android SDK: ${Build.VERSION.SDK_INT}")
                appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                appendLine()
                appendLine(stackTraceText(throwable))
            }

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

    private fun stackTraceText(throwable: Throwable): String {
        val writer = StringWriter()
        throwable.printStackTrace(PrintWriter(writer))
        return writer.toString()
    }
}
