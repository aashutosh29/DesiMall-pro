package com.aashutosh.simplestore.utils

import java.math.RoundingMode
import java.text.DecimalFormat

class Constant {
    companion object {


        //java
        const val WEB = "web"
        const val APP_NAME = "app_name"
        const val URL = "URL"
        const val GMAIL = "gmail"
        const val BRANCH_CODE = "2"


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
        fun roundUpDecimal(number: Double): Double {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            return df.format(number).toDouble()
        }

        const val SPAN_COUNT = 2
    }
}



