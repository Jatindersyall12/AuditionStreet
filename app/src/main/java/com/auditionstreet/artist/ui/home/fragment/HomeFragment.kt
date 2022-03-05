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
import com.auditionstreet.artist.databinding.FragmentHomeBinding
import com.auditionstreet.artist.model.response.HomeApiResponse
import com.auditionstreet.artist.storage.preference.Preferences
import com.auditionstreet.artist.ui.home.activity.AllApplicationActivity
import com.auditionstreet.artist.ui.home.activity.OtherUserProfileActivity
import com.auditionstreet.artist.ui.home.activity.ShortlistedActivity
import com.auditionstreet.artist.ui.home.adapter.ApplicationListAdapter
import com.auditionstreet.artist.ui.home.adapter.HomeShortListAdapter
import com.auditionstreet.artist.ui.home.adapter.ProjectListAdapter
import com.auditionstreet.artist.ui.home.viewmodel.HomeViewModel
import com.auditionstreet.artist.ui.home.viewmodel.ProjectViewModel
import com.auditionstreet.artist.ui.projects.activity.ProfileActivity
import com.auditionstreet.artist.ui.projects.fragment.MyProjectsListingFragmentDirections
import com.auditionstreet.artist.utils.AppConstants
import com.auditionstreet.artist.utils.showToast
import com.auditionstreet.castingagency.ui.chat.DialogsActivity
import com.leo.wikireviews.utils.livedata.EventObserver
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser
import com.silo.utils.AppBaseFragment
import com.silo.utils.network.Resource
import com.silo.utils.network.Status
import com.silo.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val EXTRA_QB_USERS = "qb_users"
@AndroidEntryPoint
class HomeFragment : AppBaseFragment(R.layout.fragment_home), View.OnClickListener {
    private val binding by viewBinding(FragmentHomeBinding::bind)
    private lateinit var projectListAdapter: ProjectListAdapter
    private lateinit var applicationListAdapter: ApplicationListAdapter
    private lateinit var shortListAdapter: HomeShortListAdapter
    private var pendingProjectList = ArrayList<HomeApiResponse.Data.PendingRequest>()
    private var projectList = ArrayList<HomeApiResponse.Data.Project>()
    private var acceptedList = ArrayList<HomeApiResponse.Data.Accept>()

    private val viewModel: ProjectViewModel by viewModels()
    private val viewModelHome: HomeViewModel by viewModels()

