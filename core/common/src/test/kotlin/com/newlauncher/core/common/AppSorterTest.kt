package com.newlauncher.core.common

import android.graphics.drawable.Drawable
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class AppSorterTest {

    private val icon: Drawable = mockk(relaxed = true)

    private fun app(label: String, pkg: String = "com.$label") =
        AppInfo(packageName = pkg, className = "MainActivity", label = label, icon = icon)

    @Test
    fun `byLabel sorts case-insensitively`() {
        val apps = listOf(app("zeta"), app("Alpha"), app("beta"))
        assertEquals(listOf("Alpha", "beta", "zeta"), AppSorter.byLabel(apps).map { it.label })
    }

    @Test
    fun `filter matches label`() {
        val apps = listOf(app("Camera"), app("Chrome"), app("Calculator"))
        assertEquals(listOf("Calculator"), AppSorter.filter(apps, "calc").map { it.label })
    }

    @Test
    fun `filter matches package name`() {
        val apps = listOf(app("Notes", pkg = "com.example.notes"))
        assertEquals(1, AppSorter.filter(apps, "example.notes").size)
    }

    @Test
    fun `blank query returns all`() {
        val apps = listOf(app("A"), app("B"))
        assertEquals(2, AppSorter.filter(apps, "   ").size)
    }

    @Test
    fun `search combines filter and sort`() {
        val apps = listOf(app("zeta"), app("Alpha"), app("beta"))
        assertEquals(
            listOf("Alpha", "beta"),
            AppSorter.search(apps, "a").map { it.label }
        )
    }

    @Test
    fun `sectionByLetter buckets by first letter`() {
        val apps = listOf(app("Apple"), app("banana"), app("123go"), app("Apricot"))
        val sections = AppSorter.sectionByLetter(apps)
        assertEquals(listOf("Apple", "Apricot"), sections["A"]!!.map { it.label })
        assertEquals(listOf("banana"), sections["B"]!!.map { it.label })
        assertEquals(listOf("123go"), sections["#"]!!.map { it.label })
    }

    @Test
    fun `app key is stable`() {
        val a = app("X", pkg = "com.x")
        assertEquals("com.x/MainActivity", a.key)
    }
}
