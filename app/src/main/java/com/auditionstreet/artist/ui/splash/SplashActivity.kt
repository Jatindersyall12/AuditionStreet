package com.auditionstreet.artist.ui.splash

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.auditionstreet.artist.databinding.ActivitySplashBinding
import com.auditionstreet.artist.storage.preference.Preferences
import com.auditionstreet.artist.ui.home.activity.FirstTimeHereActivity
import com.auditionstreet.artist.ui.home.activity.HomeActivity
import com.auditionstreet.artist.ui.login_signup.AuthorizedUserActivity
import com.auditionstreet.artist.utils.AppConstants
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
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
        // Get firebase token after logout
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String ->
            if (!TextUtils.isEmpty(token)) {
                Log.d("Token", "retrieve token successful : $token")
                if (preferences.getString(AppConstants.FIREBASE_ID).isNullOrEmpty()){
                    preferences.setString(AppConstants.FIREBASE_ID, token)
                }
            } else {
                Log.w("Token", "token should not be null...")
            }
        }.addOnFailureListener { e: java.lang.Exception? -> }.addOnCanceledListener {}
            .addOnCompleteListener { task: Task<String> ->
                Log.v(
                    "Token",
                    "This is the token : " + task.result
                )
            }

        lifecycleScope.launch {
            delay(3000)
            if (preferences.getString(AppConstants.USER_ID).isEmpty()) {
                val i = Intent(this@SplashActivity, AuthorizedUserActivity::class.java)
                startActivity(i)
            } else {
                if (!preferences.getBoolean(AppConstants.SECOND_TIME_HERE)){
                    val i = Intent(this@SplashActivity, FirstTimeHereActivity::class.java)
                    startActivity(i)
                }else{
                    val i = Intent(this@SplashActivity, HomeActivity::class.java)
                    startActivity(i)
                }
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