package com.gpn.network

import GasStation
import com.gpn.viewmodel.BrandResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class FindByCityOrZipcodeResponse(
    val data: DataNode?
)

data class DataNode(
    val locationBySearchTerm: LocationBySearchTerm?
)

data class LocationBySearchTerm(
    val stations: Stations?,
    val countryCode: String?
)

data class Stations(
    val results: List<GasStation>?
)

data class PriceAlertRequest(
    val stationId: Int,
    val fuelType: Int,
    val expectedPrice: Float,
    val line1: String,
    val locality : String,
    val postalCode : String,
    val region: String,
    val countryCode: String,
    val gasStationBrand: String,
    val userId: String,
    val userEmail: String,
    val pushNotification: Boolean,
)

data class PriceAlertResponse(
    val success: Boolean,
    val message: String
)

data class Alert(
    val id: Long,
    val stationId: Int,
    val fuelType: Int,
    val expectedPrice: Double,
    val pushNotification: Boolean?,
    val email: String?,
    val line1: String?,
    val locality: String?,
    val postalCode: String?,
    val region: String?,
    val countryCode: String?,
    val gasStationBrand: String?
){
    fun formattedAddress(): String {
        return listOfNotNull(line1, locality, region, countryCode).joinToString(", ")
    }
}

interface GasPriceApi {
    @GET("findByCityOrZipcode")
    suspend fun findByCityOrZipcode(
        @Query("search") search: String,
        @Query("fuel") fuel: Int,
        @Query("maxAge") maxAge: Int,
        @Query("brandId") brandId: String? = null
    ): FindByCityOrZipcodeResponse

    @GET("getBrands")
    suspend fun getBrands(): BrandResponse

    @POST("createAlert")
    suspend fun createPriceAlert(@Body request: PriceAlertRequest): PriceAlertResponse

    @PUT("updateAlert")
    suspend fun updateAlert(@Body alert: Alert): Response<List<Alert>>

    @GET("/getAlerts")
    suspend fun getAlerts(): List<Alert>

    @DELETE("deleteAlert/{id}")
    suspend fun deleteAlert(@Path("id") alertId: Long): Response<List<Alert>>

    @DELETE("/deleteAllAlerts")
    suspend fun deleteAllAlerts(): Response<ResponseBody>

}
