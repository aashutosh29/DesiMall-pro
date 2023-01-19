package com.aashutosh.desimall_pro.utils

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import java.math.RoundingMode
import java.text.DecimalFormat

class Constant {
    companion object {


        const val ADDRESS = "address"
        const val LAND_MARK = "landmark"

        const val LOCATION_CHANGED = "location_changed"
        //java
        const val WEB = "web"
        const val APP_NAME = "app_name"
        const val URL = "URL"
        const val BRANCH_NAME = "branch_name"
        const val BRANCH_CODE = "2"
        const val IS_FAV = "is_fav"
        const val DETAILS = "details"
        const val LOCATION = "location"
        const val ZIP = "zip"
        const val VERIFY_USER_LOCATION = "from_navigator"
        const val USER_SKIPPED = "user_skipped"
        const val DETAIlS_VERIFED = "details_verified"
        const val PHONE_NUMBER = "verified_phone_number"
        const val DOCUMENT = "document"
        const val LAT_LON = "lat_lon"
        const val LAT = "lat"
        const val LON = "LON"
        const val VERIFIED_LOCATION = "verified_location"
        const val VERIFIED_NUM = "verified_num"

        const val phoneNumberKey = "PhoneNumberKey"

        const val FIRST_HOME = "first_home"
        const val MRP_PRICE = "mrp"
        const val CREATE = "create"
        const val CATEGORY_ID = "category_Id"
        const val ASC_OR_DSC = "asc_or_dsc"
        const val ORDER_BY = "order_by"
        const val MAX_PRICE = "max_price"
        const val MIN_PRICE = "min_price"
        const val IS_SEARCH_FOCUS = "search_focus"
        const val FIRST_LOAD = "first_load"
        const val IS_PROFILE = "is_profile"
        const val PHOTO = "user_photo"
        const val EMAIL = "email"
        const val NAME = "name"
        const val LOGIN = "user_login"
        const val IS_NOTIFICATION = "is_notification"
        const val CATEGORY_NAME = "category_name"
        const val ID = "id"
        const val IS_VIEW_ALL = "is_view_all"
        const val PRODUCT_PRICE = "product_price"
        const val PRODUCT_NAME = "product_name"
        const val IMAGE_URL = "image_url"
        const val CLIENT_ID = "ck_32e46804bfe8c984b139a35b6d3e7b6893e43644"
        const val CLIENT_SECRET = "cs_8d6fa97258ec30c830f3f601e0b4b3d082778203"
        const val BASE_URL = "http://103.234.185.42:50/API/"
        const val BASE_URL_2 = "https://fcm.googleapis.com/fcm/"
        const val DESCRIPTION = "description"


        const val ORDER_VERIFICATION_PENDING = "0"
        const val ORDER_VERIFIED = "1"
        const val ORDER_SHIPPING = "2"
        const val ORDER_DELIVERED = "3"


        fun roundUpDecimal(number: Double): Double {
            val df = DecimalFormat("#,###,##0.00")
            df.roundingMode = RoundingMode.CEILING
            return df.format(number).replace(".00", "").toDouble()
        }

        fun roundUpString(number: String): String {
            val formatter = DecimalFormat("#,###,##0.00");
            return formatter.format(number.toDouble())

        }


        fun setProgressDialog(context: Context, message: String): AlertDialog {
            val llPadding = 30
            val ll = LinearLayout(context)
            ll.orientation = LinearLayout.HORIZONTAL
            ll.setPadding(llPadding, llPadding, llPadding, llPadding)
            ll.gravity = Gravity.START
            var llParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            llParam.gravity = Gravity.START
            ll.layoutParams = llParam

            val progressBar = ProgressBar(context)
            progressBar.isIndeterminate = true
            progressBar.setPadding(0, 0, llPadding, 0)
            progressBar.layoutParams = llParam

            llParam = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            llParam.gravity = Gravity.CENTER
            val tvText = TextView(context)
            tvText.text = message
            tvText.setTextColor(Color.parseColor("#000000"))
            tvText.textSize = 16.toFloat()
            tvText.layoutParams = llParam

            ll.addView(progressBar)
            ll.addView(tvText)

            val builder = AlertDialog.Builder(context)
            builder.setCancelable(true)
            builder.setView(ll)

            val dialog = builder.create()
            val window = dialog.window
            if (window != null) {
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(dialog.window?.attributes)
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                dialog.window?.attributes = layoutParams
            }
            return dialog
        }

        fun ViewPager.autoScroll(interval: Long) {

            val handler = Handler()
            var scrollPosition = 0

            val runnable = object : Runnable {

                override fun run() {

                    /**
                     * Calculate "scroll position" with
                     * adapter pages count and current
                     * value of scrollPosition.
                     */
                    val count = adapter?.count ?: 0
                    setCurrentItem(scrollPosition++ % count, true)

                    handler.postDelayed(this, interval)
                }
            }

            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageSelected(position: Int) {
                    // Updating "scroll position" when user scrolls manually
                    scrollPosition = position + 1
                }

                override fun onPageScrollStateChanged(state: Int) {
                    // Not necessary
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    // Not necessary
                }
            })

            handler.post(runnable)
        }

        const val SPAN_COUNT = 2
    }
}



