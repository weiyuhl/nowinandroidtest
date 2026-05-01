package com.lhzkml.nowtest.feature.settings.impl

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class AppLanguage(
    val languageTag: String,
) {
    SYSTEM_DEFAULT(""),
    ENGLISH("en"),
    SIMPLIFIED_CHINESE("zh-CN"),
    ;

    companion object {
        fun fromLanguageTags(languageTags: String): AppLanguage =
            entries.firstOrNull { it.languageTag == languageTags } ?: SYSTEM_DEFAULT
    }
}

interface AppLanguageRepository {
    val appLanguage: StateFlow<AppLanguage>

    fun setAppLanguage(appLanguage: AppLanguage)
}

@Singleton
internal class AndroidAppLanguageRepository @Inject constructor() : AppLanguageRepository {
    private val _appLanguage = MutableStateFlow(currentAppLanguage())

    override val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    override fun setAppLanguage(appLanguage: AppLanguage) {
        AppCompatDelegate.setApplicationLocales(
            if (appLanguage == AppLanguage.SYSTEM_DEFAULT) {
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.forLanguageTags(appLanguage.languageTag)
            },
        )
        _appLanguage.value = appLanguage
    }

    private fun currentAppLanguage(): AppLanguage =
        AppLanguage.fromLanguageTags(AppCompatDelegate.getApplicationLocales().toLanguageTags())
}
