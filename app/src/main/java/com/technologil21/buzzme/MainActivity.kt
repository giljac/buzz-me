package com.technologil21.buzzme

import android.Manifest
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_sos_manager.*
import kotlinx.android.synthetic.main.toolbar.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "toto_main"
    private val SMS_PERM = 123
    private var isGrted = false
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private var mPreviousMenuItem: MenuItem? = null
    var idFrag: Int = R.id.home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate")

        // Set a Toolbar to replace the ActionBar.
        setSupportActionBar(toolbar as Toolbar)

        drawerToggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close)
        // Tie DrawerLayout events to the ActionBarToggle
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.isDrawerIndicatorEnabled = true

        nav_view.setNavigationItemSelectedListener(this)

        checkMyPermissions()
        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment.newInstance()).commit()
            mPreviousMenuItem = nav_view.menu.findItem(R.id.home)
            nav_view.setCheckedItem(R.id.home)
        }
    }

//    private fun setupDrawerToggle(): ActionBarDrawerToggle {
//        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
//        // and will not render the hamburger icon without it.
//        return ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close)
//    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
//    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
//        super.onPostCreate(savedInstanceState, persistentState)
//    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

//        txtApp.text = R.string.app_name.toString() + " version " + R.string.version_num
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        replaceWith(menuItem)
        // Highlight the selected item has been done by NavigationView
        mPreviousMenuItem?.let { it.isChecked = false }
        menuItem.isChecked = true
        mPreviousMenuItem = menuItem
        // Set action bar title
        title = menuItem.title
        // Close the navigation drawer
        drawer.closeDrawers()
        return true
    }

    private fun replaceWith(menuItem: MenuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        val selectedFragment: Fragment
        toolbar.visibility = VISIBLE
        idFrag = menuItem.itemId
        when (idFrag) {
            R.id.home -> selectedFragment = HomeFragment.newInstance()
            R.id.nav_renvoi -> selectedFragment = AlertListFragment()
            R.id.nav_notif -> selectedFragment = NotificationFragment()
            R.id.nav_sos -> {
                selectedFragment = SosManagerFragment()//;toolbar.visibility = GONE
            }
            else -> selectedFragment = HomeFragment.newInstance()
        }
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
    }

    override fun onBackPressed() {
        //if isDrawerOpen ->  closeDrawer
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Log.d(TAG, "isDrawerOpen")
            drawer.closeDrawer(GravityCompat.START)
        }
        //sinon
        else {
            //si navigation web
            if (idFrag == R.id.nav_sos) {
                if (webView.canGoBack()) {
                    Log.d(TAG, "find sos")
                    webView.goBack()
                    return
                } else if (toolbar.visibility == GONE) {
                    //toolbar.isGone = false
                    toolbar.visibility = VISIBLE
                    return
                }
            }
        }
        super.onBackPressed()
    }

    /*  Gestion des permissions
    * Pour permettre la surveillance et l'envoi des SMS d'alerte */
    //gestion des resultats de demande de permissions par EasyPermissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //on verifie qu'on a les permissions necessaires a l'appli
    private fun checkMyPermissions() {
        val perms = arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_NOTIFICATION_POLICY, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            isGrted = true
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale), SMS_PERM, *perms)
        }
    }

    //resultat positif a une demande de permission
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        isGrted = true
    }

    //resultat negatif a une demande de permission
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            isGrted = false
            AppSettingsDialog.Builder(this).build().show()
            if (!isGrted) finish()
        }
    }
}
