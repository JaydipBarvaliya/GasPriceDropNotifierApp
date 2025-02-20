data class GasStation(
    val id: Int,
    val name: String,
    val address: Address,
    val prices: List<Price> = emptyList(),
    val price: Double,
    val hasTriggerCreated: Boolean = false
)

data class Address(
    val line1: String,
    val locality: String,
    val region: String,
    val postalCode: String,
    val country: String = "US"
) {
    fun formattedAddress() = "$line1, $locality, $region $postalCode, $country"
}

data class Price(
    val credit: PriceDetail = PriceDetail(),
    val cash: PriceDetail = PriceDetail()
) {
    fun bestPrice() = minOf(credit.price, cash.price)
}

data class PriceDetail(val price: Double = 0.0) {
    val formattedPrice: String
        get() = "$${"%.2f".format(price)}"
}