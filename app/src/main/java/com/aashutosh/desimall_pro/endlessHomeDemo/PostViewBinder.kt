package com.aashutosh.desimall_pro.endlessHomeDemo

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
import com.aashutosh.desimall_pro.models.product.ProductItem
import com.aashutosh.desimall_pro.ui.fragments.HomeFragment
import com.aashutosh.desimall_pro.utils.Constant.Companion.roundUpDecimal
import com.bumptech.glide.Glide
import com.drakeet.multitype.ItemViewBinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class PostViewBinder(
    private val context: Context,
    private val homeFragment: HomeFragment
) :
    ItemViewBinder<ProductItem, PostViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_product, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: ProductItem) {
        holder.setData(item, context, homeFragment)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivLogo: ImageView = itemView.findViewById(R.id.ivProduct)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val clMain: CardView = itemView.findViewById(R.id.clMain)
        val tvMrp: TextView = itemView.findViewById(R.id.tvMrp)
        val tvDiscountPercent: TextView = itemView.findViewById(R.id.tvDiscountPercent)
        val ivAddToCart: ImageView = itemView.findViewById(R.id.ivAddToCart)

        fun setData(post: ProductItem, context: Context, homeFragment: HomeFragment) {

            // sets the image to the imageview from our itemHolder class
            Glide.with(context).load(if (post.images.isEmpty()) "" else post.images[0].src)
                .error(R.drawable.sample_image).into(ivLogo);

            // sets the text to the textview from our itemHolder class
            tvPrice.text = "₹ ${post.price}"
            tvMrp.text = "₹ ${post.regular_price}"
            tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            val discount =
                roundUpDecimal(((post.regular_price.toDouble() - post.price.toDouble()) / post.regular_price.toDouble()) * 100)
            tvDiscountPercent.text = "$discount % off"
            tvName.text = post.name
            clMain.setOnClickListener(View.OnClickListener {
               // homeFragment.getItemClicked(post)
            })
            ivAddToCart.setOnClickListener(View.OnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                //    homeFragment.addToCart(post)
                }
            })


        }
    }
}
