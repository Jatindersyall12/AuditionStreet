package com.auditionstreet.artist.ui.projects.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.auditionstreet.artist.BuildConfig
import com.auditionstreet.artist.R
import com.auditionstreet.artist.api.ApiConstant
import com.auditionstreet.artist.databinding.FragmentMyProjectDetailBinding
import com.auditionstreet.artist.model.response.MyProjectDetailResponse
import com.auditionstreet.artist.storage.preference.Preferences
import com.auditionstreet.artist.ui.projects.viewmodel.MyProjectDetailViewModel
import com.auditionstreet.artist.utils.showToast
import com.leo.wikireviews.utils.livedata.EventObserver
import com.silo.utils.AppBaseFragment
import com.silo.utils.network.Resource
import com.silo.utils.network.Status
import com.silo.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyProjectDetailFragment : AppBaseFragment(R.layout.fragment_my_project_detail),
    View.OnClickListener {
    private val binding by viewBinding(FragmentMyProjectDetailBinding::bind)
    private val navArgs by navArgs<MyProjectDetailFragmentArgs>()

    private val viewModel: MyProjectDetailViewModel by viewModels()

    @Inject
    lateinit var preferences: Preferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
        getMyProjectDetail(navArgs.projectId)
    }

    private fun getMyProjectDetail(projectId: String) {
        /* preferences.getString(
            AppConstants.USER_ID)*/
        viewModel.getMyProjectDetail(
            BuildConfig.BASE_URL + ApiConstant.GET_MY_PROJECTS_DETAILS + "/" + projectId
        )
    }

    private fun setListeners() {
        // binding.btnAddProject.setOnClickListener(this)
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
                    ApiConstant.GET_MY_PROJECTS_DETAILS -> {
                        setDetail(apiResponse.data as MyProjectDetailResponse)
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
            else -> {

            }
        }
    }

    private fun setDetail(myProjectResponse: MyProjectDetailResponse) {
        binding.tvTitle.text = myProjectResponse.data[0].projectDetails.title
        binding.tvAgeDetail.text = myProjectResponse.data[0].projectDetails.age
        if (myProjectResponse.data[0].projectDetails.heightFt.isEmpty())
            binding.tvHeightDetail.text = resources.getString(R.string.str_empty)
        else
            binding.tvHeightDetail.text = myProjectResponse.data[0].projectDetails.heightFt+"."+
                    myProjectResponse.data[0].projectDetails.heightIn
       /* if (myProjectResponse.data[0].projectDetails.lang.isEmpty())
            binding.tvLanguageDetail.text = resources.getString(R.string.str_empty)
        else
            binding.tvLanguageDetail.text = myProjectResponse.data[0].projectDetails.lang*/
        var languages = ""
        for (i in 0 until myProjectResponse.data[0].projectDetails.lang.size){
                    languages += myProjectResponse.data[0].projectDetails.lang[i].name + " ,"
        }
        if (languages.length >= 1)
            binding.tvLanguageDetail.text = languages.substring(0, languages.length - 1)
        else
            binding.tvLanguageDetail.text = resources.getString(R.string.str_empty)
        if (myProjectResponse.data[0].projectDetails.fromDate.isEmpty())
            binding.tvDatesDetail.text = resources.getString(R.string.str_empty)
        else
            binding.tvDatesDetail.text =
                myProjectResponse.data[0].projectDetails.fromDate + resources.getString(R.string.str_to) + myProjectResponse.data[0].projectDetails.toDate
        if (myProjectResponse.data[0].projectDetails.location.isEmpty())
            binding.tvLocationDetail.text = resources.getString(R.string.str_empty)
        else
        binding.tvLocationDetail.text = myProjectResponse.data[0].projectDetails.location
        binding.tvDescDetail.text = myProjectResponse.data[0].projectDetails.description
    }

    override fun onClick(v: View?) {
        when (v) {
            //   binding.btnAddProject -> {

        }
    }
    //  }
}