package com.shiki.kisanfintech.ui.main

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.shiki.kisanfintech.R
import com.shiki.kisanfintech.databinding.ActivityLanguageBinding
import com.shiki.kisanfintech.databinding.ActivityMainBinding
import com.shiki.kisanfintech.ui.About.AboutFragment
import com.shiki.kisanfintech.ui.home_page.HomePage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    private val readExternal=READ_EXTERNAL_STORAGE
    private val readImage= READ_MEDIA_IMAGES
    private val readNotification= POST_NOTIFICATIONS

    private var _binding: ActivityMainBinding?= null
    private val binding get()=_binding!!

    private val permissions= arrayOf(
        readExternal,readImage
    )

    val readNotificationPermission =registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
    { permissionMap->
        if (permissionMap.all { it.value }){
            Toast.makeText(this, "Notification permissions granted", Toast.LENGTH_SHORT).show()
        }
    }
    val readExternalPermission=registerForActivityResult(ActivityResultContracts.RequestPermission())
    {isGranted->
        if (isGranted){
            readNotificationPermission.launch(arrayOf(readNotification))
        }else{
            Toast.makeText(this, "Read external storage permission denied!", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel= ViewModelProvider(this).get(MainViewModel::class.java)

        requestPermissions()
        navHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment

        binding.bottomNavigation.setOnItemSelectedListener {
            handleBottomNavigation(it.itemId)
        }
        binding.bottomNavigation.selectedItemId = R.id.homePage

    }

    private fun handleBottomNavigation(itemId: Int): Boolean = when(itemId) {
        R.id.homePage -> {
            swapFragments(HomePage())
            true
        }

        R.id.aboutFragment -> {
            swapFragments(AboutFragment())
            true
        }

        else -> false
    }

    private fun swapFragments(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.my_nav_host_fragment, fragment)
            .commit()
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notGrantedPermissions = permissions.filterNot { permission ->
                ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }
            if (notGrantedPermissions.isNotEmpty()) {
                val showRationale = notGrantedPermissions.any { permission ->
                    ActivityCompat.shouldShowRequestPermissionRationale(this,permission)
                }
                if (showRationale) {
                    AlertDialog.Builder(this)
                        .setTitle("Storage Permission")
                        .setMessage("Storage permission is needed in order to show images and videos")
                        .setNegativeButton("Cancel") { dialog, _ ->
                            /* Toast.makeText(
                                 this,
                                 "Read media storage permission denied!",
                                 Toast.LENGTH_SHORT
                             ).show()*/
                            dialog.dismiss()
                        }
                } else {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            permissions.toString()
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        /*Toast.makeText(
                            this,
                            "Read external storage permission granted",
                            Toast.LENGTH_SHORT
                        ).show()*/
                    } else
                    {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, readImage)) {
                            AlertDialog.Builder(this)
                                .setTitle("Storage Permission")
                                .setMessage("Storage permission is needed in order to show images and video")
                                .setNegativeButton("Cancel") { dialog, _ ->
                                    Toast.makeText(
                                        this,
                                        "Read external storage permission denied!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    dialog.dismiss()
                                }
                                .setPositiveButton("OK") { _, _ ->
                                    readExternalPermission.launch(readImage)
                                }
                                .show()
                        } else {
                            readExternalPermission.launch(readImage)
                        }

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, readNotification)) {
                            AlertDialog.Builder(this)
                                .setTitle("Notifivation Permission")
                                .setMessage("Notifcation permission is needed in order to show notification")
                                .setNegativeButton("Cancel") { dialog, _ ->
                                    Toast.makeText(
                                        this,
                                        "Notification permission denied!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    dialog.dismiss()
                                }
                                .setPositiveButton("OK") { _, _ ->
                                    readExternalPermission.launch(readNotification)
                                }
                                .show()
                        } else {
                            readExternalPermission.launch(readNotification)
                        }
                    }
                }
            }
        }
    }

}