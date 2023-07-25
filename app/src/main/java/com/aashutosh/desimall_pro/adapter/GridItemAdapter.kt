package com.aashutosh.desimall_pro.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.models.GridItem

class GridItemAdapter(
    private val mList: List<GridItem>,
    private val onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<GridItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val item = mList[position]

        holder.ivIcon.setImageResource(item.iconResId)
        holder.tvTitle.text = item.title

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(item)

        }
    }


    override fun getItemCount(): Int {
        return mList.size

    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val clMain: ConstraintLayout = itemView.findViewById(R.id.clMain)
    }
    interface OnItemClickListener {
        fun onItemClick(item: GridItem)
    }
}


