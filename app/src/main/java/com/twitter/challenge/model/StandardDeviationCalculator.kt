package com.twitter.challenge.model

import kotlin.math.pow
import kotlin.math.sqrt

fun List<Double>.calculateStandardDeviation(): Double {
    val mean = calculateMean()
    val stdDevSquared = sumOf { (it to mean).calculateDiffSquared() } / size
    return sqrt(stdDevSquared)
}

fun List<Double>.calculateMean(): Double = sum() / size

fun Pair<Double, Double>.calculateDiffSquared(): Double {
    return (first - second).pow(2)
}