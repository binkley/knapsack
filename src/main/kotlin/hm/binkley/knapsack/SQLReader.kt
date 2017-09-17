package hm.binkley.knapsack

import java.io.FileNotFoundException
import java.util.ArrayList
import java.util.regex.Pattern

class SQLReader(private val purpose: String) {
    fun lines() = catenateSql(rawLines)

    fun oneLine() = lines().single()

    private fun catenateSql(rawLines: List<String>): List<String> {
        val lines = ArrayList<String>()
        val buffer = StringBuilder()
        for (rawLine in rawLines.
                map { it.trim() }.
                filter { !it.isEmpty() }.
                filter { !it.isComment() }) {
            buffer += ' '

            val matcher = rawLine.matchEnd()
            if (!matcher.find()) {
                buffer += rawLine
                continue
            }

            buffer += rawLine.substring(0, matcher.start())
            lines.add(buffer.trim().toString())
            buffer.setLength(0)
        }

        if (buffer.isNotEmpty())
            throw IllegalArgumentException(
                    "Unterminated SQL in: $purpose: $buffer")

        return lines
    }

    private val rawLines: List<String>
        get() {
            val source = "/hm/binkley/knapsack/knapsack-$purpose.sql"
            val resource = javaClass.getResource(source)
                    ?: throw FileNotFoundException(source)
            return resource.
                    readText().
                    lines()
        }

    companion object {
        private val TERMINATING_SEMICOLON = Pattern.compile("\\s*;\\s*$")
        private val COMMENTS = Pattern.compile("^--")

        private fun String.isComment() = COMMENTS.matcher(this).find()
        private fun String.matchEnd() = TERMINATING_SEMICOLON.matcher(this)

        private infix operator fun <T> StringBuilder.plusAssign(text: T) {
            append(text)
        }
    }
}
