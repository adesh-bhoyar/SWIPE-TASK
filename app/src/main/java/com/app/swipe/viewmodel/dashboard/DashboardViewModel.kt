package com.app.swipe.viewmodel.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.swipe.model.Product
import com.app.swipe.model.SuccessResponse
import com.app.swipe.utils.network.NetworkResult
import com.app.swipe.viewmodel.dashboard.repository.DashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepositoryNew: DashboardRepository
) : ViewModel() {

    val products: LiveData<NetworkResult<List<Product>>> get() = dashboardRepositoryNew.products
    val productAdded: LiveData<NetworkResult<SuccessResponse>> get() = dashboardRepositoryNew.productAdded

    fun getProducts() {
        viewModelScope.launch {
            dashboardRepositoryNew.getProducts()
        }
    }

    fun addProduct(
        files: File?,
        productName: String,
        productType: String,
        price: String,
        tax: String
    ) {
        viewModelScope.launch {
            dashboardRepositoryNew.addProduct(files, productName, productType, price, tax)
        }
    }
}