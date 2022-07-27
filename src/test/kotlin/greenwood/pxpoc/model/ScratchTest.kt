package greenwood.pxpoc.model

import greenwood.common.client.galileo.toUSDMoney
import org.javamoney.moneta.Money
import org.junit.jupiter.api.Test

class ScratchTest {

    @Test
    fun `Money test`() {
        val money = Money.of(123.00, "USD")
        println("Money: ${money.toString()}")
        println("Money: ${money}")
        println("Money: ${money.numberStripped}")
        println("Money: ${money.number}")



    }
}