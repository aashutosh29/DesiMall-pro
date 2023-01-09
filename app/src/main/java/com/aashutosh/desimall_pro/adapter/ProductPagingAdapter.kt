package com.aashutosh.desimall_pro.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem
import com.aashutosh.desimall_pro.ui.fragments.HomeFragment
import com.aashutosh.desimall_pro.utils.Constant
import com.bumptech.glide.Glide
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ProductPagingAdapter(
    private val context: Context,
    private val homeFragment: HomeFragment
) :
    PagingDataAdapter<DesiDataResponseSubListItem, ProductPagingAdapter.ViewHolder>(COMPARATOR) {


    @SuppressLint("SetTextI18n")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productItem = getItem(position)
        if (productItem != null) {
            // sets the image to the imageview from our itemHolder class
            Glide.with(context)
                .load(if (productItem.sku == null) " " else "https://livedesimall.in/ldmimages/" + productItem.sku + ".png")
                .error(R.drawable.app_icon)
                .placeholder(R.drawable.app_icon)
                .into(holder.ivLogo)

            // sets the text to the textview from our itemHolder class
            holder.tvPrice.text = "₹ ${Constant.roundUpString(productItem.variant_sale_price.toString())}"
            holder.tvMrp.text = "₹ ${Constant.roundUpString(productItem.variant_mrp.toString())}"
            holder.tvMrp.paintFlags = holder.tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            if ((productItem.variant_sale_price.toDouble() - productItem.variant_mrp.toDouble()) == 0.0) {
                holder.tvMrp.visibility = View.INVISIBLE
                holder.clDiscount.visibility = View.INVISIBLE
            }

            val discount =
                Constant.roundUpDecimal(((productItem.variant_mrp.toDouble() - productItem.variant_sale_price.toDouble()) / productItem.variant_mrp.toDouble()) * 100)
            holder.tvDiscountPercent.text = "$discount % off"
            holder.tvName.text = productItem.sku_name
            holder.clMain.setOnClickListener(View.OnClickListener {
                homeFragment.getItemClicked(productItem)
            })
            holder.ivAddToCart.setOnClickListener(View.OnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                    homeFragment.addToCart(productItem)
                }
            })
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }


    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<DesiDataResponseSubListItem>() {
            override fun areItemsTheSame(
                oldItem: DesiDataResponseSubListItem,
                newItem: DesiDataResponseSubListItem
            ): Boolean {
                return oldItem.sku == newItem.sku
            }

            override fun areContentsTheSame(
                oldItem: DesiDataResponseSubListItem,
                newItem: DesiDataResponseSubListItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivLogo: ImageView = itemView.findViewById(R.id.ivProduct)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val clMain: CardView = itemView.findViewById(R.id.clMain)
        val tvMrp: TextView = itemView.findViewById(R.id.tvMrp)
        val tvDiscountPercent: TextView = itemView.findViewById(R.id.tvDiscountPercent)
        val ivAddToCart: ImageView = itemView.findViewById(R.id.ivAddToCart)
        val clDiscount: ConstraintLayout = itemView.findViewById(R.id.clDiscount)

    }
}