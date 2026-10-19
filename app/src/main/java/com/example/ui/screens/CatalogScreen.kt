package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SqlCategory
import com.example.data.model.SqlCodeItem
import com.example.data.repository.FilterCriteria
import com.example.data.repository.ViewDisplayMode
import com.example.ui.components.SqlCodeCard
import com.example.ui.components.SqlCompactRow
import com.example.ui.components.SqlFilterSection

@Composable
fun CatalogScreen(
    currentCategory: SqlCategory,
    items: List<SqlCodeItem>,
    totalAllCount: Int,
    favoriteIds: Set<String>,
    filterCriteria: FilterCriteria,
    onUpdateFilterCriteria: (FilterCriteria) -> Unit,
    onClearAllFilters: () -> Unit,
    viewMode: ViewDisplayMode,
    onToggleFavorite: (String) -> Unit,
    onCopyCode: (String) -> Unit,
    onOpenDetails: (SqlCodeItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_catalog_${currentCategory.name}")
    ) {
        // Category Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = currentCategory.displayName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = currentCategory.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Live Combinable Filters
        SqlFilterSection(
            criteria = filterCriteria,
            onUpdateCriteria = onUpdateFilterCriteria,
            onClearAllFilters = onClearAllFilters,
            totalFound = items.size,
            totalAll = totalAllCount
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Empty state vs Results list
        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "Nenhum resultado",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nenhum código SQL encontrado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tente alterar os termos da busca ou redefinir os filtros de banco de dados e dificuldade.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onClearAllFilters,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_empty_clear_filters")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Limpar filtros e busca")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    if (viewMode == ViewDisplayMode.CARDS) {
                        SqlCodeCard(
                            item = item,
                            isFavorite = favoriteIds.contains(item.id),
                            onToggleFavorite = { onToggleFavorite(item.id) },
                            onCopyCode = onCopyCode,
                            onOpenDetails = { onOpenDetails(item) }
                        )
                    } else {
                        SqlCompactRow(
                            item = item,
                            isFavorite = favoriteIds.contains(item.id),
                            onToggleFavorite = { onToggleFavorite(item.id) },
                            onCopyCode = onCopyCode,
                            onOpenDetails = { onOpenDetails(item) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
