package com.aashutosh.desimall_pro.ui

import android.widget.TextView

interface CategoryView {
    fun getCategoryClicked(categoryItem: String)
     fun getCategoryClicked2(categoryItem: String, tvLogo: TextView)
}