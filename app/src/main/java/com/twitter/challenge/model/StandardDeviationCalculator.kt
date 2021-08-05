package com.twitter.challenge.model

import kotlin.math.pow
import kotlin.math.sqrt

fun List<Double>.calculateStandardDeviation(): Double {
    if (isEmpty()) return 0.0
    val mean = average()
    val stdDevSquared = sumOf { (it to mean).calculateDiffSquared() } / (size - 1)
    return sqrt(stdDevSquared)
}

fun Pair<Double, Double>.calculateDiffSquared(): Double = (first - second).pow(2)