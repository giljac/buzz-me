package com.technologil21.buzzme

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment


class WifiConnectDialog : AppCompatDialogFragment() {
    interface WifiConnectDialogListener {
        fun onDialogYesClicked()
        fun onDialogCancelClicked()
    }

    private lateinit var callback: WifiConnectDialogListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            callback = targetFragment as WifiConnectDialogListener
        } catch (e: ClassNotFoundException) {
            throw ClassCastException("Calling Fragment must implement OnAddFriendListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = this.context?.let { AlertDialog.Builder(it) }
        builder!!.setTitle("Attention!")
                .setMessage("Cette opération va déconnecter/connecter le wifi,\nvoulez-vous continuer?")
                .setNegativeButton("Annuler") { dialog, which ->
//                    callback.onDialogCancelClicked()
                }
                .setPositiveButton("Oui") { dialog, which -> callback.onDialogYesClicked() }
                .setIcon(R.drawable.ic_wifi_black_24dp)
        return builder.create()
    }
}