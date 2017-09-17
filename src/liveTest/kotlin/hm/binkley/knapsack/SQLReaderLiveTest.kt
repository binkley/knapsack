package hm.binkley.knapsack

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.io.FileNotFoundException

internal class SQLReaderLiveTest {
    @Rule
    @JvmField
    internal val thrown = ExpectedException.none()

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

    @Test
    fun shouldComplainAtUnterminated() {
        thrown.expect(IllegalArgumentException::class.java)

        SQLReader("test-unterminated").lines()
    }

    @Test
    fun shouldComplainAtMultiline() {
        thrown.expect(IllegalArgumentException::class.java)

        SQLReader("test-single-line").oneLine()
    }

    @Test
    fun shouldBeNicerAboutMissingResources() {
        thrown.expect(FileNotFoundException::class.java)
        thrown.expectMessage("no-such-file")

        SQLReader("no-such-file").lines()
    }
}
