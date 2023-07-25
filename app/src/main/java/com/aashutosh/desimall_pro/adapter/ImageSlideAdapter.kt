package com.aashutosh.desimall_pro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.models.Ads
import com.aashutosh.desimall_pro.models.Raw
import com.aashutosh.desimall_pro.ui.CategoryView
import com.aashutosh.desimall_pro.ui.fragments.HomeFragment
import com.bumptech.glide.Glide

class ImageSlideAdapter(
    private val homeFragment: CategoryView,
    private val context: Context,
    private var imageList: ArrayList<Raw>
) :
    PagerAdapter() {
    override fun getCount(): Int {
        return imageList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.image_slider_item, null
            )
        val ivImages = view.findViewById<ImageView>(R.id.iv_image)
        val clMain = view.findViewById<ConstraintLayout>(R.id.clMain)
        clMain.setOnClickListener(View.OnClickListener {
            homeFragment.getAdsClicked(imageList[position])
        })

        imageList[position].image.let {
            Glide.with(context)
                .load(it)
                .error(R.drawable.app_icon)
                .fitCenter()
                .into(ivImages);
        }


        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }
}