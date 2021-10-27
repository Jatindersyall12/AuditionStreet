package com.auditionstreet.artist.ui.plan.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.auditionstreet.artist.BuildConfig
import com.auditionstreet.artist.R
import com.auditionstreet.artist.api.ApiConstant
import com.auditionstreet.artist.databinding.FragmentPlanListBinding
import com.auditionstreet.artist.databinding.FragmentProfileBinding
import com.auditionstreet.artist.model.response.*
import com.auditionstreet.artist.storage.preference.Preferences
import com.auditionstreet.artist.ui.home.activity.AllApplicationActivity
import com.auditionstreet.artist.ui.plan.adapter.PlansListAdapter
import com.auditionstreet.artist.ui.plan.viewmodel.PlansViewModel
import com.auditionstreet.artist.ui.profile.viewmodel.ProfileViewModel
import com.auditionstreet.artist.ui.projects.adapter.MyProjectListAdapter
import com.auditionstreet.artist.ui.projects.adapter.WorkListAdapter
import com.auditionstreet.artist.ui.projects.fragment.MyProjectsListingFragmentDirections
import com.auditionstreet.artist.ui.projects.viewmodel.MyProjectViewModel
import com.auditionstreet.artist.utils.*
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.google.gson.Gson
import com.leo.wikireviews.utils.livedata.EventObserver
import com.megamind.razorpay.RazorPayActivity
import com.silo.model.request.PurchasePlanRequest
import com.silo.model.request.WorkGalleryRequest
import com.silo.utils.AppBaseFragment
import com.silo.utils.network.Resource
import com.silo.utils.network.Status
import com.silo.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.set


@AndroidEntryPoint
class PlanListFragment : AppBaseFragment(R.layout.fragment_plan_list), View.OnClickListener {
    private val binding by viewBinding(FragmentPlanListBinding::bind)
    private lateinit var plansListAdapter: PlansListAdapter
    private var plansList = ArrayList<PlansListResponse.Data>()

    private val viewModel: PlansViewModel by viewModels()

    @Inject
    lateinit var preferences: Preferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        init()
    }

    private fun getPlans() {

        viewModel.getPlansList(
            BuildConfig.BASE_URL + ApiConstant.GET_ALL_PLANS + "/" + preferences.getString(
                AppConstants.USER_ID
            )
        )
    }


    private fun setObservers() {
        viewModel.users.observe(viewLifecycleOwner, EventObserver {
            handleApiCallback(it)
        })
        viewModel.purchasePlan.observe(viewLifecycleOwner, EventObserver {
            handleApiCallback(it)
        })
    }

    private fun handleApiCallback(apiResponse: Resource<Any>) {
        when (apiResponse.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiResponse.apiConstant) {
                    ApiConstant.GET_ALL_PLANS -> {
                        val planListResponse = apiResponse.data as PlansListResponse
                        plansList = planListResponse.data as ArrayList<PlansListResponse.Data>
                        setAdapter(apiResponse.data as PlansListResponse)
                    }
                    ApiConstant.PURCHASE_PLAN -> {
                        requireActivity().onBackPressed()
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
        binding.rvPlans.apply {
            layoutManager = LinearLayoutManager(activity)
            plansListAdapter = PlansListAdapter(requireActivity())
            { position: String ->
                preferences.setString(AppConstants.PLAN_ID,
                plansList[position.toInt()].id.toString())
                val intent = Intent(requireActivity(), RazorPayActivity::class.java)
                intent.putExtra(resources.getString(R.string.name),
                    preferences.getString(AppConstants.USER_NAME))
                intent.putExtra(resources.getString(R.string.email),
                    preferences.getString(AppConstants.USER_EMAIL))
                intent.putExtra(resources.getString(R.string.phone),
                    preferences.getString(AppConstants.PHONE_NUMBER))
                intent.putExtra(resources.getString(R.string.amount),
                plansList[position.toInt()].price)
                intent.putExtra(resources.getString(R.string.currency), "INR")
                launchRazorPayActivity.launch(intent)
            }
            adapter = plansListAdapter
        }
    }

    private fun setAdapter(plansListResponse: PlansListResponse) {
        if (plansListResponse.data.size > 0) {
            plansListAdapter.submitList(plansListResponse.data)
            binding.rvPlans.visibility = View.VISIBLE
            binding.layNoRecord.visibility = View.GONE

        } else {

            binding.rvPlans.visibility = View.GONE
            binding.layNoRecord.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {

    }

    override fun onResume() {
        super.onResume()
        getPlans()
    }

    var launchRazorPayActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data: Intent? = result.data
        if (result.resultCode == Activity.RESULT_OK) {
            data!!.getStringExtra(resources.getString(R.string.transaction_id))?.let { Log.e("message", it) }
            val purchasePlanRequest = PurchasePlanRequest()
            purchasePlanRequest.artistId = preferences.getString(AppConstants.USER_ID)
            purchasePlanRequest.planId = preferences.getString(AppConstants.PLAN_ID)
            purchasePlanRequest.transactionId =
                data!!.getStringExtra(resources.getString(R.string.transaction_id)).toString()
            purchasePlanRequest.paymentMode = "Credit Card"
            viewModel.purchasePlanApi(purchasePlanRequest)
        } else if (result.resultCode == Activity.RESULT_CANCELED){
            data!!.getStringExtra(resources.getString(R.string.error_message))?.let { Log.e("message", it) }
        }
    }

}