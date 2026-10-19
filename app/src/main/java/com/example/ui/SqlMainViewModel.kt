package com.example.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.PracticalScenario
import com.example.data.model.SqlCategory
import com.example.data.model.SqlCodeItem
import com.example.data.model.SqlComparison
import com.example.data.model.SqlDatabase
import com.example.data.repository.AppThemeMode
import com.example.data.repository.FilterCriteria
import com.example.data.repository.LibraryStats
import com.example.data.repository.PreferencesManager
import com.example.data.repository.SqlRepository
import com.example.data.repository.ViewDisplayMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val currentCategory: SqlCategory = SqlCategory.OVERVIEW,
    val filterCriteria: FilterCriteria = FilterCriteria(),
    val displayedItems: List<SqlCodeItem> = emptyList(),
    val totalAllItemsCount: Int = 0,
    val popularItems: List<SqlCodeItem> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val searchHistory: List<String> = emptyList(),
    val comparisons: List<SqlComparison> = emptyList(),
    val practicalScenarios: List<PracticalScenario> = emptyList(),
    val stats: LibraryStats = LibraryStats(0, 0, 0, 0, 0, 0, 0),
    val selectedDetailItem: SqlCodeItem? = null,
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val viewDisplayMode: ViewDisplayMode = ViewDisplayMode.CARDS
)

class SqlMainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SqlRepository()
    private val preferences = PreferencesManager(application)
    private val clipboard = application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    private val _uiState = MutableStateFlow(
        MainUiState(
            totalAllItemsCount = repository.allItems.size,
            popularItems = repository.allItems.filter { it.isPopular },
            favoriteIds = preferences.getFavoriteIds(),
            searchHistory = preferences.getSearchHistory(),
            comparisons = repository.comparisons,
            practicalScenarios = repository.practicalScenarios,
            stats = repository.getStats(),
            themeMode = preferences.getThemeMode(),
            viewDisplayMode = preferences.getViewDisplayMode()
        )
    )
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        refreshItems()
    }

    fun selectCategory(category: SqlCategory) {
        _uiState.update { state ->
            val updatedCriteria = if (category == SqlCategory.FAVORITES) {
                state.filterCriteria.copy(category = null, onlyFavorites = true)
            } else if (category == SqlCategory.OVERVIEW || category == SqlCategory.PRACTICAL || category == SqlCategory.COMPARATOR) {
                state.filterCriteria.copy(category = null, onlyFavorites = false)
            } else {
                state.filterCriteria.copy(category = category, onlyFavorites = false)
            }
            state.copy(
                currentCategory = category,
                filterCriteria = updatedCriteria
            )
        }
        refreshItems()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(filterCriteria = it.filterCriteria.copy(query = query)) }
        refreshItems()
    }

    fun submitSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.length >= 2) {
            preferences.addSearchQuery(trimmed)
            _uiState.update { it.copy(searchHistory = preferences.getSearchHistory()) }
        }
        // If searching and user was on Overview/Practical/Comparator, route to the best matching catalog
        if (_uiState.value.currentCategory == SqlCategory.OVERVIEW) {
            _uiState.update { it.copy(currentCategory = SqlCategory.COMMANDS) }
        }
        updateSearchQuery(query)
    }

    fun removeSearchHistoryItem(item: String) {
        preferences.removeSearchHistoryItem(item)
        _uiState.update { it.copy(searchHistory = preferences.getSearchHistory()) }
    }

    fun updateFilterCriteria(criteria: FilterCriteria) {
        _uiState.update { it.copy(filterCriteria = criteria) }
        refreshItems()
    }

    fun clearAllFilters() {
        _uiState.update { state ->
            val defaultCategory = if (state.currentCategory == SqlCategory.OVERVIEW ||
                state.currentCategory == SqlCategory.PRACTICAL ||
                state.currentCategory == SqlCategory.COMPARATOR ||
                state.currentCategory == SqlCategory.FAVORITES
            ) {
                null
            } else {
                state.currentCategory
            }
            state.copy(
                filterCriteria = FilterCriteria(
                    category = defaultCategory,
                    onlyFavorites = state.currentCategory == SqlCategory.FAVORITES
                )
            )
        }
        refreshItems()
    }

    fun toggleFavorite(id: String) {
        val isNowFavorite = preferences.toggleFavorite(id)
        val updatedSet = preferences.getFavoriteIds()
        _uiState.update { it.copy(favoriteIds = updatedSet) }

        val item = repository.findById(id)
        val title = item?.name ?: "Código"
        val message = if (isNowFavorite) "$title adicionado aos favoritos ★" else "$title removido dos favoritos"
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()

        if (_uiState.value.currentCategory == SqlCategory.FAVORITES || _uiState.value.filterCriteria.onlyFavorites) {
            refreshItems()
        }
    }

    fun copyToClipboard(code: String, label: String = "Código SQL") {
        try {
            val clip = ClipData.newPlainText(label, code)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(getApplication(), "$label copiado para a área de transferência!", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            Toast.makeText(getApplication(), "Erro ao copiar código", Toast.LENGTH_SHORT).show()
        }
    }

    fun openDetail(item: SqlCodeItem) {
        _uiState.update { it.copy(selectedDetailItem = item) }
    }

    fun closeDetail() {
        _uiState.update { it.copy(selectedDetailItem = null) }
    }

    fun cycleThemeMode() {
        val nextMode = when (_uiState.value.themeMode) {
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.LIGHT -> AppThemeMode.SYSTEM
            AppThemeMode.SYSTEM -> AppThemeMode.DARK
        }
        preferences.setThemeMode(nextMode)
        _uiState.update { it.copy(themeMode = nextMode) }
    }

    fun toggleViewMode() {
        val next = if (_uiState.value.viewDisplayMode == ViewDisplayMode.CARDS) {
            ViewDisplayMode.TABLE
        } else {
            ViewDisplayMode.CARDS
        }
        preferences.setViewDisplayMode(next)
        _uiState.update { it.copy(viewDisplayMode = next) }
    }

    private fun refreshItems() {
        val state = _uiState.value
        val items = repository.filterItems(state.filterCriteria, state.favoriteIds)
        _uiState.update { it.copy(displayedItems = items) }
    }
}
