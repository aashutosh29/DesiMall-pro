package com.aashutosh.desimall_pro.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.ui.CartInterface
import com.aashutosh.desimall_pro.utils.Constant.Companion.roundUpString
import com.bumptech.glide.Glide
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CartAdapter(
    private val mList: List<CartProduct>,
    private val context: Context,
    private val cartInterface: CartInterface
) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shopping_cart, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = mList[position]
        var quantity: Int = cartItem.quantity


        Glide.with(context)
            .load(cartItem.image)
            .placeholder(R.drawable.app_icon)
            .into(holder.ivLogo)
        holder.tvName.text = cartItem.name
        holder.tvDetail.text = cartItem.name
        holder.tvQuantity.text = cartItem.quantity.toString()
        holder.tvPrice.text = "₹ ${roundUpString((cartItem.quantity * cartItem.price).toString())}"
        holder.tvMrp.text = "₹ ${roundUpString((cartItem.mrp * cartItem.quantity).toString())}"
        holder.tvMrp.paintFlags = holder.tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.ivPlus.setOnClickListener(View.OnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                cartInterface.updateQty(cartItem, quantity)
            }
            quantity++
            holder.tvQuantity.text = quantity.toString()

        })
        holder.ivMinus.setOnClickListener(View.OnClickListener {
            if (quantity > 1) {
                quantity--
                GlobalScope.launch(Dispatchers.Main) {
                    cartInterface.updateQty(cartItem, quantity)
                }
                holder.tvQuantity.text = quantity.toString()
            } else if (quantity == 1)
                GlobalScope.launch(Dispatchers.Main) {
                    cartInterface.deleteProduct(cartItem)
                }
        })


    }



    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val ivLogo: ImageView = itemView.findViewById(R.id.ivProduct)
        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvDetail: TextView = itemView.findViewById(R.id.tvProductDetail)
        val ivPlus: ImageView = itemView.findViewById(R.id.ivPlus)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val ivMinus: ImageView = itemView.findViewById(R.id.ivMinus)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvMrp: TextView = itemView.findViewById(R.id.tvMrp)
    }

}