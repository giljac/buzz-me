package com.technologil21.buzzme

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_alert_list.*
import java.util.*
import java.util.regex.Pattern


class AlertListFragment : Fragment() {
    //patterns
    private val PHONE: Pattern = Pattern.compile("(0|(\\+33))[1-9][0-9]{8}")
    private val SPACE: Pattern = Pattern.compile("\\s")

    //    private lateinit var mContactsList: ArrayList<ContactItem>
    private var mContactsList: ArrayList<ContactItem>? = null
    private lateinit var mAdapter: ContactsAdapter
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_alert_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtons()
        buildRecyclerView()
    }

    private fun addContact() {
        if (!getName.text.toString().isEmpty() && validNum(getPhone.text.toString())) {
            mContactsList?.add(ContactItem(getName.text.toString(), getPhone.text.toString()))
            //masquer le clavier
            val imm = context!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            //effacer les champs
            getName.setText("")
            getPhone.setText("")
            getPhone.clearFocus()
            //mette à jour la liste affichée
            mAdapter.notifyDataSetChanged()
            savePrefs()
        } else {
            Toast.makeText(context, "Données non valides!", Toast.LENGTH_LONG).show()
        }
    }

    private fun validNum(numero: String): Boolean {
        var num = numero
        num = SPACE.matcher(num).replaceAll("")
        return PHONE.matcher(num).matches()
    }

    fun removeItem(position: Int) {
        mContactsList!!.removeAt(position)
        mAdapter.notifyItemRemoved(position)
        savePrefs()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadPrefs()
    }

    private fun buildRecyclerView() {
        recyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(recyclerView.context)
        mAdapter = this.mContactsList?.let { ContactsAdapter(it) }!!

        recyclerView.layoutManager = mLayoutManager
        recyclerView.adapter = mAdapter
        mAdapter.setOnItemClickListener(object : ContactsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
            }

            override fun onDeleteClick(position: Int) {
                removeItem(position)
            }
        })
    }

    private fun setButtons() {
        getPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher("FR"))
        buttonAdd.setOnClickListener { addContact() }
    }

    private fun savePrefs() {
        val liste = Gson().toJson(mContactsList)
        AppPreferences.contactsL = liste //.toJson(mContactsList)
        Log.d("toto", "savePrefs: " + AppPreferences.contactsL)
    }

    private fun loadPrefs() {
        val gson = Gson()
        val json = AppPreferences.contactsL
        Log.d("toto", "loadPrefs: " + json)
        val type = object : TypeToken<ArrayList<ContactItem>>() {}.type
//        val type = object : TypeToken<ArrayList<ContactItem?>?>() {}.type
        mContactsList = gson.fromJson<ArrayList<ContactItem>>(json, type)
        if (mContactsList == null) {
            mContactsList = ArrayList()
        }
    }
}
