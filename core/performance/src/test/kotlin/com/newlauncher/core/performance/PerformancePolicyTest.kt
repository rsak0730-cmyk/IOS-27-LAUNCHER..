package com.newlauncher.core.performance

import org.junit.Assert.assertEquals
import org.junit.Test

class PerformancePolicyTest {

    private fun signals(
        avg: Float = 16f,
        p95: Float = 20f,
        mem: Float = 0.6f,
        thermal: PerformancePolicy.ThermalStatus = PerformancePolicy.ThermalStatus.NOMINAL,
        battery: Float = 0.8f,
    ) = PerformancePolicy.Signals(avg, p95, mem, thermal, battery)

    @Test
    fun `healthy signals permit ultra`() {
        assertEquals(
            GraphicsProfile.ULTRA,
            PerformancePolicy.allowedBySignals(signals(avg = 15f, p95 = 18f))
        )
    }

    @Test
    fun `high thermal forces performance`() {
        assertEquals(
            GraphicsProfile.PERFORMANCE,
            PerformancePolicy.allowedBySignals(
                signals(thermal = PerformancePolicy.ThermalStatus.HIGH)
            )
        )
    }

    @Test
    fun `severe thermal forces performance`() {
        assertEquals(
            GraphicsProfile.PERFORMANCE,
            PerformancePolicy.allowedBySignals(
                signals(thermal = PerformancePolicy.ThermalStatus.SEVERE)
            )
        )
    }

    @Test
    fun `slow frames force performance`() {
        assertEquals(
            GraphicsProfile.PERFORMANCE,
            PerformancePolicy.allowedBySignals(signals(avg = 30f, p95 = 50f))
        )
    }

    @Test
    fun `borderline frames cap at balanced`() {
        assertEquals(
            GraphicsProfile.BALANCED,
            PerformancePolicy.allowedBySignals(signals(avg = 22f, p95 = 30f))
        )
    }

    @Test
    fun `low battery caps at balanced`() {
        assertEquals(
            GraphicsProfile.BALANCED,
            PerformancePolicy.allowedBySignals(signals(battery = 0.1f))
        )
    }

    @Test
    fun `low memory forces performance`() {
        assertEquals(
            GraphicsProfile.PERFORMANCE,
            PerformancePolicy.allowedBySignals(signals(mem = 0.05f))
        )
    }

    @Test
    fun `decide never exceeds the user target`() {
        // Target ULTRA, but thermal pressure -> effective capped at PERFORMANCE.
        val effective = PerformancePolicy.decide(
            target = GraphicsProfile.ULTRA,
            signals = signals(thermal = PerformancePolicy.ThermalStatus.HIGH)
        )
        assertEquals(GraphicsProfile.PERFORMANCE, effective)
    }

    @Test
    fun `decide equals target when signals allow it`() {
        val effective = PerformancePolicy.decide(
            target = GraphicsProfile.BALANCED,
            signals = signals(avg = 15f, p95 = 18f, battery = 0.8f)
        )
        assertEquals(GraphicsProfile.BALANCED, effective)
    }

    @Test
    fun `decide with performance target stays performance`() {
        // Even with perfect signals, a PERFORMANCE target is never exceeded.
        val effective = PerformancePolicy.decide(
            target = GraphicsProfile.PERFORMANCE,
            signals = signals(avg = 10f, p95 = 12f, battery = 1f)
        )
        assertEquals(GraphicsProfile.PERFORMANCE, effective)
    }
}
