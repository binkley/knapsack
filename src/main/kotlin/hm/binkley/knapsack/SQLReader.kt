package hm.binkley.knapsack

import java.util.ArrayList
import java.util.regex.Pattern

class SQLReader(private val purpose: String) {
    fun lines() = catenateSql(rawLines())

    fun oneLine() = oneLineOnly(lines())

    private fun oneLineOnly(lines: List<String>): String {
        if (1 != lines.size) {
            throw IllegalArgumentException(
                    "Must have exactly one line in '$purpose':\n$lines")
        }
        return lines[0]
    }

    private fun catenateSql(rawLines: List<String>): List<String> {
        val lines = ArrayList<String>(rawLines.size)
        val buffer = StringBuilder()
        for (rawLine in rawLines.map { it.trim() }) {
            if (rawLine.isEmpty()) {
                continue
            }
            if (COMMENTS.matcher(rawLine).find()) {
                continue
            }
            val matcher = TERMINATING_SEMICOLON.matcher(rawLine)
            if (!matcher.find()) {
                buffer.append(' ').append(rawLine)
                continue
            }
            buffer.append(' ').append(rawLine.substring(0, matcher.start()))
            lines.add(buffer.toString())
            buffer.setLength(0)
        }

        if (buffer.isNotEmpty())
            throw IllegalArgumentException(
                    "Unterminated SQL in: $purpose: $buffer")

        return lines
    }

    private fun rawLines()
            = javaClass.
            getResource("/hm/binkley/knapsack/knapsack-$purpose.sql").
            readText().
            lines()

    companion object {
        private val TERMINATING_SEMICOLON = Pattern.compile("; *$")
        private val COMMENTS = Pattern.compile("^--")
    }
}
