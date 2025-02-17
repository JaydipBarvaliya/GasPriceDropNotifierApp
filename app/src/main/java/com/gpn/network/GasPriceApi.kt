package com.gpn.network

import com.gpn.data.GasStation
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

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
    fun findByCityOrZipcode(
        @Header("search") search: String,
        @Header("fuel") fuel: Int,
        @Header("maxAge") maxAge: Int,
        @Header("brandId") brandId: String = ""
    ): Call<FindByCityOrZipcodeResponse>
}
