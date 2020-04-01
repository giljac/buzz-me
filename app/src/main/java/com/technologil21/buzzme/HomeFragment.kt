package com.technologil21.buzzme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSimuler.setOnClickListener {
            AlertModule(context!!, "Simulation d'alerte").sendAlert()
        }
    }

    companion object {
        private const val ARG_IS_ACTIVE = "argBool"
        //méthode statique newInstance, qui s’occupe dé gérer la création du fragment
        //en passant les données dans un bundle
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}