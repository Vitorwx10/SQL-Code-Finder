package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DifficultyLevel
import com.example.data.model.Purpose
import com.example.data.model.SqlCategory
import com.example.data.model.SqlDatabase
import com.example.data.repository.FilterCriteria
import com.example.data.repository.SortOption

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SqlFilterSection(
    criteria: FilterCriteria,
    onUpdateCriteria: (FilterCriteria) -> Unit,
    onClearAllFilters: () -> Unit,
    totalFound: Int,
    totalAll: Int,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Count active filters (excluding query)
    val activeFilterCount = (if (criteria.database != SqlDatabase.ALL) 1 else 0) +
            (if (criteria.difficulty != null) 1 else 0) +
            (if (criteria.purpose != null) 1 else 0) +
            (if (criteria.onlyFavorites) 1 else 0) +
            (if (criteria.highCompatibilityOnly) 1 else 0) +
            (if (criteria.popularOnly) 1 else 0) +
            (if (criteria.sortOption != SortOption.RELEVANCE) 1 else 0)

    Column(modifier = modifier.fillMaxWidth()) {
        // Quick Database selector row (horizontal scroll)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SqlDatabase.values().forEach { db ->
                val isSelected = criteria.database == db
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        onUpdateCriteria(criteria.copy(database = db))
                    },
                    label = {
                        Text(
                            text = db.shortName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("filter_db_${db.name}"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        // Subheader: Results count + Filter toggle + Clear all
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$totalFound resultados",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = " (de $totalAll)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (activeFilterCount > 0 || criteria.query.isNotBlank()) {
                    TextButton(
                        onClick = onClearAllFilters,
                        modifier = Modifier.testTag("btn_clear_filters")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Limpar filtros",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Limpar tudo", fontSize = 12.sp)
                    }
                }

                // Expand Filters button with badge
                OutlinedButton(
                    onClick = { isExpanded = !isExpanded },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isExpanded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent
                    ),
                    modifier = Modifier.testTag("btn_toggle_filters_panel")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filtros avançados",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Filtros", fontSize = 12.sp)
                    if (activeFilterCount > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "$activeFilterCount",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }

        // Expandable Filters Panel
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Quick Toggles: Favoritos | Alta Compatibilidade | Mais Utilizados
                    Text(
                        text = "Filtros Especiais",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilterChip(
                            selected = criteria.onlyFavorites,
                            onClick = { onUpdateCriteria(criteria.copy(onlyFavorites = !criteria.onlyFavorites)) },
                            label = { Text("Apenas Favoritos ★", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = criteria.highCompatibilityOnly,
                            onClick = { onUpdateCriteria(criteria.copy(highCompatibilityOnly = !criteria.highCompatibilityOnly)) },
                            label = { Text("Alta Compatibilidade (Universal)", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = criteria.popularOnly,
                            onClick = { onUpdateCriteria(criteria.copy(popularOnly = !criteria.popularOnly)) },
                            label = { Text("Mais Utilizados", fontSize = 11.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dificuldade
                    Text(
                        text = "Nível de Dificuldade",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = criteria.difficulty == null,
                            onClick = { onUpdateCriteria(criteria.copy(difficulty = null)) },
                            label = { Text("Todos", fontSize = 11.sp) }
                        )
                        DifficultyLevel.values().forEach { diff ->
                            FilterChip(
                                selected = criteria.difficulty == diff,
                                onClick = { onUpdateCriteria(criteria.copy(difficulty = if (criteria.difficulty == diff) null else diff)) },
                                label = { Text(diff.displayName, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Finalidade
                    Text(
                        text = "Finalidade do Código",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilterChip(
                            selected = criteria.purpose == null,
                            onClick = { onUpdateCriteria(criteria.copy(purpose = null)) },
                            label = { Text("Todas as finalidades", fontSize = 11.sp) }
                        )
                        Purpose.values().forEach { purp ->
                            FilterChip(
                                selected = criteria.purpose == purp,
                                onClick = { onUpdateCriteria(criteria.copy(purpose = if (criteria.purpose == purp) null else purp)) },
                                label = { Text(purp.displayName, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Ordenação
                    Text(
                        text = "Ordenar Resultados",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SortOption.values().forEach { opt ->
                            FilterChip(
                                selected = criteria.sortOption == opt,
                                onClick = { onUpdateCriteria(criteria.copy(sortOption = opt)) },
                                label = { Text(opt.displayName, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }
    }
}
