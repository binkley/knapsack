package hm.binkley.knapsack

import org.hamcrest.Matchers
import org.junit.Assert

internal infix fun <T> T.`is`(expected: T)
        = Assert.assertThat(this,
        Matchers.`is`(expected))
