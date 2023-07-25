package com.aashutosh.desimall_pro.ui

import android.widget.TextView
import com.aashutosh.desimall_pro.models.Raw

interface CategoryView {
    fun getCategoryClicked(categoryItem: String)
     fun getCategoryClicked2(categoryItem: String, tvLogo: TextView)

    fun getAdsClicked(ads: Raw)
}