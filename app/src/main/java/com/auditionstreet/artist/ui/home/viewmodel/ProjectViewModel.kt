package com.auditionstreet.artist.ui.home.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auditionstreet.artist.R
import com.auditionstreet.artist.api.ApiConstant
import com.auditionstreet.artist.model.response.AddGroupResponse
import com.auditionstreet.artist.model.response.MyProjectResponse
import com.auditionstreet.artist.model.response.ProjectResponse
import com.auditionstreet.artist.ui.home.repository.ProjectRepository
import com.leo.wikireviews.utils.livedata.Event
import com.silo.model.request.AcceptRejectProjectRequest
import com.silo.model.request.LoginRequest
import com.silo.model.request.ProjectRequest
import com.silo.model.request.ReportCastingRequest
import com.silo.utils.network.NetworkHelper
import com.silo.utils.network.Resource
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class ProjectViewModel @ViewModelInject constructor(
    private val projectRepository: ProjectRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel() {
    val loginRequest = LoginRequest()

    private val _users = MutableLiveData<Event<Resource<ProjectResponse>>>()
    val users: LiveData<Event<Resource<ProjectResponse>>>
        get() = _users

    private val _accept_reject_project = MutableLiveData<Event<Resource<AddGroupResponse>>>()
    val acceptRejectProject: LiveData<Event<Resource<AddGroupResponse>>>
        get() = _accept_reject_project

    private val all_applications = MutableLiveData<Event<Resource<MyProjectResponse>>>()
    val allAppliactions: LiveData<Event<Resource<MyProjectResponse>>>
        get() = all_applications

    private val _report_casting = MutableLiveData<Event<Resource<AddGroupResponse>>>()
    val reportCasting: LiveData<Event<Resource<AddGroupResponse>>>
        get() = _report_casting

    private fun getProject(projectRequest: ProjectRequest) {
        viewModelScope.launch {
            _users.postValue(Event(Resource.loading(ApiConstant.GET_PROJECTS, null)))
            if (networkHelper.isNetworkConnected()) {
                projectRepository.getProjects(projectRequest).let {
                    if (it.isSuccessful && it.body() != null) {
                        _users.postValue(
                            Event(
                                Resource.success(
                                    ApiConstant.GET_PROJECTS,
                                    it.body()
                                )
                            )
                        )
                    } else {
                        _users.postValue(
                            Event(
                                Resource.error(
                                    ApiConstant.GET_PROJECTS,
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

    fun getAllApplications(url: String) {
        viewModelScope.launch {
            all_applications.postValue(Event(Resource.loading(ApiConstant.GET_MY_PROJECTS, null)))
            if (networkHelper.isNetworkConnected()) {
                projectRepository.getMyProjects(url).let {
                    if (it.isSuccessful && it.body() != null) {
                        all_applications.postValue(
                            Event(
                                Resource.success(
                                    ApiConstant.GET_MY_PROJECTS,
                                    it.body()
                                )
                            )
                        )
                    } else {
                        all_applications.postValue(
                            Event(
                                Resource.error(
                                    ApiConstant.GET_MY_PROJECTS,
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

     fun acceptRejectProject(acceptRejectProjectRequest: AcceptRejectProjectRequest) {
        viewModelScope.launch {
            try {
                withTimeout(ApiConstant.API_TIMEOUT) {
                    _accept_reject_project.postValue(
                        Event(
                            Resource.loading(
                                ApiConstant.ACCEPT_REJECT_PROJECT,
                                null
                            )
                        )
                    )
                    if (networkHelper.isNetworkConnected()) {
                        projectRepository.acceptRejectProject(acceptRejectProjectRequest).let {
                            if (it.isSuccessful && it.body() != null) {
                                _accept_reject_project.postValue(
                                    Event(
                                        Resource.success(
                                            ApiConstant.ACCEPT_REJECT_PROJECT,
                                            it.body()
                                        )
                                    )
                                )
                            } else {
                                _accept_reject_project.postValue(
                                    Event(
                                        Resource.error(
                                            ApiConstant.ACCEPT_REJECT_PROJECT,
                                            it.code(),
                                            it.errorBody().toString(),
                                            null
                                        )
                                    )
                                )
                            }
                        }
                    } else {
                        _accept_reject_project.postValue(
                            Event(
                                Resource.requiredResource(
                                    ApiConstant.ACCEPT_REJECT_PROJECT,
                                    R.string.err_no_network_available
                                )
                            )
                        )
                    }
                }
            }catch (e: Exception) {
                _accept_reject_project.postValue(
                    Event(
                        Resource.error(
                            ApiConstant.ACCEPT_REJECT_PROJECT,
                            ApiConstant.STATUS_500,
                            "",
                            null
                        )
                    )
                )
            }
        }
    }


    fun reportCasting(reportCastingRequest: ReportCastingRequest) {
        viewModelScope.launch {
            try {
                withTimeout(ApiConstant.API_TIMEOUT) {
                    _report_casting.postValue(
                        Event(
                            Resource.loading(
                                ApiConstant.REPORT_CASTING,
                                null
                            )
                        )
                    )
                    if (networkHelper.isNetworkConnected()) {
                        projectRepository.reportCasting(reportCastingRequest).let {
                            if (it.isSuccessful && it.body() != null) {
                                _report_casting.postValue(
                                    Event(
                                        Resource.success(
                                            ApiConstant.REPORT_CASTING,
                                            it.body()
                                        )
                                    )
                                )
                            } else {
                                _report_casting.postValue(
                                    Event(
                                        Resource.error(
                                            ApiConstant.REPORT_CASTING,
                                            it.code(),
                                            it.errorBody().toString(),
                                            null
                                        )
                                    )
                                )
                            }
                        }
                    } else {
                        _report_casting.postValue(
                            Event(
                                Resource.requiredResource(
                                    ApiConstant.REPORT_CASTING,
                                    R.string.err_no_network_available
                                )
                            )
                        )
                    }
                }
            }catch (e: Exception) {
                _report_casting.postValue(
                    Event(
                        Resource.error(
                            ApiConstant.REPORT_CASTING,
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
