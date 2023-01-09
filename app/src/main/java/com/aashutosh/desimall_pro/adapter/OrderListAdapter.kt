package com.aashutosh.desimall_pro.adapter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.databinding.ItemOrderHistoryBinding
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.models.Order
import com.aashutosh.desimall_pro.utils.Constant

class OrderListAdapter(
    private val mList: List<Order>,
    private val context: Context
) : RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val binding =
            ItemOrderHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // binds the list items to a view
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = mList[position]
        holder.binding.tvOrderTag.text = "Order ${order.id}"
        holder.binding.tvDate.text = "Placed on ${order.date}"
        if (order.status == Constant.ORDER_VERIFICATION_PENDING) {
            holder.binding.tvStatus.text = "Pending"
            holder.binding.tvStatus.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.md_red_700
                )
            )
        } else {
            holder.binding.tvStatus.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.md_green_700
                )
            )
            holder.binding.tvStatus.text = "Verified"
        }

        holder.binding.recyclerview.visibility = View.VISIBLE
        // this creates a vertical layout Manager
        holder.binding.recyclerview.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = OrderHistoryProductAdapter(
            cartDataList(order.products as ArrayList<String>),
            context = context
        )
        // Setting the Adapter with the recyclerview
        holder.binding.recyclerview.adapter = adapter


        holder.binding.tvTotal.text = "₹ ${order.totalPrice}"
        holder.binding.tvItemCount.text = "${order.totalProduct} item"
    }

    private fun cartDataList(products: ArrayList<String>): List<CartProduct> {
        val cartList: ArrayList<CartProduct> = arrayListOf()
        for (product in products) {
            val detail: List<String> = product.split("++")
            cartList.add(
                CartProduct(
                    detail[0].toInt(),
                    detail[1],
                    detail[2],
                    "",
                    detail[3].toInt(),
                    detail[4].toDouble(),
                    detail[5].toDouble()
                )
            )

        }
        Log.d(ContentValues.TAG, "cartDataList: $cartList")
        return cartList
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(val binding: ItemOrderHistoryBinding) : RecyclerView.ViewHolder(binding.root)

}