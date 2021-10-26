package com.auditionstreet.artist.ui.plan.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auditionstreet.artist.R
import com.auditionstreet.artist.api.ApiConstant
import com.auditionstreet.artist.model.response.DeleteMediaResponse
import com.auditionstreet.artist.model.response.MyProjectResponse
import com.auditionstreet.artist.model.response.PlansListResponse
import com.auditionstreet.artist.ui.plan.repository.PlansRepository
import com.auditionstreet.artist.ui.projects.repository.MyProjectRepository
import com.leo.wikireviews.utils.livedata.Event
import com.silo.model.request.AcceptRejectProjectRequest
import com.silo.model.request.PurchasePlanRequest
import com.silo.utils.network.NetworkHelper
import com.silo.utils.network.Resource
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class PlansViewModel @ViewModelInject constructor(
    private val plansRepository: PlansRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel() {

    private val _users = MutableLiveData<Event<Resource<PlansListResponse>>>()
    val users: LiveData<Event<Resource<PlansListResponse>>>
        get() = _users

    private val _purchase_plan = MutableLiveData<Event<Resource<DeleteMediaResponse>>>()
    val purchasePlan: LiveData<Event<Resource<DeleteMediaResponse>>>
        get() = _purchase_plan

     fun getPlansList(url: String) {
        viewModelScope.launch {
            _users.postValue(Event(Resource.loading(ApiConstant.GET_ALL_PLANS, null)))
            if (networkHelper.isNetworkConnected()) {
                plansRepository.getPlansList(url).let {
                    if (it.isSuccessful && it.body() != null) {
                        _users.postValue(
                            Event(
                                Resource.success(
                                    ApiConstant.GET_ALL_PLANS,
                                    it.body()
                                )
                            )
                        )
                    } else {
                        _users.postValue(
                            Event(
                                Resource.error(
                                    ApiConstant.GET_ALL_PLANS,
                                    it.code(),
                                    it.errorBody().toString(),
                                    null
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    fun purchasePlanApi(purchasePlanRequest: PurchasePlanRequest) {
        viewModelScope.launch {
            try {
                withTimeout(ApiConstant.API_TIMEOUT) {
                    _purchase_plan.postValue(
                        Event(
                            Resource.loading(
                                ApiConstant.PURCHASE_PLAN,
                                null
                            )
                        )
                    )
                    if (networkHelper.isNetworkConnected()) {
                        plansRepository.acceptRejectProject(purchasePlanRequest).let {
                            if (it.isSuccessful && it.body() != null) {
                                _purchase_plan.postValue(
                                    Event(
                                        Resource.success(
                                            ApiConstant.PURCHASE_PLAN,
                                            it.body()
                                        )
                                    )
                                )
                            } else {
                                _purchase_plan.postValue(
                                    Event(
                                        Resource.error(
                                            ApiConstant.PURCHASE_PLAN,
                                            it.code(),
                                            it.errorBody().toString(),
                                            null
                                        )
                                    )
                                )
                            }
                        }
                    } else {
                        _purchase_plan.postValue(
                            Event(
                                Resource.requiredResource(
                                    ApiConstant.PURCHASE_PLAN,
                                    R.string.err_no_network_available
                                )
                            )
                        )
                    }
                }
            }catch (e: Exception) {
                _purchase_plan.postValue(
                    Event(
                        Resource.error(
                            ApiConstant.PURCHASE_PLAN,
                            ApiConstant.STATUS_500,
                            "",
                            null
                        )
                    )
                )
            }
        }
    }
}
