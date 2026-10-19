package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DifficultyLevel
import com.example.data.model.SqlCodeItem
import com.example.data.model.SqlDatabase
import com.example.ui.theme.BadgeMySQL
import com.example.ui.theme.BadgeOracle
import com.example.ui.theme.BadgePostgres
import com.example.ui.theme.BadgeSqlServer
import com.example.ui.theme.BadgeSqlite
import com.example.ui.theme.BadgeUniversal
import com.example.ui.theme.CodeBgDark
import com.example.ui.theme.CodeBorderDark
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SqlCodeCard(
    item: SqlCodeItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onCopyCode: (String) -> Unit,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hasCopied by remember { mutableStateOf(false) }

    LaunchedEffect(hasCopied) {
        if (hasCopied) {
            delay(1800)
            hasCopied = false
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sql_card_${item.id}")
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Name + Subcategory Tag + Favorite Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("card_title_${item.id}")
                        )
                        if (item.isPopular) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Popular",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "${item.category.displayName} • ${item.subcategory.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                // Favorite Toggle Button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.testTag("btn_favorite_${item.id}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                        tint = if (isFavorite) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Brief Description
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Badges row: Databases + Difficulty + Purpose
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Difficulty Badge
                DifficultyBadge(level = item.difficulty)

                // Databases Badges
                item.databases.take(4).forEach { db ->
                    DatabaseBadge(db = db)
                }
                if (item.databases.size > 4) {
                    DatabaseBadgeCount(remaining = item.databases.size - 4)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Code Syntax Snippet Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CodeBgDark)
                    .border(1.dp, CodeBorderDark, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = SqlSyntaxHighlighter.highlight(item.syntax),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions: Copiar Código | Ver Detalhes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Copy button with feedback
                FilledTonalButton(
                    onClick = {
                        onCopyCode(item.syntax)
                        hasCopied = true
                    },
                    modifier = Modifier.testTag("btn_copy_${item.id}"),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (hasCopied) Color(0xFF065F46) else MaterialTheme.colorScheme.surface,
                        contentColor = if (hasCopied) Color(0xFF34D399) else MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = if (hasCopied) Icons.Filled.Check else Icons.Outlined.ContentCopy,
                        contentDescription = "Copiar código",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (hasCopied) "Copiado!" else "Copiar código",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Details Button
                OutlinedButton(
                    onClick = onOpenDetails,
                    modifier = Modifier.testTag("btn_details_${item.id}"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Ver detalhes",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Outlined.OpenInNew,
                        contentDescription = "Ver detalhes",
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DatabaseBadge(db: SqlDatabase) {
    val color = when (db) {
        SqlDatabase.ALL -> BadgeUniversal
        SqlDatabase.MYSQL -> BadgeMySQL
        SqlDatabase.POSTGRESQL -> BadgePostgres
        SqlDatabase.SQL_SERVER -> BadgeSqlServer
        SqlDatabase.ORACLE -> BadgeOracle
        SqlDatabase.SQLITE -> BadgeSqlite
    }

    Surface(
        color = color.copy(alpha = 0.18f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = db.shortName,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun DatabaseBadgeCount(remaining: Int) {
    Surface(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = "+$remaining bancos",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun DifficultyBadge(level: DifficultyLevel) {
    val (color, label) = when (level) {
        DifficultyLevel.BASIC -> Pair(Color(0xFF10B981), "Básico")
        DifficultyLevel.INTERMEDIATE -> Pair(Color(0xFFF59E0B), "Intermediário")
        DifficultyLevel.ADVANCED -> Pair(Color(0xFFEF4444), "Avançado")
    }

    Surface(
        color = color.copy(alpha = 0.16f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
