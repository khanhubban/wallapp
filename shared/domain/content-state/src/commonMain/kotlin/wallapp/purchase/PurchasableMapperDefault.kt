package wallapp.purchase

import wallapp.billing.sku.BillingSku
import wallapp.data.purchase.Purchasable
import wallapp.pricing.PricingMapper
import wallapp.resources.string.StringRepository

class PurchasableMapperDefault(
    private val stringRepository: StringRepository,
) : PurchasableMapper {

    override fun mapPurchasableSubscriptionUnlimitedMonthly(
        monthlyBillingSku: BillingSku,
    ): Purchasable.SubscriptionUnlimitedMonthly {
        val pricePerMonth = stringRepository.plusPricePerMonth(monthlyBillingSku.priceLocalized)

        return Purchasable.SubscriptionUnlimitedMonthly(
            billingSku = monthlyBillingSku,
            pricePerMonthLabel = pricePerMonth,
        )
    }

    override fun mapPurchasableSubscriptionUnlimitedAnnual(
        annualBillingSku: BillingSku,
        plusUnlimitedMonthlyPurchasable: Purchasable.SubscriptionUnlimitedMonthly?,
    ): Purchasable.SubscriptionUnlimitedAnnual {
        val pricePerYear = stringRepository
            .plusPricePerYear(annualBillingSku.priceLocalized)

        val pricePerMonthLocalized = stringRepository.price(
            currencyCode = annualBillingSku.priceCurrencyCode,
            amount = PricingMapper.getPlusPriceAnnualPerMonth(
                annualBillingSku.priceNumerical,
            ).toString()
        )

        val pricePerMonth = stringRepository.plusPricePerMonth(pricePerMonthLocalized)

//        val annualDiscount = plusMonthlyPurchasable?.let {
//            stringRepository.plusPricePerYearHighlight(
//                PricingMapper.getPlusAnnualDiscount(
//                    plusAnnualPrice = annualBillingSku.priceNumerical,
//                    plusMonthlyPrice = plusMonthlyPurchasable.billingSku.priceNumerical,
//                )
//            )
//        }

        return Purchasable.SubscriptionUnlimitedAnnual(
            billingSku = annualBillingSku,
            pricePerYearLabel = pricePerYear,
            pricePerMonthLabel = pricePerMonth,
            annualDiscountLabel = null,
        )
    }

    override fun mapPurchasableSubscriptionAdFreeMonthly(
        adFreeMonthlyBillingSku: BillingSku
    ): Purchasable.SubscriptionStandardMonthly {
        val pricePerMonth = stringRepository.plusPricePerMonth(adFreeMonthlyBillingSku.priceLocalized)

        return Purchasable.SubscriptionStandardMonthly(
            billingSku = adFreeMonthlyBillingSku,
            pricePerMonthLabel = pricePerMonth,
        )
    }

}