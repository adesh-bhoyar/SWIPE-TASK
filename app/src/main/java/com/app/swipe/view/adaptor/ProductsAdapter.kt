package com.app.swipe.view.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.swipe.R
import com.app.swipe.databinding.ItemProductBinding
import com.app.swipe.model.Product
import com.bumptech.glide.Glide

class ProductsAdapter(
    var list: List<Product>,
    private val context: Context,
) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: ItemProductBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_product,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position]
        if (product.image.isNotEmpty()) {
            Glide.with(context)
                .load(product.image)
                .into(holder.itemProductBinding.image)
        }
        holder.itemProductBinding.name.text = product.product_name
        holder.itemProductBinding.type.text = product.product_type
        holder.itemProductBinding.tex.text = "Tax: Rs ${product.tax}"
        holder.itemProductBinding.price.text = "Rs ${product.price}"
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemProductBinding: ItemProductBinding) :
        RecyclerView.ViewHolder(itemProductBinding.root) {
        val itemProductBinding = itemProductBinding
    }

    fun updateList(list: List<Product>) {
        this.list = list;
        notifyDataSetChanged()
    }
}