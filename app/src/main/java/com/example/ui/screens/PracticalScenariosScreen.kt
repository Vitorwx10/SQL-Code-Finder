package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Schema
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PracticalScenario
import com.example.ui.components.DatabaseBadge
import com.example.ui.components.SqlSyntaxHighlighter
import com.example.ui.theme.CodeBgDark
import com.example.ui.theme.CodeBorderDark
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticalScenariosScreen(
    scenarios: List<PracticalScenario>,
    onCopyCode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTag by remember { mutableStateOf<String?>(null) }
    val allTags = remember(scenarios) {
        scenarios.flatMap { it.tags }.distinct()
    }

    val filteredScenarios = remember(selectedTag, scenarios) {
        if (selectedTag == null) scenarios else scenarios.filter { it.tags.contains(selectedTag) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_practical_scenarios"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Exemplos Práticos do Mundo Real",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Modelagens completas de DDL, regras de negócio e consultas analíticas para e-commerce, estoque e faturamento.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tag Filter Chips
        item {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedTag == null,
                    onClick = { selectedTag = null },
                    label = { Text("Todos os Cenários (${scenarios.size})", fontSize = 11.sp) }
                )
                allTags.forEach { tag ->
                    val isSelected = selectedTag == tag
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTag = if (isSelected) null else tag },
                        label = { Text(tag, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Scenarios List
        items(filteredScenarios, key = { it.id }) { scenario ->
            ScenarioCard(
                scenario = scenario,
                onCopyCode = onCopyCode
            )
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScenarioCard(
    scenario: PracticalScenario,
    onCopyCode: (String) -> Unit
) {
    var isExpandedDdl by remember { mutableStateOf(false) }
    var hasCopiedQuery by remember { mutableStateOf(false) }

    LaunchedEffect(hasCopiedQuery) {
        if (hasCopiedQuery) {
            delay(1500)
            hasCopiedQuery = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title & Business Case
            Text(
                text = scenario.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = scenario.businessCase,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tags + DB Badges
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                scenario.compatibleDatabases.take(3).forEach { db ->
                    DatabaseBadge(db = db)
                }
                scenario.tags.forEach { tag ->
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "#$tag",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // DDL Schema Toggle (if schema is not placeholder)
            if (!scenario.ddlSchema.startsWith("--")) {
                OutlinedButton(
                    onClick = { isExpandedDdl = !isExpandedDdl },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schema,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isExpandedDdl) "Ocultar Schema DDL (Tabelas)" else "Ver Estrutura DDL da Tabela",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = if (isExpandedDdl) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }

                AnimatedVisibility(
                    visible = isExpandedDdl,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CodeBgDark)
                            .border(1.dp, CodeBorderDark, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = SqlSyntaxHighlighter.highlight(scenario.ddlSchema),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.5.sp,
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Practical Query Box Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Consulta SQL Aplicada:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                FilledTonalButton(
                    onClick = {
                        onCopyCode(scenario.query)
                        hasCopiedQuery = true
                    },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        imageVector = if (hasCopiedQuery) Icons.Filled.Check else Icons.Outlined.ContentCopy,
                        contentDescription = "Copiar SQL",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (hasCopiedQuery) "Copiado!" else "Copiar SQL", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Query Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CodeBgDark)
                    .border(1.dp, CodeBorderDark, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = SqlSyntaxHighlighter.highlight(scenario.query),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Objective and Explanation
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Objetivo: ${scenario.objective}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = scenario.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}
