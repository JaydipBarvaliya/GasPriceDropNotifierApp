package com.gpn.data

data class GasStation(
    val id: Int,
    val name: String,
    val address: Address,
    val prices: List<Price>?,
    val hasTriggerCreated: Boolean = false
)

data class Address(
    val line1: String,
    val locality: String,
    val region: String,
    val postalCode: String
)

data class Price(
    val credit: PriceDetail? = null,
    val cash: PriceDetail? = null
)

data class PriceDetail(
    val price: Double,
    val formattedPrice: String
)
