package hm.binkley.knapsack

import java.io.FileNotFoundException
import java.net.URL
import java.util.ArrayList
import java.util.regex.Pattern

class SQLReader(private val purpose: String) {
    fun lines() = catenateSql(purpose, rawLines())

    fun oneLine() = lines().single()

    private fun rawLines() = source(purpose).readText().lines()

    companion object {
        private val TERMINATING_SEMICOLON = Pattern.compile("\\s*;\\s*$")
        private val COMMENTS = Pattern.compile("^--")

        private fun String.isComment() = COMMENTS.matcher(this).find()
        private fun String.matchEnd() = TERMINATING_SEMICOLON.matcher(this)

        private infix operator fun <T> StringBuilder.plusAssign(text: T) {
            append(text)
        }

        private fun source(purpose: String): URL {
            val source = "/hm/binkley/knapsack/knapsack-$purpose.sql"
            return javaClass.getResource(source)
                    ?: throw FileNotFoundException(source)
        }

        private fun catenateSql(purpose: String, rawLines: List<String>):
                List<String> {
            val lines = ArrayList<String>()
            val buffer = StringBuilder()
            for (line in rawLines.
                    map { it.trim() }.
                    filter { !it.isEmpty() }.
                    filter { !it.isComment() }) {
                buffer += ' '

                val matcher = line.matchEnd()
                if (!matcher.find()) {
                    buffer += line
                    continue
                }

                buffer += line.substring(0, matcher.start())
                lines.add(buffer.trim().toString())
                buffer.setLength(0)
            }

            if (buffer.isEmpty())
                return lines

            throw IllegalArgumentException(
                    "Unterminated SQL (no ending semicolon) in: $purpose: $buffer")
        }
    }
}