    @Inject
    lateinit var preferences: Preferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
        init()
        getHomeScreenData()
        scrollToTop()
    }
    private fun scrollToTop() {
        binding.layScroll.post {
            binding.layScroll.fullScroll(View.FOCUS_UP)
        }
    }
    private fun getHomeScreenData(){
        viewModelHome.getHomeScreenData(
            BuildConfig.BASE_URL + ApiConstant.GET_HOME_DATA + preferences.getString(
                AppConstants.USER_ID
            )
        )
    }

    private fun setListeners() {
        binding.tvShortListMore.setOnClickListener(this)
        binding.tvViewAllApplication.setOnClickListener(this)
        binding.clCompleteProfileErrorBand.setOnClickListener(this)
    }


    private fun setObservers() {
        viewModel.users.observe(viewLifecycleOwner, EventObserver {
            handleApiCallback(it)
        })
        viewModelHome.getHomeScreenData.observe(viewLifecycleOwner, EventObserver {
            handleApiCallback(it)
        })
    }

    private fun handleApiCallback(apiResponse: Resource<Any>) {
        when (apiResponse.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiResponse.apiConstant) {
                    ApiConstant.GET_HOME_DATA ->{
                        val homeScreenDetailResponse = apiResponse.data as HomeApiResponse
                        pendingProjectList = homeScreenDetailResponse.data.pendingRequest as ArrayList<HomeApiResponse.Data.PendingRequest>
                        projectList = homeScreenDetailResponse.data.projectList as ArrayList<HomeApiResponse.Data.Project>
                        acceptedList = homeScreenDetailResponse.data.acceptList as ArrayList<HomeApiResponse.Data.Accept>
                        setAdapter(homeScreenDetailResponse)
                        setApplicationAdapter(homeScreenDetailResponse)
                        setShortListAdapter(homeScreenDetailResponse)
                        if (homeScreenDetailResponse.data.isprofileupdated.equals("false")){
                           binding.clCompleteProfileErrorBand.visibility = View.VISIBLE
                        }else{
                            binding.clCompleteProfileErrorBand.visibility = View.GONE
                        }
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

    private fun init() {
        binding.rvSlidingProject.apply {
            layoutManager = LinearLayoutManager(activity)
            projectListAdapter = ProjectListAdapter(requireActivity())
            { position: Int ->
                sharedViewModel.setDirection(
                    MyProjectsListingFragmentDirections.navigateToProjectDetail(
                        pendingProjectList[position].projectId
                    )
                )
            }
            adapter = projectListAdapter
            binding.rvSlidingProject.setLayoutManager(
                LinearLayoutManager(
                    requireActivity(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
        }

        binding.rvApplication.apply {
            layoutManager = LinearLayoutManager(activity)
            applicationListAdapter = ApplicationListAdapter(requireActivity())
            { position: Int ->
                AppConstants.APPLICATIONID = projectList[position].id.toString()
                val i = Intent(requireActivity(), AllApplicationActivity::class.java)
                startActivity(i)
               /* val i = Intent(requireActivity(), OtherUserProfileActivity::class.java)
                startActivity(i)*/
            }
            adapter = applicationListAdapter
            binding.rvApplication.setLayoutManager(
                LinearLayoutManager(
                    requireActivity(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
        }

        binding.rvShortlist.apply {
            layoutManager = LinearLayoutManager(activity)
            shortListAdapter = HomeShortListAdapter(requireActivity())
            { position: Int, isViewProfileClicked: Boolean ->
                if (isViewProfileClicked) {
                    AppConstants.CASTINGID = acceptedList!![position].castingId.toString()
                    val i = Intent(requireActivity(), OtherUserProfileActivity::class.java)
                    startActivity(i)
                }else{
                    loadChatUsersFromQB(acceptedList[position].castingEmail/*"akshit@gmail.com"*/)
                }
            }
            adapter = shortListAdapter
            binding.rvShortlist.setLayoutManager(
                LinearLayoutManager(
                    requireActivity(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
        }
    }

    private fun setAdapter(homeApiResponse: HomeApiResponse) {
        if (homeApiResponse.data.pendingRequest.size > 0) {
            projectListAdapter.submitList(homeApiResponse.data.pendingRequest)
            binding.rvSlidingProject.visibility = View.VISIBLE
            binding.tvNoProjectFound.visibility = View.GONE
        } else {
            binding.rvSlidingProject.visibility = View.GONE
             binding.tvNoProjectFound.visibility = View.VISIBLE
        }
    }

    private fun setApplicationAdapter(homeApiResponse: HomeApiResponse) {
        if (homeApiResponse.data.projectList.size > 0) {
            applicationListAdapter.submitList(homeApiResponse.data.projectList)
            binding.rvApplication.visibility = View.VISIBLE
            binding.tvNoProjectFoundCurrent.visibility = View.GONE
        } else {
            binding.rvApplication.visibility = View.GONE
             binding.tvNoProjectFoundCurrent.visibility = View.VISIBLE
             binding.tvViewAllApplication.visibility=View.GONE
        }
    }

    private fun setShortListAdapter(homeApiResponse: HomeApiResponse) {
        if (homeApiResponse.data.acceptList.size > 0) {
            shortListAdapter.submitList(homeApiResponse.data.acceptList)
            binding.rvShortlist.visibility = View.VISIBLE
            binding.tvNoProjectFoundSortListed.visibility = View.GONE
        } else {
            binding.rvShortlist.visibility = View.GONE
             binding.tvNoProjectFoundSortListed.visibility = View.VISIBLE
            binding.tvShortListMore.visibility=View.GONE

        }

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.tvShortListMore -> {
                val i = Intent(requireActivity(), ShortlistedActivity::class.java)
                // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
                // requireActivity().finish()
            }
            R.id.tvViewAllApplication -> {
                val i = Intent(requireActivity(), AllApplicationActivity::class.java)
                // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
                //  requireActivity().finish()
            }
            R.id.clCompleteProfileErrorBand ->{
                val intent = Intent(requireActivity(), ProfileActivity::class.java)
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }

    /**
     * Get Chat User List
     */

    private fun loadChatUsersFromQB(email: String) {
        loadUsersWithoutQuery(email)
    }

    private fun loadUsersWithoutQuery(email: String) {
        showProgress()
        QBUsers.getUserByLogin(email).performAsync(object : QBEntityCallback<QBUser> {
            override fun onSuccess(qbUser: QBUser, params: Bundle?) {
                hideProgress()
                Log.e("user", "yes")
                val i = Intent(requireActivity(), DialogsActivity::class.java)
                i.putExtra(EXTRA_QB_USERS, qbUser)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
                activity!!.finish()
            }

            override fun onError(e: QBResponseException) {
                hideProgress()
                Log.e("user", "No")
                showToast(requireActivity(),"No User Found")           }
        })
    }
}