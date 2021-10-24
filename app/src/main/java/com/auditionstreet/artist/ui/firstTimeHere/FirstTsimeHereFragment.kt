package com.auditionstreet.castingagency.ui.firstTimeHere

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.auditionstreet.artist.R
import com.auditionstreet.artist.databinding.FragmentFirstTimeHereBinding
import com.auditionstreet.artist.model.FirstTimeHereModel
import com.auditionstreet.artist.storage.preference.Preferences
import com.auditionstreet.artist.ui.home.activity.HomeActivity
import com.auditionstreet.artist.utils.AppConstants
import com.auditionstreet.artist.utils.DataHelper
import com.auditionstreet.castingagency.ui.firstTimeHere.adapter.FirstTimeHereAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.silo.utils.AppBaseFragment
import com.silo.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FirstTsimeHereFragment : AppBaseFragment(R.layout.fragment_first_time_here), View.OnClickListener {
    private val binding by viewBinding(FragmentFirstTimeHereBinding::bind)
    private lateinit var pagerAdapter: FirstTimeHereAdapter
    private var mstate: Int=0

    @Inject
    lateinit var preferences: Preferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences.setBoolean(AppConstants.SECOND_TIME_HERE, true)
        init()
        setListner()
    }

    private fun setListner() {

        binding.firstTimePager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mstate=0
                if (position == binding.firstTimePager.adapter!!.itemCount - 1) {
                    binding.btnDone.visibility = View.VISIBLE
                    binding.btnNext.visibility = View.GONE
                    binding.btnSkip.visibility = View.GONE
                }
                else {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnDone.visibility = View.GONE
                    binding.btnSkip.visibility = View.VISIBLE
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                /*if(mstate==2&&position==9) {
                    val i = Intent(requireActivity(), HomeActivity::class.java)
                    startActivity(i)
                    requireActivity().finish()
                }*/
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                ++mstate;
            }
        })

        binding.btnSkip.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
        binding.btnDone.setOnClickListener(this)
        binding.imgBack.setOnClickListener(this)
    }

    fun init() {
        pagerAdapter = FirstTimeHereAdapter(requireActivity())
        binding.firstTimePager.adapter = pagerAdapter
        val firstTimeHereList = ArrayList<FirstTimeHereModel>()
        for (i in 0 until DataHelper.firstTimehere.size){
            val firstTimeHereModel = FirstTimeHereModel()
            firstTimeHereModel.firstTimeImage =  DataHelper.firstTimehere[i]
            firstTimeHereModel.firstTimeText = DataHelper.firstTimehereString[i]
            firstTimeHereList.add(firstTimeHereModel)
        }
        pagerAdapter.submitList(firstTimeHereList)
        TabLayoutMediator(binding.tabWalkThrough, binding.firstTimePager)
        { tab, position -> }.attach()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnSkip ->{
                val i = Intent(requireActivity(), HomeActivity::class.java)
                startActivity(i)
                requireActivity().finish()
            }
            R.id.btnNext ->{
                jumpToPage()
            }
            R.id.btnDone ->{
                val i = Intent(requireActivity(), HomeActivity::class.java)
                startActivity(i)
                requireActivity().finish()
            }
            R.id.imgBack ->{
                requireActivity().onBackPressed()
            }
        }
    }

    private fun jumpToPage() {
        binding.firstTimePager.setCurrentItem( binding.firstTimePager.getCurrentItem() + 1, true)
    }
}