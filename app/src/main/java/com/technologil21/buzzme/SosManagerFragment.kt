package com.technologil21.buzzme

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest.Builder
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_sos_manager.*
import java.lang.ref.WeakReference


class SosManagerFragment : Fragment(), WifiConnectDialog.WifiConnectDialogListener {
    companion object {
        private val url = "http://192.168.4.1"
        private val SSID = "SOS Manager"//"Bbox-11D449_plus"
        private val networkSSID = "\"$SSID\""
        private val PASS_WIFI = "Gilberte"//"33189D73D3"
        private val passPhrase = "\"$PASS_WIFI\""
    }

    private val TAG = "toto_SOS"
    lateinit var wifiManager: WifiManager
    lateinit var wifiLock: WifiManager.WifiLock
    private var connectedBefore = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        val contextThemeWrapper = ContextThemeWrapper(activity, R.style.AppTheme_NoActionBar)
        // clone the inflater using the ContextThemeWrapper
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        return localInflater.inflate(R.layout.fragment_sos_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wifiManager = context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = MyWebChromeClient() //pour gestion des alertes javascript
        webView.loadUrl("file:///android_asset/basic.html")

        btnConnect.setOnClickListener {
            onConnectBtnCicked()
        }
    }

    private fun onConnectBtnCicked() {
        connectedBefore = false
        openDialog()
    }

    //interface WifiConnectDialogListener
    override fun onDialogYesClicked() {
        Log.d(TAG, "onDialogYesClicked")
        PrepareConnectToMySsidTask(this).execute()
    }

    //interface WifiConnectDialogListener
    override fun onDialogCancelClicked() {
        Log.d(TAG, "onDialogCancelClicked")
    }

    private fun goForSsid() {
        RunConnectToMySsidTask(this).execute()
    }

    fun displayStatusConnection(
            text: String = "",
            progBarInvisible: Boolean = true,
            visible: Boolean = true
    ) {
        Log.d(TAG + "_display", text)
        statusView.text = text
        statusView.visibility = VISIBLE
        //statusView.isVisible = visible
        if (progBarInvisible) indeterminateBar.visibility = INVISIBLE
        //indeterminateBar.isInvisible = progBarInvisible
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        wifiLock.release()
        super.onDestroy()
    }

