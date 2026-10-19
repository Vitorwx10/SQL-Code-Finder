package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AppThemeMode
import com.example.data.repository.ViewDisplayMode

@Composable
fun SqlTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    searchHistory: List<String>,
    onSelectHistoryItem: (String) -> Unit,
    onRemoveHistoryItem: (String) -> Unit,
    themeMode: AppThemeMode,
    onCycleTheme: () -> Unit,
    viewMode: ViewDisplayMode,
    onToggleViewMode: () -> Unit,
    favoritesCount: Int,
    onOpenFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var isSearchFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Header: Logo, App Name, Theme switcher, View mode, Favorites
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = "SQL Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "SQL Code Finder",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Biblioteca Inteligente Multi-DB",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp
                    )
                }
            }

            // Right header actions: View mode, Theme cycle, Favorites
            Row(verticalAlignment = Alignment.CenterVertically) {
                // View Mode Toggle (Cards vs Table)
                IconButton(
                    onClick = onToggleViewMode,
                    modifier = Modifier.testTag("btn_toggle_view_mode")
                ) {
                    Icon(
                        imageVector = if (viewMode == ViewDisplayMode.CARDS) Icons.Default.TableRows else Icons.Default.GridView,
                        contentDescription = "Alternar para ${if (viewMode == ViewDisplayMode.CARDS) "Tabela" else "Cards"}",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Theme Toggle
                IconButton(
                    onClick = onCycleTheme,
                    modifier = Modifier.testTag("btn_cycle_theme")
                ) {
                    Icon(
                        imageVector = when (themeMode) {
                            AppThemeMode.DARK -> Icons.Default.DarkMode
                            AppThemeMode.LIGHT -> Icons.Default.LightMode
                            AppThemeMode.SYSTEM -> Icons.Outlined.SettingsBrightness
                        },
                        contentDescription = "Alternar Tema",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Favorites shortcut with badge
                IconButton(
                    onClick = onOpenFavorites,
                    modifier = Modifier.testTag("btn_header_favorites")
                ) {
                    if (favoritesCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = Color(0xFFF59E0B),
                                    contentColor = Color.Black
                                ) {
                                    Text(
                                        text = "$favoritesCount",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Favoritos",
                                tint = Color(0xFFFBBF24)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.StarBorder,
                            contentDescription = "Favoritos",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = "Pesquise VARCHAR, SELECT, JOIN, contar datas...",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Pesquisar",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.testTag("btn_clear_search")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpar busca",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    if (searchQuery.isNotBlank()) {
                        onSearchSubmit(searchQuery)
                    }
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("main_search_input")
                .onFocusChanged { isSearchFocused = it.isFocused }
        )

        // Recent search history row
        if (searchHistory.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = "Histórico",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                searchHistory.forEach { historyQuery ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.clickable {
                            onSelectHistoryItem(historyQuery)
                            focusManager.clearFocus()
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = historyQuery,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Remover",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .size(12.dp)
                                    .clickable { onRemoveHistoryItem(historyQuery) }
                            )
                        }
                    }
                }
            }
        }
    }
}
