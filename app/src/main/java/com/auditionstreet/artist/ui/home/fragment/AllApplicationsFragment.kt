package com.auditionstreet.artist.ui.home.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.auditionstreet.artist.BuildConfig
import com.auditionstreet.artist.R
import com.auditionstreet.artist.api.ApiConstant
import com.auditionstreet.artist.databinding.FragmentAllApplicationsBinding
import com.auditionstreet.artist.model.response.AddGroupResponse
import com.auditionstreet.artist.model.response.DeleteMediaResponse
import com.auditionstreet.artist.model.response.MyProjectResponse
import com.auditionstreet.artist.storage.preference.Preferences
import com.auditionstreet.artist.ui.home.activity.OtherUserProfileActivity
import com.auditionstreet.artist.ui.home.adapter.AllApplicationsAdapter
import com.auditionstreet.artist.ui.home.viewmodel.ProjectViewModel
import com.auditionstreet.artist.ui.projects.fragment.MyProjectsListingFragmentDirections
import com.auditionstreet.artist.utils.AppConstants
import com.auditionstreet.artist.utils.showPaymentDialog
import com.auditionstreet.artist.utils.showToast
import com.leo.wikireviews.utils.livedata.EventObserver
import com.megamind.razorpay.RazorPayActivity
import com.silo.model.request.AcceptRejectProjectRequest
import com.silo.model.request.ReportCastingRequest
import com.silo.utils.AppBaseFragment
import com.silo.utils.network.Resource
import com.silo.utils.network.Status
import com.silo.utils.viewbinding.viewBinding
import com.yuyakaido.android.cardstackview.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AllApplicationsFragment :   AppBaseFragment(R.layout.fragment_all_applications),
    CardStackListener, View.OnClickListener {
    private val binding by viewBinding(FragmentAllApplicationsBinding::bind)
    private lateinit var allApplicationsAdapter: AllApplicationsAdapter
    private var manager: CardStackLayoutManager ?= null
    private val viewModel: ProjectViewModel by viewModels()
    private lateinit var projectList: MyProjectResponse;
    private var cardPosition = 0
    private var applicationId = ""

    @Inject
    lateinit var preferences: Preferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applicationId = AppConstants.APPLICATIONID
        manager = CardStackLayoutManager(requireActivity(), this)
        setListeners()
        setObservers()
        if (manager != null)
        init()
        getAllApplications()
    }

    private fun setListeners() {
        binding.tvSelectProject.setOnClickListener(this)
    }

    private fun getAllApplications() {
        viewModel.getAllApplications(
            BuildConfig.BASE_URL + ApiConstant.GET_MY_PROJECTS + "/" + preferences.getString(
                AppConstants.USER_ID
            )
        )
    }

    private fun setObservers() {
        viewModel.allAppliactions.observe(viewLifecycleOwner, EventObserver {
            handleApiCallback(it)
        })
        viewModel.acceptRejectProject.observe(viewLifecycleOwner, EventObserver {
            handleApiCallback(it)
        })
        viewModel.reportCasting.observe(viewLifecycleOwner, EventObserver {
            handleApiCallback(it)
        })

    }

    private fun handleApiCallback(apiResponse: Resource<Any>) {
        when (apiResponse.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiResponse.apiConstant) {
                    ApiConstant.GET_MY_PROJECTS -> {
                        projectList = apiResponse.data as MyProjectResponse
                        setAdapter(apiResponse.data as MyProjectResponse)
                        if (!applicationId.isEmpty()){
                            var tempApplicationList = ArrayList<MyProjectResponse.Data>()
                            for (i in 0 until projectList.data.size){
                                if (projectList.data[i].id == applicationId.toInt()){
                                    tempApplicationList.add(projectList.data[i])
                                }
                            }
                            allApplicationsAdapter.submitList(tempApplicationList)
                        }
                    }
                    ApiConstant.ACCEPT_REJECT_PROJECT -> {
                        val response = apiResponse.data as AddGroupResponse
                        if (response.code == 306){
                           // showToast(requireContext(), response.msg)
                            showPaymentDialog(requireActivity(), response.msg){
                                sharedViewModel.setDirection(
                                    AllApplicationsFragmentDirections.navigateToPlansList()
                                )
                            }
                        }else{
                            projectList.data.removeAt(0)
                            allApplicationsAdapter.submitList(projectList.data)
                            manageCardAndTextViewVisibility()
                        }
                    }
                    ApiConstant.REPORT_CASTING -> {
                        val acceptRejectProjectRequest = AcceptRejectProjectRequest()
                        acceptRejectProjectRequest.id = projectList.data[cardPosition].id.toString()
                        acceptRejectProjectRequest.status = "2"
                        acceptRejectProjectRequest.userStatus = "1"
                        acceptRejectProjectRequest.artistId = preferences.getString(AppConstants.USER_ID)
                        viewModel.acceptRejectProject(acceptRejectProjectRequest)
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

    override fun onDestroy() {
        super.onDestroy()
        if (manager != null){
            manager = null
        }
    }

    private fun init() {
        binding.cardAllApplications.apply {
            allApplicationsAdapter = AllApplicationsAdapter(requireActivity())
            { position: Int ->
                if (position == 0){
                    AppConstants.CASTINGID = projectList.data[cardPosition].castingId.toString()
                    val i = Intent(requireActivity(), OtherUserProfileActivity::class.java)
                    startActivity(i)
                   /* val intent = Intent(requireActivity(), RazorPayActivity::class.java)
                    intent.putExtra(resources.getString(R.string.name), "Vishav")
                    intent.putExtra(resources.getString(R.string.email), "vishav@megamindcreations.com")
                    intent.putExtra(resources.getString(R.string.phone), "9815240558")
                    intent.putExtra(resources.getString(R.string.amount), "1")
                    intent.putExtra(resources.getString(R.string.currency), "INR")
                    launchRazorPayActivity.launch(intent)*/
                    /*sharedViewModel.setDirection(
                        AllApplicationsFragmentDirections.navigateToPlansList()
                    )*/
                }else if(position == 1){
                    val reportCastingRequest = ReportCastingRequest()
                    reportCastingRequest.artistId = preferences.getString(AppConstants.USER_ID)
                    reportCastingRequest.castingId = projectList.data[cardPosition].castingId.toString()
                    reportCastingRequest.message = "Fake Post"
                    viewModel.reportCasting(reportCastingRequest)
                }
            }
            adapter = allApplicationsAdapter
            manager!!.setStackFrom(StackFrom.None)
            manager!!.setVisibleCount(1)
            manager!!.setTranslationInterval(12.0f)
            manager!!.setScaleInterval(0.95f)
            manager!!.setSwipeThreshold(0.3f)
            manager!!.setMaxDegree(80.0f)
            manager!!.setDirections(Direction.HORIZONTAL)
            manager!!.setCanScrollHorizontal(true)
            manager!!.setCanScrollVertical(true)
            manager!!.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            manager!!.setOverlayInterpolator(LinearInterpolator())
            binding.cardAllApplications.layoutManager = manager
            binding.cardAllApplications.adapter = allApplicationsAdapter
            binding.cardAllApplications.itemAnimator.apply {
                if (this is DefaultItemAnimator) {
                    supportsChangeAnimations = false
                }
            }
        }
    }

    private fun setAdapter(projectResponse: MyProjectResponse) {
        if (projectResponse.data.size > 0) {
            allApplicationsAdapter.submitList(projectResponse.data)
        }
        manageCardAndTextViewVisibility()
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction?) {
        if (direction!!.name.equals("Left")) {
              val acceptRejectProjectRequest = AcceptRejectProjectRequest()
            acceptRejectProjectRequest.id = projectList.data[cardPosition].id.toString()
            acceptRejectProjectRequest.status = "2"
            acceptRejectProjectRequest.userStatus = "1"
            acceptRejectProjectRequest.artistId = preferences.getString(AppConstants.USER_ID)
            viewModel.acceptRejectProject(acceptRejectProjectRequest)
            showToast(requireActivity(), getString(R.string.application_rejected))
        }
        else {
            val acceptRejectProjectRequest = AcceptRejectProjectRequest()
            acceptRejectProjectRequest.id = projectList.data[cardPosition].id.toString()
            acceptRejectProjectRequest.status = "1"
            acceptRejectProjectRequest.userStatus = "1"
            acceptRejectProjectRequest.artistId = preferences.getString(AppConstants.USER_ID)
            viewModel.acceptRejectProject(acceptRejectProjectRequest)
            showToast(requireActivity(), getString(R.string.application_accepted))
        }
    }

    override fun onCardRewound() {
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
        cardPosition = position
        Log.e("Position", position.toString())
    }

    override fun onCardDisappeared(view: View?, position: Int) {
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.tvSelectProject -> {
               // showSelectProject()
            }
        }
    }

    private fun manageCardAndTextViewVisibility(){
        if (projectList.data.size > 0) {
            binding.cardAllApplications.visibility = View.VISIBLE
            binding.tvNoAppFound.visibility = View.GONE
        } else {
            binding.cardAllApplications.visibility = View.GONE
            binding.tvNoAppFound.visibility = View.VISIBLE
        }
    }

    var launchRazorPayActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data: Intent? = result.data
        if (result.resultCode == Activity.RESULT_OK) {
            data!!.getStringExtra(resources.getString(R.string.transaction_id))?.let { Log.e("message", it) }
        } else if (result.resultCode == Activity.RESULT_CANCELED){
            data!!.getStringExtra(resources.getString(R.string.error_message))?.let { Log.e("message", it) }
        }
    }

}
