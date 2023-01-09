package com.aashutosh.desimall_pro.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.databinding.ItemOrderHistoryProductBinding
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.utils.Constant.Companion.roundUpDecimal
import com.aashutosh.desimall_pro.utils.Constant.Companion.roundUpString
import com.bumptech.glide.Glide

class OrderHistoryProductAdapter(
    private val mList: List<CartProduct>,
    private val context: Context,
) :
    RecyclerView.Adapter<OrderHistoryProductAdapter.ViewHolder>() {
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemOrderHistoryProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = mList[position]
        Glide.with(context)
            .load(cartItem.image)
            .placeholder(R.drawable.app_icon)
            .into(holder.binding.ivProduct)
        holder.binding.tvProductName.text = cartItem.name
        holder.binding.tvProductDetail.text = cartItem.name
        holder.binding.tvQuantity.text = "x ${cartItem.quantity.toString()}"
        holder.binding.tvPrice.text = "₹ ${roundUpString((cartItem.quantity * cartItem.price).toString())}"



    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(val binding: ItemOrderHistoryProductBinding) :
        RecyclerView.ViewHolder(binding.root)

}