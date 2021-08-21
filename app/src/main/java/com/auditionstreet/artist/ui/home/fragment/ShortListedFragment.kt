package com.auditionstreet.artist.ui.home.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.auditionstreet.artist.BuildConfig
import com.auditionstreet.artist.R
import com.auditionstreet.artist.api.ApiConstant
import com.auditionstreet.artist.databinding.FragmentShortListBinding
import com.auditionstreet.artist.model.response.ProjectResponse
import com.auditionstreet.artist.storage.preference.Preferences
import com.auditionstreet.artist.ui.home.activity.OtherUserProfileActivity
import com.auditionstreet.artist.ui.home.adapter.ShortListAdapter
import com.auditionstreet.artist.ui.home.viewmodel.ProjectViewModel
import com.auditionstreet.artist.utils.AppConstants
import com.auditionstreet.artist.utils.showToast
import com.leo.wikireviews.utils.livedata.EventObserver
import com.silo.utils.AppBaseFragment
import com.silo.utils.network.Resource
import com.silo.utils.network.Status
import com.silo.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShortListedFragment : AppBaseFragment(R.layout.fragment_short_list) {
    private val binding by viewBinding(FragmentShortListBinding::bind)
    private lateinit var shortListAdapter: ShortListAdapter
    private val viewModel: ProjectViewModel by viewModels()
    private var shortListedList: ArrayList<ProjectResponse.Data> ?= null

    @Inject
    lateinit var preferences: Preferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shortListedList = ArrayList()
        setListeners()
        setObservers()
        init()
        getProjectList()
    }

    private fun setListeners() {
    }


    private fun setObservers() {
        viewModel.users.observe(viewLifecycleOwner, EventObserver {
            handleApiCallback(it)
        })
    }

    private fun getProjectList(){
        viewModel.getProject(
            BuildConfig.BASE_URL + ApiConstant.GET_SHORTLISTED_LIST + "/" + preferences.getString(
                AppConstants.USER_ID
            )
        )
    }

    private fun handleApiCallback(apiResponse: Resource<Any>) {
        when (apiResponse.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiResponse.apiConstant) {
                    ApiConstant.GET_SHORTLISTED_LIST -> {
                        val response = apiResponse.data as ProjectResponse
                        setAdapter(apiResponse.data as ProjectResponse)
                        shortListedList = response.data
                    }
                }
            }
            Status.LOADING -> {
                showProgress()
            }
            Status.ERROR -> {
                hideProgress()
                showToast(requireContext(), apiResponse.message!!)
            }
            Status.RESOURCE -> {
                hideProgress()
                showToast(requireContext(), getString(apiResponse.resourceId!!))
            }
        }
    }

    private fun init() {
        binding.rvShortList.apply {
            layoutManager = LinearLayoutManager(activity)
            shortListAdapter = ShortListAdapter(requireActivity())
            { position: Int ->
                AppConstants.CASTINGID = shortListedList!![position].castingId.toString()
                val i = Intent(requireActivity(), OtherUserProfileActivity::class.java)
                startActivity(i)
            }
            adapter = shortListAdapter
        }
    }

    private fun setAdapter(projectResponse: ProjectResponse) {
        if (projectResponse.data.size > 0) {
            shortListAdapter.submitList(projectResponse.data)
            binding.rvShortList.visibility = View.VISIBLE
            binding.tvNoDataFound.visibility = View.GONE
        } else {
            binding.rvShortList.visibility = View.GONE
            binding.tvNoDataFound.visibility = View.VISIBLE
        }
    }
}