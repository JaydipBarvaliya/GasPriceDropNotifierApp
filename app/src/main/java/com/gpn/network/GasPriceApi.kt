package com.gpn.network

import BrandResponse
import GasStation
import retrofit2.http.GET
import retrofit2.http.Query

data class FindByCityOrZipcodeResponse(
    val data: DataNode?
)

data class DataNode(
    val locationBySearchTerm: LocationBySearchTerm?
)

data class LocationBySearchTerm(
    val stations: Stations?
)

data class Stations(
    val results: List<GasStation>?
)

interface GasPriceApi {
    @GET("findByCityOrZipcode")
    suspend fun findByCityOrZipcode(
        @Query("search") search: String,
        @Query("fuel") fuel: Int,
        @Query("maxAge") maxAge: Int,
        @Query("brandId") brandId: String = ""
    ): FindByCityOrZipcodeResponse

    @GET("getBrands")
    suspend fun getBrands(): BrandResponse
}
