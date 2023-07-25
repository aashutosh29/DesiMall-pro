package com.aashutosh.desimall_pro.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.aashutosh.desimall_pro.utils.PushNotificationService.TAG
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern
import kotlin.math.log

class OTPReceiver : BroadcastReceiver() {

    private var otpReceiverListener: OTPReceiverListener? = null

    fun initListener(oTPReceiverListener: OTPReceiverListener) {
        otpReceiverListener = oTPReceiverListener
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            if (extras != null) {
                val status = extras.getSerializable(SmsRetriever.EXTRA_STATUS) as? Status
                    ?: extras.getParcelable(SmsRetriever.EXTRA_STATUS)

                if (status != null) {
                    when (status.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                            if (!message.isNullOrEmpty()) {
                                val pattern = Pattern.compile("\\b\\d{6}\\b")
                                val matcher = pattern.matcher(message)
                                if (matcher.find()) {
                                    val myOtp = matcher.group(0)
                                    otpReceiverListener?.onOtpSuccess(myOtp)
                                    Log.d(TAG, "fuji: it should work ")
                                }else{
                                    otpReceiverListener?.onOtpTimeOut()
                                    Log.d(TAG, "fuji: matcher problem ")
                                }
                            }else{
                                Log.d(TAG, "fuji: message is empty or null ")
                                otpReceiverListener?.onOtpTimeOut()
                            }
                        }

                        CommonStatusCodes.TIMEOUT -> otpReceiverListener?.onOtpTimeOut()
                    }
                }
            }
        }
    }

    interface OTPReceiverListener {
        fun onOtpSuccess(otp: String?)
        fun onOtpTimeOut()
    }
}
