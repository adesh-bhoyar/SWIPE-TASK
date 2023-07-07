package com.app.swipe.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.app.swipe.R
import com.app.swipe.databinding.FragmentProductsBinding
import com.app.swipe.model.Product
import com.app.swipe.utils.network.NetworkResult
import com.app.swipe.view.adaptor.ProductsAdapter
import com.app.swipe.viewmodel.dashboard.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsFragment : Fragment() {

    private val dashboardViewModelNew: DashboardViewModel by activityViewModels()
    private lateinit var adaptor: ProductsAdapter
    private lateinit var binding: FragmentProductsBinding
    private lateinit var products: List<Product>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_products, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //----
        observeApiResult()
        //----
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_productsFragment_to_addProductFragment)
        }
        //---
        binding.search.addTextChangedListener {
            val text = binding.search.text
            adaptor.updateList(products.filter { it.product_name.contains(text.toString(),true) })
        }
    }


    private fun observeApiResult() {
        dashboardViewModelNew.products.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    binding.progress.visibility = View.GONE
                    // -----
                    products = it.data as List<Product>
                    //---
                    adaptor = ProductsAdapter(products, requireContext())
                    binding.productRecycle.adapter = adaptor
                }
                is NetworkResult.Error -> {
                    binding.progress.visibility = View.GONE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
            }
        })
    }
}