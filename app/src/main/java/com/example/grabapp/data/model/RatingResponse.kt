package com.example.grabapp.data.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class RatingResponse(
    @SerializedName("driverId")
    val driverId: String,
    @SerializedName("averageRating")
    val averageRating: Double,
    @SerializedName("totalReviews")
    val totalReviews: Int,
    @SerializedName("ratingDistribution")
    val ratingDistribution: RatingDistribution
) {
    companion object {
        private val gson = Gson()
        
        fun fromJson(json: String): RatingResponse? {
            return try {
                gson.fromJson(json, RatingResponse::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun toJson(ratingResponse: RatingResponse): String {
            return try {
                gson.toJson(ratingResponse)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }
}

data class RatingDistribution(
    @SerializedName("1")
    val oneStar: Int = 0,
    @SerializedName("2")
    val twoStar: Int = 0,
    @SerializedName("3")
    val threeStar: Int = 0,
    @SerializedName("4")
    val fourStar: Int = 0,
    @SerializedName("5")
    val fiveStar: Int = 0
)
