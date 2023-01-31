package com.aashutosh.desimall_pro.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aashutosh.desimall_pro.R
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.ui.CategoryView


class CategoryAdapter(
    private val mList: List<String>,
    private val categoryView: CategoryView,
    private val clickedItem: String
) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    var textViewList: ArrayList<TextView> = ArrayList()

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(com.aashutosh.desimall_pro.R.layout.item_category, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        textViewList.add(holder.tvLogo)
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
        if (clickedItem == categoryItem.trim()) {
            holder.tvLogo.setBackgroundColor(Color.parseColor("#E53935"))
            holder.tvLogo.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            holder.tvLogo.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.tvLogo.setTextColor(Color.parseColor("#E53935"))
        }
        holder.clMain.setOnClickListener(View.OnClickListener {
            for (text in textViewList) {
                text.setBackgroundColor(Color.parseColor("#FFFFFF"))
                text.setTextColor(Color.parseColor("#E53935"))
            }
            holder.tvLogo.setBackgroundColor(Color.parseColor("#E53935"))
            holder.tvLogo.setTextColor(Color.parseColor("#FFFFFF"))
            categoryView.getCategoryClicked2(categoryItem, holder.tvLogo)
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