package com.example.data.repository

import com.example.data.model.DifficultyLevel
import com.example.data.model.PracticalScenario
import com.example.data.model.Purpose
import com.example.data.model.SqlCategory
import com.example.data.model.SqlCodeItem
import com.example.data.model.SqlComparison
import com.example.data.model.SqlDatabase
import java.text.Normalizer

enum class SortOption(val displayName: String) {
    RELEVANCE("Relevância"),
    NAME_ASC("Nome (A-Z)"),
    NAME_DESC("Nome (Z-A)"),
    DIFFICULTY_ASC("Dificuldade (Básico primeiro)"),
    DIFFICULTY_DESC("Dificuldade (Avançado primeiro)")
}

data class FilterCriteria(
    val query: String = "",
    val database: SqlDatabase = SqlDatabase.ALL,
    val category: SqlCategory? = null,
    val difficulty: DifficultyLevel? = null,
    val purpose: Purpose? = null,
    val onlyFavorites: Boolean = false,
    val highCompatibilityOnly: Boolean = false,
    val popularOnly: Boolean = false,
    val sortOption: SortOption = SortOption.RELEVANCE
)

data class LibraryStats(
    val totalItems: Int,
    val dataTypesCount: Int,
    val commandsCount: Int,
    val functionsCount: Int,
    val joinsCount: Int,
    val practicalCount: Int,
    val comparisonsCount: Int
)

class SqlRepository {

    val allItems: List<SqlCodeItem> by lazy {
        SqlDataTypes.items + SqlCommandsData.items + SqlFunctionsAndJoinsData.items
    }

    val comparisons: List<SqlComparison> = SqlComparisonsData.comparisons
    val practicalScenarios: List<PracticalScenario> = SqlScenariosData.scenarios

    fun getStats(): LibraryStats {
        return LibraryStats(
            totalItems = allItems.size,
            dataTypesCount = allItems.count { it.category == SqlCategory.DATA_TYPES },
            commandsCount = allItems.count { it.category == SqlCategory.COMMANDS },
            functionsCount = allItems.count { it.category == SqlCategory.FUNCTIONS },
            joinsCount = allItems.count { it.category == SqlCategory.JOINS },
            practicalCount = practicalScenarios.size,
            comparisonsCount = comparisons.size
        )
    }

    fun findById(id: String): SqlCodeItem? {
        return allItems.find { it.id == id }
    }

    fun filterItems(criteria: FilterCriteria, favoriteIds: Set<String>): List<SqlCodeItem> {
        val normalizedQuery = normalize(criteria.query.trim())

        var list = allItems.filter { item ->
            // 1. Database filter
            if (criteria.database != SqlDatabase.ALL && !item.databases.contains(criteria.database)) {
                return@filter false
            }

            // 2. Category filter
            if (criteria.category != null &&
                criteria.category != SqlCategory.OVERVIEW &&
                criteria.category != SqlCategory.FAVORITES &&
                criteria.category != SqlCategory.PRACTICAL &&
                criteria.category != SqlCategory.COMPARATOR
            ) {
                if (item.category != criteria.category) return@filter false
            }

            // 3. Difficulty filter
            if (criteria.difficulty != null && item.difficulty != criteria.difficulty) {
                return@filter false
            }

            // 4. Purpose filter
            if (criteria.purpose != null && item.purpose != criteria.purpose) {
                return@filter false
            }

            // 5. Favorites filter
            if (criteria.onlyFavorites && !favoriteIds.contains(item.id)) {
                return@filter false
            }

            // 6. High compatibility filter (supports 4+ databases or is ALL)
            if (criteria.highCompatibilityOnly && item.databases.size < 4) {
                return@filter false
            }

            // 7. Popular only
            if (criteria.popularOnly && !item.isPopular) {
                return@filter false
            }

            // 8. Search query matching across: Name, Syntax, Description, Keywords, Compatible DBs, SubCategory
            if (normalizedQuery.isNotEmpty()) {
                val matchesName = normalize(item.name).contains(normalizedQuery)
                val matchesSyntax = normalize(item.syntax).contains(normalizedQuery)
                val matchesDescription = normalize(item.description).contains(normalizedQuery)
                val matchesSubCategory = normalize(item.subcategory.displayName).contains(normalizedQuery)
                val matchesKeywords = item.keywords.any { normalize(it).contains(normalizedQuery) }
                val matchesDb = item.databases.any { normalize(it.displayName).contains(normalizedQuery) || normalize(it.shortName).contains(normalizedQuery) }

                if (!matchesName && !matchesSyntax && !matchesDescription && !matchesSubCategory && !matchesKeywords && !matchesDb) {
                    return@filter false
                }
            }

            true
        }

        // Apply Sorting
        list = when (criteria.sortOption) {
            SortOption.RELEVANCE -> {
                if (normalizedQuery.isEmpty()) {
                    list.sortedByDescending { it.isPopular }
                } else {
                    list.sortedWith(
                        compareByDescending<SqlCodeItem> { normalize(it.name) == normalizedQuery }
                            .thenByDescending { normalize(it.name).startsWith(normalizedQuery) }
                            .thenByDescending { it.isPopular }
                    )
                }
            }
            SortOption.NAME_ASC -> list.sortedBy { it.name.lowercase() }
            SortOption.NAME_DESC -> list.sortedByDescending { it.name.lowercase() }
            SortOption.DIFFICULTY_ASC -> list.sortedBy { it.difficulty.level }
            SortOption.DIFFICULTY_DESC -> list.sortedByDescending { it.difficulty.level }
        }

        return list
    }

    private fun normalize(text: String): String {
        val temp = Normalizer.normalize(text, Normalizer.Form.NFD)
        return Regex("\\p{InCombiningDiacriticalMarks}+").replace(temp, "").lowercase()
    }
}
