package com.app.swipe.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.app.swipe.R
import com.app.swipe.databinding.ActivityDashboardBinding
import com.app.swipe.model.Product
import com.app.swipe.utils.network.NetworkResult
import com.app.swipe.viewmodel.dashboard.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val dashboardViewModelNew by viewModels<DashboardViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            dashboardViewModelNew.getProducts()
        }
    }
}