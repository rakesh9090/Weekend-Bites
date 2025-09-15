import com.demo.lib.Holding
import com.demo.lib.PortfolioCalculator
import org.junit.Assert.*
import org.junit.Test

class PortfolioCalculatorTest {

    private val calculator = PortfolioCalculator()

    private val sampleHoldings = listOf(
        Holding("ABC", 10, 120.0, 100.0, 115.0),
        Holding("XYZ", 5, 200.0, 180.0, 190.0)
    )


    @Test
    fun `current value is correct`() {
        val result = calculator.currentValue(sampleHoldings)
        assertEquals(10 * 120.0 + 5 * 200.0, result, 0.001)
    }

    @Test
    fun `total investment is correct`() {
        val result = calculator.totalInvestment(sampleHoldings)
        assertEquals(10 * 100.0 + 5 * 180.0, result, 0.001)
    }

    @Test
    fun `total PNL with profit holdings`() {
        val result = calculator.totalPNL(sampleHoldings)
        assertEquals(
            calculator.currentValue(sampleHoldings) - calculator.totalInvestment(sampleHoldings),
            result,
            0.001
        )
    }

    @Test
    fun `today's PNL is correct`() {
        val result = calculator.todaysPNL(sampleHoldings)
        val expected = (115.0 - 120.0) * 10 + (190.0 - 200.0) * 5
        assertEquals(expected, result, 0.001)
    }


    @Test
    fun `handles empty holdings`() {
        assertEquals(0.0, calculator.currentValue(emptyList<Holding>()), 0.001)
        assertEquals(0.0, calculator.totalInvestment(emptyList<Holding>()), 0.001)
        assertEquals(0.0, calculator.totalPNL(emptyList<Holding>()), 0.001)
        assertEquals(0.0, calculator.todaysPNL(emptyList<Holding>()), 0.001)
    }

    @Test
    fun `calculate summary shows correct numeric values`() {
        val summary = calculator.calculateSummary(sampleHoldings)

        val expectedCurrentValue = calculator.currentValue(sampleHoldings)
        val expectedInvestment = calculator.totalInvestment(sampleHoldings)
        val expectedTotalPnl = calculator.totalPNL(sampleHoldings)
        val expectedTodayPnl = calculator.todaysPNL(sampleHoldings)
        val expectedPercent = (expectedTotalPnl / expectedInvestment) * 100

        fun extractNumber(s: String): Double {
            return s.replace("[₹,]".toRegex(), "")
                .split(" ").first()
                .toDouble()
        }

        assertEquals(expectedCurrentValue, extractNumber(summary.currentValue), 0.01)
        assertEquals(expectedInvestment, extractNumber(summary.totalInvestment), 0.01)
        assertEquals(expectedTotalPnl, extractNumber(summary.totalPnl), 0.01)
        assertEquals(expectedTodayPnl, extractNumber(summary.todayPnl), 0.01)
        assertTrue(summary.totalPnl.contains(String.format("%.2f", expectedPercent)))
    }

}
