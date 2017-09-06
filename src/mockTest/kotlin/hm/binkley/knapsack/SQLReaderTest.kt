package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Test

internal class SQLReaderTest {
    @Test
    fun shouldSkipComments() {
        val reader = SQLReader("test-comments")

        assert.that(reader.lines(), equalTo(listOf("LINE ONE", "LINE TWO")))
    }

    @Test
    fun shouldSkipBlankLines() {
        val reader = SQLReader("test-blank-lines")

        assert.that(reader.lines(), equalTo(listOf("LINE ONE", "LINE TWO")))
    }

    @Test
    fun shouldTrimWhitespace() {
        val reader = SQLReader("test-whitespace")

        assert.that(reader.lines(), equalTo(listOf("LINE ONE", "LINE TWO")))
    }

    @Test
    fun shouldCatenate() {
        val reader = SQLReader("test-multiline")

        assert.that(reader.lines(), equalTo(listOf("LINE ONE", "LINE TWO")))
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldComplainAtUnterminated() {
        SQLReader("test-unterminated").lines()
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldComplainAtMultiline() {
        SQLReader("test-single-line").oneLine()
    }
}
