package com.technologil21.buzzme

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_notification.*


class NotificationFragment : Fragment() {
    private var askedLevel: Int = 80

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateViews()
        switchSound.setOnClickListener {
            AppPreferences.soundOn = switchSound.isChecked
            updateViews()
        }
        switchUnmute.setOnClickListener {
            setNotificationsDND()
            updateViews()
        }

        alertLevel.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                levelValue.text = "$progress%"
                askedLevel = progress
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                AppPreferences.alertVol = askedLevel
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }
        })
    }

    private fun setNotificationsDND() {
        if (switchUnmute.isChecked) {
            //Desactive Do Not Disturb
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                if (accessGranted()) {
                    AppPreferences.disableDND = true
                } else {
                    /*
                        If notification policy access not granted for this package
                            Activity Action : Show Do Not Disturb access settings.
                            Users can grant and deny access to Do Not Disturb configuration from here.
                    */
                    val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                    startActivityForResult(intent, Global.ON_ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS_CALLBACK_CODE)
                    Toast.makeText(context, "Autorisez l'accès à " + getString(R.string.app_name), Toast.LENGTH_LONG).show()
                }
            }
        } else AppPreferences.disableDND = false
        updateViews()
    }

    private fun accessGranted(): Boolean {
        // Get the notification manager instance
        val mNotificationManager: NotificationManager? = activity!!.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        /*Set the interruption filter
               int INTERRUPTION_FILTER_ALL = Interruption filter constant
               - Normal interruption filter - no notifications are suppressed.
                */
        if (mNotificationManager?.isNotificationPolicyAccessGranted!!) {
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == Global.ON_ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS_CALLBACK_CODE) {
            AppPreferences.disableDND = accessGranted()
            updateViews()
        }
    }

    /**
     * Initialisation des Views à partir des valeurs dans AppPreferences
     * */
    fun updateViews() {
        var sonUnmute = AppPreferences.disableDND
        var sonOnOff = AppPreferences.soundOn
        //DND
        switchUnmute.isChecked = AppPreferences.disableDND
        var image = if (sonUnmute) R.drawable.ic_ring_volume_black_24dp
        else R.drawable.ic_ring_volume_grey_24dp
        switchUnmute.setCompoundDrawablesWithIntrinsicBounds(image, 0, 0, 0)
        //alerte sonore
        switchSound.isChecked = sonOnOff
        image = if (sonOnOff) R.drawable.ic_alarm_black_24dp
        else R.drawable.ic_alarm_grey_24dp
        switchSound.setCompoundDrawablesWithIntrinsicBounds(image, 0, 0, 0)
        //niveau sonore
        var niveau = AppPreferences.alertVol
        alertLevel.progress = niveau
        levelValue.text = "$niveau%"
    }
}


