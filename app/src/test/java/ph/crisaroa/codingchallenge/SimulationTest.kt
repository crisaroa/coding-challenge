package ph.crisaroa.codingchallenge

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SimulationTest {

    @Test
    fun testInvalidInput() {
        assertFailsWith<IllegalArgumentException> { Simulation(-1, 2, 3, 4, 5) }
        assertFailsWith<IllegalArgumentException> { Simulation(1, 2, 3, 4, 0) }
        assertFailsWith<IllegalArgumentException> { Simulation(1, 2, 3, -1, 5) }
    }

    @Test
    fun testPickColors() {
        val simulation = Simulation(9, 3, 3, 3, 1)
        val pickedColors = simulation.pickColors()
        assertEquals(3, pickedColors.size)
    }

    @Test
    fun testRepeatPickColors() {
        val simulation = Simulation(9, 3, 3, 3, 10)
        val average = simulation.repeatPickColors()
        assertEquals(2.3, average, 0.2)
    }

    @Test
    fun testGenerateBounds() {
        val simulation = Simulation(9, 3, 3, 3, 10)
        simulation.repeatPickColors()
        val bounds = simulation.generateBounds(0.95, 2.3)
        assertEquals(1.93, bounds.first, 0.2)
        assertEquals(2.67, bounds.second, 0.2)
    }

    @Test
    fun testInvalidConfidence() {
        val simulation = Simulation(9, 3, 3, 3, 10)
        simulation.repeatPickColors()
        assertFailsWith<IllegalArgumentException> { simulation.generateBounds(0.8, 2.9) }
    }
}