package com.auditionstreet.artist.ui.splash

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.auditionstreet.artist.databinding.ActivitySplashBinding
import com.auditionstreet.artist.storage.preference.Preferences
import com.auditionstreet.artist.ui.home.activity.HomeActivity
import com.auditionstreet.artist.ui.login_signup.AuthorizedUserActivity
import com.auditionstreet.artist.utils.AppConstants
import com.silo.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    private val binding by viewBinding(ActivitySplashBinding::inflate)

    @Inject
    lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.e("sdsd", printKeyHash(this)!!)
        lifecycleScope.launch {
            delay(3000)
            if (preferences.getString(AppConstants.USER_ID).isEmpty()) {
                val i = Intent(this@SplashActivity, AuthorizedUserActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(this@SplashActivity, HomeActivity::class.java)
                startActivity(i)
            }
            finish()
        }
    }

    fun printKeyHash(context: Activity): String? {
        val packageInfo: PackageInfo
        var key: String? = null
        try {
            //getting application package name, as defined in manifest
            val packageName: String = context.getApplicationContext().getPackageName()

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            Log.e("Package Name=", context.getApplicationContext().getPackageName())
            for (signature in packageInfo.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                key = String(Base64.encode(md.digest(), 0))

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("Name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("No such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
        return key
    }
}