package com.aashutosh.desimall_pro.endlessHomeDemo

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.models.product.ProductItem
import com.aashutosh.desimall_pro.ui.fragments.HomeFragment
import com.aashutosh.desimall_pro.utils.Constant.Companion.roundUpDecimal
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat

class PostsAdapter : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    private var products = emptyList<ProductItem>()
    private lateinit var context: Context
    private lateinit var homeFragment: HomeFragment

    fun setPosts(posts: List<ProductItem>, context: Context, homeFragment: HomeFragment) {
        this.products = posts
        this.context = context
        this.homeFragment = homeFragment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(

            LayoutInflater.from(parent.context).inflate(R.layout.item_horizontal_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productItem = products[position]
        if (productItem != null) {
            // sets the image to the imageview from our itemHolder class
            Glide.with(context)
                .load(if (productItem.images.isEmpty()) "" else productItem.images[0].src)
                .error(R.drawable.sample_image)
                .into(holder.ivLogo);

            // sets the text to the textview from our itemHolder class
            holder.tvPrice.text = "₹ ${productItem.price}"
            holder.tvMrp.text = "₹ ${productItem.regular_price}"
            holder.tvMrp.paintFlags = holder.tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            val discount =
                roundUpDecimal(((productItem.regular_price.toDouble() - productItem.price.toDouble()) / productItem.regular_price.toDouble()) * 100)
            holder.tvDiscountPercent.text = "$discount % off"
            holder.tvName.text = productItem.name
            holder.clMain.setOnClickListener(View.OnClickListener {
               // homeFragment.getItemClicked(productItem)
            })
            holder.ivAddToCart.setOnClickListener(View.OnClickListener {
                GlobalScope.launch(Dispatchers.Main) {
                   // homeFragment.addToCart(productItem)
                }
            })
        }
    }




    override fun getItemCount(): Int {
        return products.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivLogo: ImageView = itemView.findViewById(R.id.ivProduct)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val clMain: CardView = itemView.findViewById(R.id.clMain)
        val tvMrp: TextView = itemView.findViewById(R.id.tvMrp)
        val tvDiscountPercent: TextView = itemView.findViewById(R.id.tvDiscountPercent)
        val ivAddToCart: ImageView = itemView.findViewById(R.id.ivAddToCart)

        init {
            itemView.setOnClickListener { v ->
                Toast.makeText(
                    v.context,
                    absoluteAdapterPosition.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
