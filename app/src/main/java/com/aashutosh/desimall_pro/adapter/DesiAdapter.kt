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
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem
import com.aashutosh.desimall_pro.ui.fragments.HomeFragment
import com.aashutosh.desimall_pro.utils.Constant
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DesiAdapter(
    private val mList: List<DesiDataResponseSubListItem>,
    private val context: Context,
    private val homeFragment: HomeFragment
) :
    RecyclerView.Adapter<DesiAdapter.ViewHolder>() {
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val productItem = mList[position]
        if (productItem != null) {
            // sets the image to the imageview from our itemHolder class
            Glide.with(context)
                .load(if (productItem.sku == null) " " else "https://livedesimall.in/ldmimages/" + productItem.sku + ".png")
                .error(R.drawable.sample_image)
                .into(holder.ivLogo);

            // sets the text to the textview from our itemHolder class
            holder.tvPrice.text = "₹ ${productItem.variant_sale_price}"
            holder.tvMrp.text = "₹ ${productItem.variant_mrp}"
            holder.tvMrp.paintFlags = holder.tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            val discount =
                Constant.roundUpString((((productItem.variant_mrp.toDouble() - productItem.variant_sale_price.toDouble()) / productItem.variant_mrp.toDouble()) * 100).toString())
            holder.tvDiscountPercent.text = "$discount % off"
            holder.tvName.text = productItem.sku_name
            holder.clMain.setOnClickListener(View.OnClickListener {
                homeFragment.getItemClicked(productItem)
            })
            holder.ivAddToCart.setOnClickListener(View.OnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                   // homeFragment.addToCart(productItem)
                }
            })
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val ivLogo: ImageView = itemView.findViewById(R.id.ivProduct)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val clMain: CardView = itemView.findViewById(R.id.clMain)
        val tvMrp: TextView = itemView.findViewById(R.id.tvMrp)
        val tvDiscountPercent: TextView = itemView.findViewById(R.id.tvDiscountPercent)
        val ivAddToCart: ImageView = itemView.findViewById(R.id.ivAddToCart)
    }

}