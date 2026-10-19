package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.SqlCategory
import com.example.data.model.SqlDatabase
import com.example.data.repository.FilterCriteria
import com.example.data.repository.PreferencesManager
import com.example.data.repository.SqlRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SQL Code Finder", appName)
    }

    @Test
    fun `sql repository contains at least 60 authentic records`() {
        val repository = SqlRepository()
        val allItems = repository.allItems
        assertTrue("Expected at least 60 records, found ${allItems.size}", allItems.size >= 60)
    }

    @Test
    fun `search for VARCHAR returns matching records`() {
        val repository = SqlRepository()
        val results = repository.filterItems(FilterCriteria(query = "VARCHAR"), emptySet())
        assertTrue("Expected results for VARCHAR", results.isNotEmpty())
        assertTrue(results.any { it.name.contains("VARCHAR", ignoreCase = true) })
    }

    @Test
    fun `filter by MySQL and Tipos de dados simultaneously`() {
        val repository = SqlRepository()
        val results = repository.filterItems(
            FilterCriteria(
                database = SqlDatabase.MYSQL,
                category = SqlCategory.DATA_TYPES
            ),
            emptySet()
        )
        assertTrue("Expected records matching MySQL and DATA_TYPES", results.isNotEmpty())
        results.forEach { item ->
            assertEquals(SqlCategory.DATA_TYPES, item.category)
            assertTrue(item.databases.contains(SqlDatabase.MYSQL))
        }
    }

    @Test
    fun `preferences manager correctly persists and toggles favorites`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = PreferencesManager(context)

        val testId = "dt-varchar"
        val initialIsFav = prefs.isFavorite(testId)

        val newStatus = prefs.toggleFavorite(testId)
        assertEquals(!initialIsFav, newStatus)
        assertEquals(newStatus, prefs.isFavorite(testId))

        // Toggle back
        prefs.toggleFavorite(testId)
        assertEquals(initialIsFav, prefs.isFavorite(testId))
    }

    @Test
    fun `practical scenarios and comparisons are loaded`() {
        val repository = SqlRepository()
        assertTrue("Expected 10 practical scenarios", repository.practicalScenarios.size >= 10)
        assertTrue("Expected dialect comparisons", repository.comparisons.isNotEmpty())
    }
}