    fun openWeb() {
        try {
            wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "BuzzMe");
            wifiLock.acquire()
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
        }
        Log.d(TAG, "openWeb")
        connectedBefore = true
        displayStatusConnection("", true, false)
        webView.loadUrl(url)
    }

    private fun openDialog() {
        Log.d(TAG, "openDialog")
        val fm = (activity as AppCompatActivity).supportFragmentManager
        val wifiConnectDialog = WifiConnectDialog()
        // SETS the target fragment for use later when sending results
        wifiConnectDialog.setTargetFragment(this, 300)
        wifiConnectDialog.show(fm, "WifiConnectDialog")
    }

    private fun registerNetworkCallback() {
        val cm =
                context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        Log.d(TAG, "registerNetworkCallback")
        cm.registerNetworkCallback(
                Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .build(),
                object : NetworkCallback() {

                    override fun onLosing(network: Network, maxMsToLive: Int) {
                        super.onLosing(network, maxMsToLive)
                        Log.d(TAG, "onLosing")
                        if (connectedBefore) {
                            displayStatusConnection("déconnexion en cours")
                        }
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
//                        Log.d(TAG, "onLost")
//                        if (connectedBefore) {
//                            displayStatusConnection("connexion perdue", true, true)
//                        }
                    }

                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        Log.d(TAG, "onAvailable")
                        if (!connectedBefore) {
                            val connected = cm.bindProcessToNetwork(network)
                            if (connected) {
                                Log.d(TAG, "connected to wifi")
                                goForSsid()
                            }
                        }
                    }
                })
    }

    private class PrepareConnectToMySsidTask(activity: SosManagerFragment) :
            AsyncTask<String, String, Boolean>() {
        private val TAG = "toto_PrepareConnect"

        private val scanStopped = 0
        private val scanRun = 1
        private val scanResultOk = 2
        private var scanMySsidStatus = scanStopped
        private var userOk4ConnWifi = false
        private val activityWeakReference = WeakReference<SosManagerFragment>(activity)
        val wifiManager =
                activity.context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
            val activity = activityWeakReference.get() ?: return
            for (i in values.indices) when (i) {
                0 -> activity.statusView.text = values[i]
                1 -> if (values[i]?.toBoolean()!!) activity.indeterminateBar.visibility =
                        INVISIBLE
                //1 -> activity.indeterminateBar.isInvisible = values[i]?.toBoolean() ?: true
                2 -> if (values[i]?.toBoolean()!!) activity.statusView.visibility =
                        VISIBLE
                //2 -> activity.statusView.isVisible = values[i]?.toBoolean() ?: true
            }
            activity.btnConnect.visibility = INVISIBLE // isVisible = false
        }

        override fun doInBackground(vararg params: String?): Boolean {
            Log.d(TAG, "doInBackground")
            val activity = activityWeakReference.get() ?: return false
            //getMaxPriority()
            accesWifi()
            //acces au wifi
            publishProgress("Détection de SOS Manager", "false", "true")
            registerScanReceiver(activity) //activer wifiScanReceiver
            //scan périodiquement le wifi pour détecter SOS Manager (avec timeout)
            var timeOut = 150 // *0.2 = 30s
            while (scanMySsidStatus != scanResultOk) {
                if (scanMySsidStatus == scanStopped) {
                    scanWifi() //scanner wifi
                }
                Thread.sleep(200)
                if (--timeOut == 0) {
                    publishProgress("SOS Manager non détecté", "true", "true")
                    return false
                }
            }
            //si SOS manager détecté et non connecté, s'y connecter
            if (wifiManager.connectionInfo.ssid != networkSSID) {
                val wifiConfig = getWiFiConfig()
                Log.d(TAG, "disconnect")
                wifiManager.disconnect()
                wifiManager.enableNetwork(wifiConfig!!.networkId, true)
                Log.d(TAG, "reconnect ${wifiConfig.SSID}")
                wifiManager.reconnect()
            }
            return true
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            Log.d(TAG, "onPostExecute")
            val activity = activityWeakReference.get() ?: return
            Log.d(TAG, "unregisterReceiver wifiScanReceiver")
            activity.context!!.unregisterReceiver(wifiScanReceiver)
            if (result) {
                activity.registerNetworkCallback()
            } else {
                activity.btnConnect.visibility = VISIBLE
                //activity.btnConnect.isVisible = true

            }
        }

        fun getMaxPriority() {
            val configurations: List<WifiConfiguration> = wifiManager.getConfiguredNetworks()
            var pri = 0
            for (config in configurations) {
//                Log.d(TAG, "$config = ${config.priority} ")
                if (config.priority > pri) {
                    pri = config.priority;
                }
            }
            Log.d(TAG, "PRIO= $pri")
        }


        //Accède t-on au wifi
        private fun accesWifi() {
            Log.d(TAG, "accesWifi")
            if (!wifiManager.isWifiEnabled) {
                wifiManager.isWifiEnabled = true //active le wifi
                publishProgress("Activation du Wifi", "false")
            }
        }

        //enregistre wifiScanReceiver
        private fun registerScanReceiver(activity: SosManagerFragment) {
            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            Log.d(TAG, "registerReceiver wifiScanReceiver")
            activity.context!!.registerReceiver(wifiScanReceiver, intentFilter)
        }

        private fun scanWifi(): Boolean {
            Log.d(TAG, "start scanWifi")
            scanMySsidStatus = scanRun
            return wifiManager.startScan()
        }

        //recoit le resultat d'un scan et met à jour "scanMySsidStatus"
        private val wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val success =
                        intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                Log.d(TAG, "Scan terminé: $success")
                val results = wifiManager.scanResults
                for (result in results) {
                    if (networkSSID == "\"${result.SSID}\"") {
                        publishProgress("SOS Manager détecté", "false", "true")
                        scanMySsidStatus = scanResultOk
                        return
                    }
                }
                scanMySsidStatus = scanStopped
            }
        }

        private fun getWiFiConfig(): WifiConfiguration? {
            val wifiList = wifiManager.configuredNetworks
            if (wifiList != null) {
                for (wifiConfig in wifiList) {
                    if (wifiConfig.SSID != null && wifiConfig.SSID == networkSSID) {
                        Log.d(TAG, wifiConfig.toString())
                        return wifiConfig
                    }
                }
            }
            //if the given ssid is not present in the WiFiConfig, create a config for it
            val wifiConfig = WifiConfiguration()
            wifiConfig.SSID = networkSSID
            wifiConfig.preSharedKey = passPhrase
//            wifiConfig.priority = 99
            wifiManager.saveConfiguration()

            wifiManager.addNetwork(wifiConfig)
            Log.d(TAG, "saved SSID to WiFiManger")
            return wifiConfig
        }
    }

    private class RunConnectToMySsidTask(activity: SosManagerFragment) :
            AsyncTask<Void, String, Boolean>() {

        private val TAG = "toto_RunConnect"

        private val activityWeakReference = WeakReference<SosManagerFragment>(activity)
        val wifiManager =
                activity.context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
            val activity = activityWeakReference.get() ?: return
            for (i in values.indices) when (i) {
                0 -> activity.statusView.text = values[i]
                1 -> if (values[i]?.toBoolean()!!) activity.indeterminateBar.visibility =
                        INVISIBLE
                //1 -> activity.indeterminateBar.isInvisible = values[i]?.toBoolean() ?: true
                2 -> if (values[i]?.toBoolean()!!) activity.statusView.visibility =
                        VISIBLE
            }
            activity.btnConnect.visibility = INVISIBLE
            //activity.btnConnect.isVisible = false
        }

        override fun doInBackground(vararg params: Void?): Boolean {
            Log.d(TAG, "RunConnectToMySsidTask")
            val activity = activityWeakReference.get() ?: return false
            try {
                Log.d(
                        TAG,
                        "Wifi connected @ SSID ${wifiManager.connectionInfo.ssid}"
                )
                if (wifiManager.connectionInfo.ssid == networkSSID) {
                    publishProgress("SOS Manager connecté", "true", "true")
                    return true
                } else {
                    Log.d(TAG, "SOS Manager non connecté")
                    publishProgress("Echec de connexion à SOS Manager", "true", "true")
                    return false
                }
            } catch (e: InterruptedException) {
                Log.d(TAG, "catch")
                e.printStackTrace()
            }
            return false
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            Log.d(TAG, "onPostExecute")
            val activity = activityWeakReference.get() ?: return
            if (result) {
                activity.openWeb()
            } else {
                activity.btnConnect.visibility = VISIBLE
            }
        }
    }
}

