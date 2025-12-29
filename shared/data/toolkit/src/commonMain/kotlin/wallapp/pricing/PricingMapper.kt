package wallapp.pricing

import wallapp.math.round

object PricingMapper {

    fun getPlusPriceAnnualPerMonth(
        plusAnnualPrice: Float,
    ): Float {
        return (plusAnnualPrice / 12f).round(2)
    }

    fun getPlusAnnualDiscount(
        plusAnnualPrice: Float,
        plusMonthlyPrice: Float,
    ): Int {
        return ((1 - (plusAnnualPrice / (plusMonthlyPrice * 12))) * 100).toInt()
    }
}