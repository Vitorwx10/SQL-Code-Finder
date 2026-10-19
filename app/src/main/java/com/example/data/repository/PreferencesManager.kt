package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences

enum class AppThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

enum class ViewDisplayMode {
    CARDS,
    TABLE
}

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("sql_code_finder_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FAVORITES = "user_favorite_sql_ids"
        private const val KEY_SEARCH_HISTORY = "user_search_history"
        private const val KEY_THEME_MODE = "user_theme_mode"
        private const val KEY_VIEW_MODE = "user_view_display_mode"
        private const val MAX_HISTORY_ITEMS = 8
    }

    fun getFavoriteIds(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun toggleFavorite(id: String): Boolean {
        val current = getFavoriteIds().toMutableSet()
        val isNowFavorite = if (current.contains(id)) {
            current.remove(id)
            false
        } else {
            current.add(id)
            true
        }
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
        return isNowFavorite
    }

    fun isFavorite(id: String): Boolean {
        return getFavoriteIds().contains(id)
    }

    fun getSearchHistory(): List<String> {
        val raw = prefs.getString(KEY_SEARCH_HISTORY, "") ?: ""
        if (raw.isBlank()) return emptyList()
        return raw.split("|||").filter { it.isNotBlank() }
    }

    fun addSearchQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank() || trimmed.length < 2) return
        val current = getSearchHistory().toMutableList()
        current.remove(trimmed)
        current.add(0, trimmed)
        val limited = current.take(MAX_HISTORY_ITEMS)
        prefs.edit().putString(KEY_SEARCH_HISTORY, limited.joinToString("|||")).apply()
    }

    fun removeSearchHistoryItem(query: String) {
        val current = getSearchHistory().toMutableList()
        current.remove(query)
        prefs.edit().putString(KEY_SEARCH_HISTORY, current.joinToString("|||")).apply()
    }

    fun clearSearchHistory() {
        prefs.edit().remove(KEY_SEARCH_HISTORY).apply()
    }

    fun getThemeMode(): AppThemeMode {
        val name = prefs.getString(KEY_THEME_MODE, AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name
        return try {
            AppThemeMode.valueOf(name)
        } catch (_: Exception) {
            AppThemeMode.DARK
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    fun getViewDisplayMode(): ViewDisplayMode {
        val name = prefs.getString(KEY_VIEW_MODE, ViewDisplayMode.CARDS.name) ?: ViewDisplayMode.CARDS.name
        return try {
            ViewDisplayMode.valueOf(name)
        } catch (_: Exception) {
            ViewDisplayMode.CARDS
        }
    }

    fun setViewDisplayMode(mode: ViewDisplayMode) {
        prefs.edit().putString(KEY_VIEW_MODE, mode.name).apply()
    }
}
