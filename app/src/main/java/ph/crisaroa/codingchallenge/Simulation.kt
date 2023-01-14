package ph.crisaroa.codingchallenge

import java.util.*
import kotlin.math.sqrt

class Simulation(i: Int, j: Int, k: Int, m: Int, n: Int) {
    private var totalBulbs = i
    private var bulbColors = j
    private var bulbCountPerColor = k
    private var bulbsToRemove = m
    private var simulationRuns = n
    private lateinit var counts: MutableList<Int>

    init {
        if (bulbsToRemove > totalBulbs) {
            throw IllegalArgumentException("Invalid input: Number of bulbs to remove cannot be greater than total number of bulbs.")
        }
        if (bulbsToRemove <= 0) {
            throw IllegalArgumentException("Invalid input: Number of bulbs to remove must be greater than 0.")
        }
        if (simulationRuns <= 0) {
            throw IllegalArgumentException("Invalid input: Number of simulation runs must be greater than 0.")
        }
    }

    //
    fun pickColors(): List<Int> {
        val random = Random()
        /* Create a range of integers from 1 to 'totalBulbs' (inclusive)
        * Then apply a transformation to each element of the range by subtracting 1 from the element,
        * taking the remainder of the element divided by 'bulbColors', and then adding 1. This will
        * result in a new list where each element is a number between 1 and 'bulbColors*/
        val bucket = (1..totalBulbs).map { (it - 1) % bulbColors + 1 }.toMutableList()
        /* Create a range of integers from 1 to 'bulbsToRemove' (inclusive)
        * Apply a transformation to each element of the range by selecting a random index from the
        * 'bucket' list and then removing the element at that index from the 'bucket' list and
        * returning it*/
        return (1..bulbsToRemove).map { bucket.removeAt(random.nextInt(bucket.size)) }
    }

    fun repeatPickColors(): Double {
        counts = mutableListOf()
        for (sim in 1..simulationRuns) {
            counts.add(pickColors().toSet().size)
        }
        return counts.average()
    }

    fun generateBounds(
        confidence: Double,
        mean: Double
    ): Pair<Double, Double> {
        /* Define a variable multiplier that is assigned a value based on the value of the variable confidence.*/
        val multiplier = when (confidence) {
            0.9 -> 1.645
            0.95 -> 1.96
            0.99 -> 2.576
            else -> throw IllegalArgumentException("Invalid confidence level")
        }
        // Calculate the standard deviation of a list of numbers, represented by the variable counts.
        val stdDev = sqrt(counts.map { (it - mean) * (it - mean) }.average())
        /*Calculate the confidence interval by multiplying the 'multiplier' and the standard
        deviation 'stdDev' and dividing the result by the square root of the number of simulation
        runs 'simulationRuns'*/
        val interval = multiplier * stdDev / sqrt(simulationRuns.toDouble())
        return Pair(mean - interval, mean + interval)
    }
}