package com.aashutosh.desimall_pro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.databinding.ItemAdsBinding
import com.aashutosh.desimall_pro.models.Raw
import com.aashutosh.desimall_pro.ui.CategoryView
import com.aashutosh.desimall_pro.ui.cloneHomeFragment.CloneHomeFragment
import com.bumptech.glide.Glide

class AdsAdapter(
    private val mList: List<Raw>,
    private val context: Context,
    private val homeFragment: CategoryView
) :
    RecyclerView.Adapter<AdsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemAdsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productItem = mList[position]
        Glide.with(context)
            .load(productItem.image)
            .error(R.drawable.sample_image)
            .into(holder.binding.ivAds)
        holder.binding.clMain.setOnClickListener(View.OnClickListener {
            homeFragment.getAdsClicked(productItem)
        })
        if (productItem.name != "null" && productItem.name.isNotEmpty()) {
            holder.binding.tvAds.text = productItem.name.trim()
        } else {
            holder.binding.tvAds.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(val binding: ItemAdsBinding) :
        RecyclerView.ViewHolder(binding.root)
}