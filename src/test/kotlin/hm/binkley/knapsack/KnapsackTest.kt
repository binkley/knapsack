package hm.binkley.knapsack

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class KnapsackTest {
    @Mock private lateinit var database: Database
    @InjectMocks private lateinit var knapsack: Knapsack

    @Test
    fun shouldClose() {
        knapsack.close()

        verify(database, times(1)).close()
    }
}
