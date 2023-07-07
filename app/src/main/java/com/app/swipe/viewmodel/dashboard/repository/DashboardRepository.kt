package com.app.swipe.viewmodel.dashboard.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.swipe.model.Product
import com.app.swipe.model.SuccessResponse
import com.app.swipe.utils.network.NetworkResult
import com.app.swipe.viewmodel.dashboard.api.DashboardService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val dashboardService: DashboardService
) {
    // api constants
    private val _products = MutableLiveData<NetworkResult<List<Product>>>()
    val products: LiveData<NetworkResult<List<Product>>> get() = _products

    // api constants
    private val _productAdded = MutableLiveData<NetworkResult<SuccessResponse>>()
    val productAdded: LiveData<NetworkResult<SuccessResponse>> get() = _productAdded


    suspend fun getProducts() {
        _products.postValue(NetworkResult.Loading())
        //-----
        val response = dashboardService.getProducts()
        if (response.isSuccessful && response.body() != null) {
            _products.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            _products.postValue(NetworkResult.Error(response.errorBody().toString()))
        } else {
            _products.postValue(NetworkResult.Error("Something went wrong !!"))
        }
    }

    suspend fun addProduct(
        files: File?,
        productName: String,
        productType: String,
        price: String,
        tax: String
    ) {
        _productAdded.postValue(NetworkResult.Loading())

        var file: MultipartBody.Part? = null
        files?.let {
            val requestBody = RequestBody.create("application/octet-stream".toMediaTypeOrNull(), it)
            file = MultipartBody.Part.createFormData("files[]", files.name, requestBody)
        }

        val productNameRequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), productName)
        val productTypeRequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), productType)
        val priceRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), price)
        val taxRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), tax)
        val response = dashboardService.addProduct(
            file,
            productNameRequestBody,
            productTypeRequestBody,
            priceRequestBody,
            taxRequestBody
        )
        if (response.isSuccessful && response.body() != null) {
            _productAdded.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            _productAdded.postValue(NetworkResult.Error(response.errorBody().toString()))
        } else {
            _productAdded.postValue(NetworkResult.Error("Something went wrong !!"))
        }
    }
}