package com.aashutosh.simplestore.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.simplestore.R
import com.aashutosh.simplestore.models.category.CategoryResponseItem
import com.aashutosh.simplestore.ui.bottomSheet.ShortBottomSheet

class SmallCategoryAdapter(
    private val mList: List<CategoryResponseItem>,
    private val context: Context,
    private val bottomSheet: ShortBottomSheet
) :
    RecyclerView.Adapter<SmallCategoryAdapter.ViewHolder>() {
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_bottom_nave, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val categoryItem = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.tvName.text = categoryItem.name.trim()
        holder.clMain.setOnClickListener(View.OnClickListener {
            bottomSheet.getCategoryClicked(categoryItem, holder.clMain)
        })

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val clMain: ConstraintLayout = itemView.findViewById(R.id.clMain)
    }

}