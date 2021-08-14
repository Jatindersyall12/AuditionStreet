package com.auditionstreet.artist.ui.projects.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auditionstreet.artist.BuildConfig
import com.auditionstreet.artist.R
import com.auditionstreet.artist.api.ApiConstant
import com.auditionstreet.artist.databinding.FragmentMyProjectsBinding
import com.auditionstreet.artist.model.response.MyProjectResponse
import com.auditionstreet.artist.storage.preference.Preferences
import com.auditionstreet.artist.ui.projects.adapter.MyProjectListAdapter
import com.auditionstreet.artist.ui.projects.viewmodel.MyProjectViewModel
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
class MyProjectsListingFragment : AppBaseFragment(R.layout.fragment_my_projects),
    View.OnClickListener {
    private val binding by viewBinding(FragmentMyProjectsBinding::bind)
    private lateinit var myProjectListAdapter: MyProjectListAdapter

    private val viewModel: MyProjectViewModel by viewModels()

    @Inject
    lateinit var preferences: Preferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        init()
    }

    private fun getMyProjects() {

        viewModel.getMyProject(
            BuildConfig.BASE_URL + ApiConstant.GET_MY_PROJECTS + "/" + preferences.getString(
                AppConstants.USER_ID
            )
        )
    }


    private fun setObservers() {
        viewModel.users.observe(viewLifecycleOwner, EventObserver {
            handleApiCallback(it)
        })
    }

    private fun handleApiCallback(apiResponse: Resource<Any>) {
        when (apiResponse.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiResponse.apiConstant) {
                    ApiConstant.GET_MY_PROJECTS -> {
                        setAdapter(apiResponse.data as MyProjectResponse)
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
        binding.rvProjects.apply {
            layoutManager = LinearLayoutManager(activity)
            myProjectListAdapter = MyProjectListAdapter(requireActivity())
            { projectId: String ->
                sharedViewModel.setDirection(
                    MyProjectsListingFragmentDirections.navigateToProjectDetail(
                        projectId
                    )
                )
            }
            adapter = myProjectListAdapter
        }
    }

    private fun setAdapter(projectResponse: MyProjectResponse) {
        if (projectResponse.data.size > 0) {
            myProjectListAdapter.submitList(projectResponse.data)
            binding.rvProjects.visibility = View.VISIBLE
            binding.layNoRecord.visibility = View.GONE

        } else {

            binding.rvProjects.visibility = View.GONE
            binding.layNoRecord.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {

    }

    override fun onResume() {
        super.onResume()
        getMyProjects()
    }
}