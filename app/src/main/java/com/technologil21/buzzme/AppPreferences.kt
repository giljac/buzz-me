package com.technologil21.buzzme

import android.content.Context
import android.content.SharedPreferences

/* https://github.com/lomza/sharedpreferences-in-kotlin */
object AppPreferences {
    private lateinit var prefs: SharedPreferences
    private const val PREFS_FILENAME = "com.technologil21.buzzme.prefs"

    /**
     * list of app specific preferences:
     * chaque paire contient le nom de la preference dans le fichier
     * et la valeur attribuee par defaut si elle n'a pas été initialisee
     */
    private val SWITCH_SOUND = Pair("sound", true)
    private val SWITCH_UNMUTE = Pair("disableDND", false)
    private val ALERT_LEVEL = Pair("alertLevel", 90)
    private val CONTACTS_LIST: Pair<String, String?> = Pair("contactsList", null)

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
    }

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var soundOn: Boolean
        get() = prefs.getBoolean(SWITCH_SOUND.first, SWITCH_SOUND.second)
        set(value) = prefs.edit().putBoolean(SWITCH_SOUND.first, value).apply()
    var disableDND: Boolean
        get() = prefs.getBoolean(SWITCH_UNMUTE.first, SWITCH_UNMUTE.second)
        set(value) = prefs.edit().putBoolean(SWITCH_UNMUTE.first, value).apply()
    var alertVol: Int
        get() = prefs.getInt(ALERT_LEVEL.first, ALERT_LEVEL.second)
        set(value) = prefs.edit().putInt(ALERT_LEVEL.first, value).apply()
    var contactsL: String?
        get() = prefs.getString(CONTACTS_LIST.first, CONTACTS_LIST.second)
        set(value) = prefs.edit().putString(CONTACTS_LIST.first, value).apply()
}