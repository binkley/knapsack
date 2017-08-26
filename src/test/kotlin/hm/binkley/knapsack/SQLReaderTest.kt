package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

internal class SQLReaderTest {
    @Rule
    @JvmField
    public val thrown = ExpectedException.none()

    @Test
    fun shouldSkipComments() {
        val reader = SQLReader("test-comments")

        assert.that(reader.lines().size, equalTo(2))
    }

    @Test
    fun shouldSkipBlankLines() {
        val reader = SQLReader("test-blank-lines")

        assert.that(reader.lines().size, equalTo(2))
    }

    @Test
    fun shouldTrimWhitespace() {
        val reader = SQLReader("test-whitespace")

        assert.that(reader.lines().size, equalTo(2))
    }

    @Test
    fun shouldCatenate() {
        val reader = SQLReader("test-multiline")

        assert.that(reader.lines().size, equalTo(2))
    }

    @Test
    fun shouldComplainAtUnterminated() {
        thrown.expect(IllegalArgumentException::class.java)
        thrown.expectMessage("test-unterminated")
        thrown.expectMessage("LINE TWO")

        val reader = SQLReader("test-unterminated")

        reader.lines()
    }

    @Test
    fun shouldComplainAtMultiline() {
        thrown.expect(IllegalArgumentException::class.java)
        thrown.expectMessage("test-single-line")
        thrown.expectMessage("LINE ONE")
        thrown.expectMessage("LINE TWO")

        val reader = SQLReader("test-single-line")

        reader.oneLine()
    }
}
