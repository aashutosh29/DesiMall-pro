package com.aashutosh.simplestore.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.simplestore.R
import com.aashutosh.simplestore.ui.fragments.CategoryFragment

class ProductCategoryAdapter(
    private val mList: List<String>,
    private val context: Context,
    private val categoryFragment: CategoryFragment
) :
    RecyclerView.Adapter<ProductCategoryAdapter.ViewHolder>() {
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_category, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val categoryItem = mList[position]

        // sets the image to the imageview from our itemHolder class

        // sets the text to the textview from our itemHolder class
        holder.tvCategory.text = categoryItem
        holder.tvProduct.text = categoryItem[0].toString()
        holder.clMain.setOnClickListener(View.OnClickListener {
            categoryFragment.getCategoryClicked(categoryItem)
        })

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvProduct: TextView = itemView.findViewById(R.id.tvProduct)
        val clMain: ConstraintLayout = itemView.findViewById(R.id.clMain)
    }

}