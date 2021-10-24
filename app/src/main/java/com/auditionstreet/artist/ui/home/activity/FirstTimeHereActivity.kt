package com.auditionstreet.artist.ui.home.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.auditionstreet.artist.R
import com.auditionstreet.artist.databinding.ActivityFirstTimeHereBinding
import com.auditionstreet.artist.storage.preference.Preferences
import com.silo.ui.base.BaseActivity
import com.silo.utils.changeIcons
import com.silo.utils.network.IconPosition
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

@AndroidEntryPoint
class FirstTimeHereActivity : BaseActivity() {
    private val binding by viewBinding(ActivityFirstTimeHereBinding::inflate)

    @Inject
    lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setNavigationController()
    }

    override fun onBackPressed() {
            super.onBackPressed()
    }

    private fun setNavigationController() {
        val navController =
            (supportFragmentManager.findFragmentById(R.id.navhostfirstTimeHere) as NavHostFragment)
                .navController
        sharedViewModel.navDirectionLiveData.observe(this) {
            navController.navigate(it)
        }
    }
}