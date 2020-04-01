package com.technologil21.buzzme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage

class ReceiveSms : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            val bundle = intent.extras
            if (bundle != null) {
                try {
                    val pdus = bundle.get("pdus") as Array<*>?
                    val msgs = arrayOfNulls<SmsMessage>(pdus!!.size)
                    for (i in msgs.indices) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, bundle.getString("format"))
                        } else {
                            msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        }
                        val msgBody = msgs[i]?.messageBody
                        //Si SOS reçu !!!!!
                        if (msgBody != null) {
                            if (msgBody.contains(context.getString(R.string.regex_sos).toRegex())) {
                                AlertModule(context, msgBody).sendAlert()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}