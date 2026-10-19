package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SqlCodeItem
import com.example.data.model.SqlDatabase
import com.example.ui.theme.CodeBgDark
import com.example.ui.theme.CodeBorderDark
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SqlDetailDialog(
    item: SqlCodeItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onCopyCode: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var hasCopiedSyntax by remember { mutableStateOf(false) }
    var hasCopiedExample by remember { mutableStateOf(false) }

    LaunchedEffect(hasCopiedSyntax) {
        if (hasCopiedSyntax) {
            delay(1800)
            hasCopiedSyntax = false
        }
    }

    LaunchedEffect(hasCopiedExample) {
        if (hasCopiedExample) {
            delay(1800)
            hasCopiedExample = false
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Top Action Bar: Title, Favorite, Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("detail_title")
                        )
                        Text(
                            text = "${item.category.displayName} • ${item.subcategory.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.testTag("detail_btn_favorite")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Favoritar",
                                tint = if (isFavorite) Color(0xFFFBBF24) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("detail_btn_close")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar detalhes",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Badges: Difficulty, Purpose, Supported Databases
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DifficultyBadge(level = item.difficulty)
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = item.purpose.displayName,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    item.databases.forEach { db ->
                        DatabaseBadge(db = db)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Descrição Completa
                SectionHeader(title = "Descrição")
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Sintaxe Oficial
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(title = "Sintaxe Oficial")
                    FilledTonalButton(
                        onClick = {
                            onCopyCode(item.syntax)
                            hasCopiedSyntax = true
                        },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = if (hasCopiedSyntax) Icons.Filled.Check else Icons.Outlined.ContentCopy,
                            contentDescription = "Copiar sintaxe",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (hasCopiedSyntax) "Copiado!" else "Copiar",
                            fontSize = 11.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CodeBgDark)
                        .border(1.dp, CodeBorderDark, RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = SqlSyntaxHighlighter.highlight(item.syntax),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Exemplo Prático
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(title = "Exemplo Prático")
                    FilledTonalButton(
                        onClick = {
                            onCopyCode(item.example)
                            hasCopiedExample = true
                        },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = if (hasCopiedExample) Icons.Filled.Check else Icons.Outlined.ContentCopy,
                            contentDescription = "Copiar exemplo",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (hasCopiedExample) "Copiado!" else "Copiar exemplo",
                            fontSize = 11.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CodeBgDark)
                        .border(1.dp, CodeBorderDark, RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = SqlSyntaxHighlighter.highlight(item.example),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Explicação do Exemplo
                SectionHeader(title = "Explicação e Comportamento")
                Text(
                    text = item.explanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Observações de Compatibilidade e Migração
                SectionHeader(title = "Compatibilidade e Observações Técnicas")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = item.compatibilityNotes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(12.dp),
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Close Button bottom
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Concluir e Voltar")
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}
