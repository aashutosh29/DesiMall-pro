package com.aashutosh.desimall_pro.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.ui.CategoryView

class CategoryAdapter(
    private val mList: List<String>,
    private val context: Context,
    private val categoryView: CategoryView
) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val categoryItem = mList[position]
        // sets the image to the imageview from our itemHolder class
        // sets the text to the textview from our itemHolder class
        holder.tvName.visibility = View.GONE
        if (categoryItem.trim() == "") {
            holder.tvLogo.text = "A"
            holder.tvName.text = "ALL"
        } else {
            holder.tvName.text = categoryItem
            holder.tvLogo.text = categoryItem.trim()[0].toString()
        }
        holder.clMain.setOnClickListener(View.OnClickListener {
            categoryView.getCategoryClicked(categoryItem)
        })

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvLogo: TextView = itemView.findViewById(R.id.tvProduct)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val clMain: CardView = itemView.findViewById(R.id.clMain)
    }

}