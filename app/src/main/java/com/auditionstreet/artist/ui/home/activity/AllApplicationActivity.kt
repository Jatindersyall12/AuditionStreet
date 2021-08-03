package com.auditionstreet.artist.ui.home.activity

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.auditionstreet.artist.R
import com.auditionstreet.artist.databinding.ActivityAllApplicationsBinding
import com.auditionstreet.artist.storage.preference.Preferences
import com.auditionstreet.artist.utils.AppConstants
import com.bumptech.glide.Glide
import com.silo.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

@AndroidEntryPoint
class AllApplicationActivity : BaseActivity() {
    private val binding by viewBinding(ActivityAllApplicationsBinding::inflate)

    @Inject
    lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setNavigationController()
       // setUpToolbar()
    }

    override fun onBackPressed() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostAllApplicationsFragment) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        if (navController.graph.startDestination == navController.currentDestination?.id) {
            val i = Intent(this, HomeActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        } else
            super.onBackPressed()
    }

    private fun setNavigationController() {
        val navController =
            (supportFragmentManager.findFragmentById(R.id.navHostAllApplicationsFragment) as NavHostFragment)
                .navController
        sharedViewModel.navDirectionLiveData.observe(this) {
            navController.navigate(it)
        }
    }

    private fun setUpToolbar() {
        setUpToolbar(toolbar, getString(R.string.str_home), true, true)
        if (preferences.getString(AppConstants.USER_IMAGE).isEmpty())
            toolBarImage.setImageResource(R.drawable.ic_dummy_profile_image)
        else
            Glide.with(this).load(preferences.getString(AppConstants.USER_IMAGE))
                .into(toolBarImage)
        imgBack.setOnClickListener {
            finish()
            /*val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)*/
        }
        toolBarImage.setOnClickListener {
            val i = Intent(this, OtherUserProfileActivity::class.java)
            // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        }
    }
}