package com.aashutosh.desimall_pro.endlessHomeDemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.models.category.CategoryResponseItem
import com.aashutosh.desimall_pro.ui.fragments.HomeFragment
import com.drakeet.multitype.ViewHolderInflater

class CategoryHolderInflater(private val homeFragment: HomeFragment) :
    ViewHolderInflater<CategoryResponseItem, CategoryHolderInflater.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_top, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: CategoryResponseItem) {
        holder.title.text = item.name
        holder.viewAll.setOnClickListener(View.OnClickListener {
            //homeFragment.getCategoryClicked(item);

        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val viewAll: TextView = itemView.findViewById(R.id.tvViewAll)
    }
}
