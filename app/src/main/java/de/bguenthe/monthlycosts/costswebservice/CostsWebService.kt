package de.bguenthe.monthlycosts.costswebservice

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.time.LocalDateTime


data class CostsDTO(
        val id: Long = 0,
        val type: String = "",
        val recordDateTime: String,
        val costs: Double = 0.0,
        val uniqueid: String = "",
        val comment: String = "",
        val deleted: Boolean = false
)

class CostsResult {
    lateinit var costs: List<CostsDTO>
}

interface CostsWebservice {
    // gibt array von remote Kosten zurück
    @GET("/costs/alluuids")
    fun getAllCosts(): Call<CostsResult>

    @GET("/costs/onecost")
    fun getOneCost(): Call<CostsDTO>

    @POST("/costs/save")
    fun saveCosts(@Body costs: CostsDTO): Call<CostsDTO>
}