import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GasStation(
    val id: Int,
    val name: String,
    val address: Address, // Address will be updated with country manually
    val prices: List<Price> = emptyList(),
    val price: Double? = null,
    val hasTriggerCreated: Boolean = false
)

@JsonClass(generateAdapter = true)
data class Address(
    val line1: String,
    val locality: String,
    val region: String,
    val postalCode: String,
    var countryCode: String = ""  // Default empty, to be set manually
)

@JsonClass(generateAdapter = true)
data class Price(
    val credit: PriceDetail? = null,
    val cash: PriceDetail? = null
)

@JsonClass(generateAdapter = true)
data class PriceDetail(
    val price: Double = 0.0,
    val formattedPrice: String? = null
)

