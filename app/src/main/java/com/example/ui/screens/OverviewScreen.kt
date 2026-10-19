package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SqlCategory
import com.example.data.model.SqlCodeItem
import com.example.data.repository.LibraryStats
import com.example.ui.components.SqlCodeCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OverviewScreen(
    stats: LibraryStats,
    popularItems: List<SqlCodeItem>,
    favoriteIds: Set<String>,
    onSelectCategory: (SqlCategory) -> Unit,
    onQuickSearch: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onCopyCode: (String) -> Unit,
    onOpenDetails: (SqlCodeItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_overview")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Guia Definitivo de Códigos SQL",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Consulte sintaxes oficiais, compare dialetos (MySQL, PostgreSQL, SQL Server, Oracle e SQLite) e copie códigos validados instantaneamente.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Stat Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatChip(label = "Códigos", value = "${stats.totalItems}+", modifier = Modifier.weight(1f))
                        StatChip(label = "Bancos", value = "5 SGBDs", modifier = Modifier.weight(1f))
                        StatChip(label = "Casos Reais", value = "${stats.practicalCount}", modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Quick Search Suggestions
        item {
            Column {
                Text(
                    text = "Pesquisas Mais Frequentes",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val suggestions = listOf("VARCHAR", "SELECT", "INNER JOIN", "CREATE TABLE", "DATEDIFF", "COALESCE", "GROUP BY", "DECIMAL")
                    suggestions.forEach { tag ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .clickable { onQuickSearch(tag) }
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = tag,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Category Navigation Grid
        item {
            Text(
                text = "Explorar por Categoria",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryHubCard(
                        title = "Tipos de Dados",
                        count = "${stats.dataTypesCount} tipos",
                        icon = Icons.Default.DataObject,
                        color = Color(0xFF0284C7),
                        onClick = { onSelectCategory(SqlCategory.DATA_TYPES) },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryHubCard(
                        title = "Comandos SQL",
                        count = "${stats.commandsCount} comandos",
                        icon = Icons.Default.Code,
                        color = Color(0xFF10B981),
                        onClick = { onSelectCategory(SqlCategory.COMMANDS) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryHubCard(
                        title = "Funções SQL",
                        count = "${stats.functionsCount} funções",
                        icon = Icons.Default.Functions,
                        color = Color(0xFFF59E0B),
                        onClick = { onSelectCategory(SqlCategory.FUNCTIONS) },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryHubCard(
                        title = "Joins e Relações",
                        count = "${stats.joinsCount} tipos",
                        icon = Icons.Default.Lan,
                        color = Color(0xFF8B5CF6),
                        onClick = { onSelectCategory(SqlCategory.JOINS) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryHubCard(
                        title = "Comparador SQL",
                        count = "${stats.comparisonsCount} conceitos",
                        icon = Icons.Default.CompareArrows,
                        color = Color(0xFFEC4899),
                        onClick = { onSelectCategory(SqlCategory.COMPARATOR) },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryHubCard(
                        title = "Exemplos Práticos",
                        count = "${stats.practicalCount} cenários",
                        icon = Icons.Default.PlayArrow,
                        color = Color(0xFF0D9488),
                        onClick = { onSelectCategory(SqlCategory.PRACTICAL) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Popular SQL Codes Highlights
        item {
            Text(
                text = "Mais Utilizados no Dia a Dia",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(popularItems.size) { index ->
            val item = popularItems[index]
            SqlCodeCard(
                item = item,
                isFavorite = favoriteIds.contains(item.id),
                onToggleFavorite = { onToggleFavorite(item.id) },
                onCopyCode = onCopyCode,
                onOpenDetails = { onOpenDetails(item) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CategoryHubCard(
    title: String,
    count: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = color.copy(alpha = 0.16f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = count,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
