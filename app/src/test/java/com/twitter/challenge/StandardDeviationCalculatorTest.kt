package com.twitter.challenge

import com.twitter.challenge.model.calculateDiffSquared
import com.twitter.challenge.model.calculateStandardDeviation
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.Test

class StandardDeviationCalculatorTest {

    companion object {
        const val OFFSET = 0.0000001
    }

    @Test
    fun `test diff squared`() {
        assertThat(4.0).isEqualTo((1.0 to 3.0).calculateDiffSquared())
        assertThat(25.0).isEqualTo((2.0 to 7.0).calculateDiffSquared())
        assertThat(0.0).isEqualTo((10.0 to 10.0).calculateDiffSquared())
        assertThat(100.0).isEqualTo((-5.0 to 5.0).calculateDiffSquared())
        assertThat(49.0).isEqualTo((8.0 to 1.0).calculateDiffSquared())
        assertThat(0.0).isEqualTo((0.0 to 0.0).calculateDiffSquared())
        assertThat(25.0).isEqualTo((-10.0 to -5.0).calculateDiffSquared())
    }

    @Test
    fun `when list is empty, then standard deviation is zero`() {
        assertThat(0.0).isEqualTo(emptyList<Double>().calculateStandardDeviation())
    }

    @Test
    fun `test standard deviation for following lists`() {
        assertThat(5.2372293656638).isEqualTo(
            listOf(10.0,12.0,23.0,23.0,16.0,23.0,21.0,16.0).calculateStandardDeviation(),
            Offset.offset(OFFSET)
        )
        assertThat(3.8655335983535313).isEqualTo(
            listOf(16.83,11.15,19.19,14.20,9.88).calculateStandardDeviation(),
            Offset.offset(OFFSET)
        )
        assertThat(5.224940191).isEqualTo(
            listOf(-5.0, 1.0, 8.0, 7.0, 2.0).calculateStandardDeviation(),
            Offset.offset(OFFSET)
        )
        assertThat(3.6193922141707).isEqualTo(
            listOf(9.0, 2.0, 5.0, 4.0, 12.0, 7.0).calculateStandardDeviation(),
            Offset.offset(OFFSET)
        )
        assertThat(44.732006225435).isEqualTo(
            listOf(25.0, -100.0, 23.0, 23.0, 10.0, 15.0, 0.0).calculateStandardDeviation(),
            Offset.offset(OFFSET)
        )
    }
}