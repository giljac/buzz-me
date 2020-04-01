package com.technologil21.buzzme

import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

//envoi du SMS d'alerte et affichage du popup d'alerte
class AlertModule(context: Context, msgBody: String) {
    private var msg = msgBody
    private var cntxt = context

    fun sendAlert() {
        if (msg.matches(cntxt.getString(R.string.regex_gilberte).toRegex())) msg = "Gilberte a envoyé un SOS!"
        //récuperation de l'heure
        val date = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm")
        // recupération de la liste des destinataires
        val jsonList = AppPreferences.contactsL

        if (jsonList != null) {
            //envoi alerte par SMS aux destinataires
            val msg2send = msg + cntxt.getString(R.string.start_alert) + dateFormat.format(date)
            sendSms(msg2send, jsonList)
        }
        // affiche popup d'alerte
        val inPop = Intent(cntxt, PopAlertActivity::class.java)
        inPop.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        inPop.putExtra(Global.NOTIF, msg)
        inPop.putExtra(Global.HEURE, dateFormat.format(date))
        ContextCompat.startActivity(cntxt, inPop, null)
    }

//    @RequiresPermission(SEND_SMS1)
    private fun sendSms(msg2send: String, jsonStr: String) {
        var sms2send = msg2send + "%n" + cntxt.getString(R.string.entete)
        sms2send = String.format(sms2send)
        val gson = Gson()
        val contacts = gson.fromJson(jsonStr, Array<ContactItem>::class.java)
        val smgr = SmsManager.getDefault()
        for (contact in contacts) {
            val phoneNum = contact.num
            try {
                smgr.sendTextMessage(phoneNum, null, sms2send, null, null)
                Toast.makeText(cntxt, "Envoi à $phoneNum", Toast.LENGTH_SHORT).show()
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(cntxt, "Erreur sur envoi à $phoneNum", Toast.LENGTH_LONG).show()
            }

        }
    }
}
