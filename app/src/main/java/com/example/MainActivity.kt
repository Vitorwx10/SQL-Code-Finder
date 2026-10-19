package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SqlCategory
import com.example.ui.SqlMainViewModel
import com.example.ui.components.SqlDetailDialog
import com.example.ui.components.SqlTopBar
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.ComparatorScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.screens.PracticalScenariosScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SqlMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            MyApplicationTheme(themeMode = uiState.themeMode) {
                // Back handler: if detail is open, close it; else if not in OVERVIEW, go back to OVERVIEW
                BackHandler(enabled = uiState.selectedDetailItem != null || uiState.currentCategory != SqlCategory.OVERVIEW) {
                    if (uiState.selectedDetailItem != null) {
                        viewModel.closeDetail()
                    } else if (uiState.currentCategory != SqlCategory.OVERVIEW) {
                        viewModel.selectCategory(SqlCategory.OVERVIEW)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Column {
                            // Top App Bar with search, history, theme toggle & favorites count
                            SqlTopBar(
                                searchQuery = uiState.filterCriteria.query,
                                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                onSearchSubmit = { viewModel.submitSearch(it) },
                                searchHistory = uiState.searchHistory,
                                onSelectHistoryItem = { viewModel.submitSearch(it) },
                                onRemoveHistoryItem = { viewModel.removeSearchHistoryItem(it) },
                                themeMode = uiState.themeMode,
                                onCycleTheme = { viewModel.cycleThemeMode() },
                                viewMode = uiState.viewDisplayMode,
                                onToggleViewMode = { viewModel.toggleViewMode() },
                                favoritesCount = uiState.favoriteIds.size,
                                onOpenFavorites = { viewModel.selectCategory(SqlCategory.FAVORITES) }
                            )

                            // 8-Category Navigation Tabs
                            CategoryNavigationTabs(
                                currentCategory = uiState.currentCategory,
                                onSelectCategory = { viewModel.selectCategory(it) },
                                favoritesCount = uiState.favoriteIds.size
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (uiState.currentCategory) {
                            SqlCategory.OVERVIEW -> {
                                OverviewScreen(
                                    stats = uiState.stats,
                                    popularItems = uiState.popularItems,
                                    favoriteIds = uiState.favoriteIds,
                                    onSelectCategory = { viewModel.selectCategory(it) },
                                    onQuickSearch = {
                                        viewModel.submitSearch(it)
                                    },
                                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                                    onCopyCode = { viewModel.copyToClipboard(it) },
                                    onOpenDetails = { viewModel.openDetail(it) }
                                )
                            }
                            SqlCategory.COMPARATOR -> {
                                ComparatorScreen(
                                    comparisons = uiState.comparisons,
                                    onCopyCode = { viewModel.copyToClipboard(it) }
                                )
                            }
                            SqlCategory.PRACTICAL -> {
                                PracticalScenariosScreen(
                                    scenarios = uiState.practicalScenarios,
                                    onCopyCode = { viewModel.copyToClipboard(it) }
                                )
                            }
                            else -> {
                                CatalogScreen(
                                    currentCategory = uiState.currentCategory,
                                    items = uiState.displayedItems,
                                    totalAllCount = uiState.totalAllItemsCount,
                                    favoriteIds = uiState.favoriteIds,
                                    filterCriteria = uiState.filterCriteria,
                                    onUpdateFilterCriteria = { viewModel.updateFilterCriteria(it) },
                                    onClearAllFilters = { viewModel.clearAllFilters() },
                                    viewMode = uiState.viewDisplayMode,
                                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                                    onCopyCode = { viewModel.copyToClipboard(it) },
                                    onOpenDetails = { viewModel.openDetail(it) }
                                )
                            }
                        }

                        // Detail Dialog
                        uiState.selectedDetailItem?.let { detailItem ->
                            SqlDetailDialog(
                                item = detailItem,
                                isFavorite = uiState.favoriteIds.contains(detailItem.id),
                                onToggleFavorite = { viewModel.toggleFavorite(detailItem.id) },
                                onCopyCode = { viewModel.copyToClipboard(it) },
                                onDismiss = { viewModel.closeDetail() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryNavigationTabs(
    currentCategory: SqlCategory,
    onSelectCategory: (SqlCategory) -> Unit,
    favoritesCount: Int,
    modifier: Modifier = Modifier
) {
    val categories = SqlCategory.values()
    val selectedIndex = categories.indexOf(currentCategory).coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier
            .fillMaxWidth()
            .testTag("nav_category_tabs"),
        edgePadding = 12.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            if (selectedIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                    height = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        divider = {}
    ) {
        categories.forEachIndexed { index, category ->
            val isSelected = category == currentCategory
            val icon = getCategoryIcon(category)

            Tab(
                selected = isSelected,
                onClick = { onSelectCategory(category) },
                modifier = Modifier.testTag("tab_${category.name}"),
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = category.displayName,
                            modifier = Modifier.size(16.dp),
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = category.displayName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (category == SqlCategory.FAVORITES && favoritesCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                color = Color(0xFFF59E0B),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "$favoritesCount",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

fun getCategoryIcon(category: SqlCategory): ImageVector {
    return when (category) {
        SqlCategory.OVERVIEW -> Icons.Default.Dashboard
        SqlCategory.DATA_TYPES -> Icons.Default.DataObject
        SqlCategory.COMMANDS -> Icons.Default.Code
        SqlCategory.FUNCTIONS -> Icons.Default.Functions
        SqlCategory.JOINS -> Icons.Default.Lan
        SqlCategory.PRACTICAL -> Icons.Default.PlayArrow
        SqlCategory.COMPARATOR -> Icons.Default.CompareArrows
        SqlCategory.FAVORITES -> Icons.Default.Star
    }
}
