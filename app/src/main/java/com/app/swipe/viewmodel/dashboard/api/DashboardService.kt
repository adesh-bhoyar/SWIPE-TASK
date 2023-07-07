package com.app.swipe.viewmodel.dashboard.api

import com.app.swipe.model.Product
import com.app.swipe.model.SuccessResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface DashboardService {

    @Multipart
    @POST("add")
    suspend fun addProduct(
        @Part files: MultipartBody.Part?,
        @Part("product_name") product_name: RequestBody,
        @Part("product_type") product_type: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
    ): Response<SuccessResponse>

    @GET("get")
    suspend fun getProducts(): Response<List<Product>>
}