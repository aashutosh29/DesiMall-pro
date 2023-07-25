package com.aashutosh.desimall_pro.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsBroadCastReceiver : BroadcastReceiver() {
    var smsBroadCastReceiverListener: SmsBroadCastReceiverListener? = null


    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extra = intent.extras
            val smsStatusRetriever = extra?.get(SmsRetriever.EXTRA_STATUS) as Status
            when (smsStatusRetriever.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val messageIntent =
                        extra.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    smsBroadCastReceiverListener?.onSuccess(messageIntent)
                }

                CommonStatusCodes.TIMEOUT -> {
                    smsBroadCastReceiverListener?.onFailure()
                }
            }
        }
    }

    interface SmsBroadCastReceiverListener {
        fun onSuccess(otp: Intent?)
        fun onFailure()
    }
}