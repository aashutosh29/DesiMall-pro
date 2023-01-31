package com.aashutosh.desimall_pro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.databinding.ItemAdsBinding
import com.aashutosh.desimall_pro.models.Ads
import com.aashutosh.desimall_pro.ui.fragments.HomeFragment
import com.bumptech.glide.Glide

class AdsAdapter(
    private val mList: List<Ads>,
    private val context: Context,
    private val homeFragment: HomeFragment
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
        holder.binding.tvAds.text = productItem.name.trim()
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(val binding: ItemAdsBinding) :
        RecyclerView.ViewHolder(binding.root)
}