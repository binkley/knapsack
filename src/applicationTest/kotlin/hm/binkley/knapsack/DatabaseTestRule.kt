package hm.binkley.knapsack

import hm.binkley.knapsack.Database.Companion.test
import org.junit.rules.ExternalResource

class DatabaseTestRule : ExternalResource() {
    private lateinit var _loader: SQLLoader

    val loader
        get() = _loader

    override fun before() {
        _loader = SQLLoader(test())
        _loader.loadSchema()
    }

    override fun after() {
        _loader.close()
    }

    fun reset() = object : ExternalResource() {
        override fun after() {
            _loader.reset()
        }
    }
}
