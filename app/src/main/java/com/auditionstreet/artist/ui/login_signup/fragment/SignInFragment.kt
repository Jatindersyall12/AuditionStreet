package com.auditionstreet.artist.ui.login_signup.fragment

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.auditionstreet.artist.R
import com.auditionstreet.artist.USER_DEFAULT_PASSWORD
import com.auditionstreet.artist.api.ApiConstant
import com.auditionstreet.artist.databinding.FragmentSigninBinding
import com.auditionstreet.artist.storage.preference.Preferences
import com.auditionstreet.artist.ui.home.activity.FirstTimeHereActivity
import com.auditionstreet.artist.ui.home.activity.HomeActivity
import com.auditionstreet.artist.ui.login_signup.viewmodel.LoginViewModel
import com.auditionstreet.artist.utils.AppConstants
import com.auditionstreet.artist.utils.showProgressDialog
import com.auditionstreet.artist.utils.showToast
import com.auditionstreet.castingagency.utils.chat.ChatHelper
import com.auditionstreet.castingagency.utils.chat.QbUsersHolder
import com.auditionstreet.castingagency.utils.chat.SharedPrefsHelper
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser
import com.silo.model.request.LoginRequest
import com.silo.model.response.LoginResponse
import com.silo.utils.AppBaseFragment
import com.silo.utils.network.Resource
import com.silo.utils.network.Status
import com.silo.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import javax.inject.Inject

private const val UNAUTHORIZED = 401
private const val DRAFT_LOGIN = "draft_login"
private const val DRAFT_USERNAME = "draft_username"
@AndroidEntryPoint
class SignInFragment : AppBaseFragment(R.layout.fragment_signin), View.OnClickListener {
    private val binding by viewBinding(FragmentSigninBinding::bind)
    private val viewModel: LoginViewModel by viewModels()
    private var callbackManager: CallbackManager? = null
    private var email = ""

    @Inject
    lateinit var preferences: Preferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get firebase token after logout
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String ->
            if (!TextUtils.isEmpty(token)) {
                Log.d("Token", "retrieve token successful : $token")
                if (preferences.getString(AppConstants.FIREBASE_ID).isNullOrEmpty()){
                    preferences.setString(AppConstants.FIREBASE_ID, token)
                }
            } else {
                Log.w("Token", "token should not be null...")
            }
        }.addOnFailureListener { e: java.lang.Exception? -> }.addOnCanceledListener {}
            .addOnCompleteListener { task: Task<String> ->
                Log.v(
                    "Token",
                    "This is the token : " + task.result
                )
            }
        LoginManager.getInstance().logOut()
        initializeFacebookCallback()
        setListeners()
        setObservers()
    }


    private fun setListeners() {
        binding.btnSignIn.setOnClickListener(this)
        binding.tvDontHaveAcocunt.setOnClickListener(this)
        binding.imgFacebook.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnSignIn -> {
                email =  binding.etxEmail.text!!.trim().toString()
                viewModel.isValidate(
                    preferences.getString(AppConstants.FIREBASE_ID),
                    binding.etxEmail.text!!.trim().toString(),
                    binding.etxPassword.text!!.trim().toString(),
                    resources.getString(R.string.str_false)
                )
            }
            binding.tvDontHaveAcocunt -> {
                sharedViewModel.setDirection(SignInFragmentDirections.navigateToSignup())
            }
            binding.imgFacebook -> {
                fbLogin()
            }
        }
    }

    private fun setObservers() {
        viewModel.users.observe(viewLifecycleOwner, {
            handleApiCallback(it)
        })
    }

    private fun handleApiCallback(apiResponse: Resource<Any>) {
        when (apiResponse.status) {
            Status.SUCCESS -> {
                this.hideProgress()
                when (apiResponse.apiConstant) {
                    ApiConstant.LOGIN -> {
                        val loginResponse = apiResponse.data as LoginResponse
                        showToast(requireActivity(), loginResponse.msg.toString())
                        preferences.setString(
                            AppConstants.USER_ID,
                            loginResponse.data!![0]!!.id.toString()
                        )
                        preferences.setString(
                            AppConstants.USER_IMAGE,
                            loginResponse.data[0]!!.image.toString()
                        )
                        preferences.setString(
                            AppConstants.PHONE_NUMBER,
                            loginResponse.data[0]!!.phoneNumber
                        )
                        preferences.setString(
                            AppConstants.USER_EMAIL,
                            loginResponse.data[0]!!.email
                        )
                        preferences.setString(
                            AppConstants.USER_NAME,
                            loginResponse.data[0]!!.name
                        )
                        // CHat Login
                        prepareUser(loginResponse.data[0]!!.name!!)
                       /* startActivity(Intent(requireActivity(), HomeActivity::class.java))
                        requireActivity().finish()*/
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

    private fun fbLogin() {
        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken == null || accessToken.isExpired)
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("public_profile", "email"))
    }

    private fun initializeFacebookCallback() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    if (loginResult.accessToken != null) {
                        setFacebookData(loginResult)
                    }
                }

                override fun onCancel() {
                    showToast(
                        requireActivity(),
                        getString(R.string.err_facebook_authentication_fail)
                    )
                }

                override fun onError(error: FacebookException) {
                    showToast(
                        requireActivity(),
                        this@SignInFragment.getString(R.string.err_facebook_authentication_fail)
                    )
                }

            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)

    }

    private fun setFacebookData(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { _, response ->
            try {
                email = response.jsonObject.getString(resources.getString(R.string.str_social_email))
                val loginRequest = LoginRequest()
                loginRequest.email =
                    response.jsonObject.getString(resources.getString(R.string.str_social_email))
                loginRequest.password = ""
                loginRequest.isSocial = resources.getString(R.string.str_true)
                 loginRequest.deviceToken=preferences.getString(AppConstants.FIREBASE_ID)
                val first_name = response.jsonObject.getString(
                    resources.getString(R.string.str_social_first_name)
                )
                loginRequest.name =
                    first_name + " " + response.jsonObject.getString(resources.getString(R.string.str_social_last_name))
                loginRequest.socialType = resources.getString(R.string.str_social_type)
                loginRequest.socialId = resources.getString(R.string.str_social_id)
                viewModel.authorizedUser(loginRequest)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,email,first_name,last_name")
        request.parameters = parameters
        request.executeAsync()
    }

    /*private fun saveDrafts() {
       SharedPrefsHelper.save(DRAFT_LOGIN, binding.etxEmail.text.toString())
       SharedPrefsHelper.save(DRAFT_USERNAME, usernameEt.text.toString())
   }*/

    private fun clearDrafts() {
        SharedPrefsHelper.save(DRAFT_LOGIN, "")
        SharedPrefsHelper.save(DRAFT_USERNAME, "")
    }

    private fun prepareUser(userName: String) {
        val qbUser = QBUser()
        qbUser.login = email/*binding.etxEmail.text.toString().trim { it <= ' ' }*/
        qbUser.fullName = userName
        qbUser.password = USER_DEFAULT_PASSWORD
        signIn(qbUser)
    }

    private fun signIn(user: QBUser) {
       showProgress()
        ChatHelper.login(user, object : QBEntityCallback<QBUser> {
            override fun onSuccess(userFromRest: QBUser, bundle: Bundle?) {
                if (userFromRest.fullName != null && userFromRest.fullName == user.fullName) {
                    loginToChat(user)
                } else {
                    //Need to set password NULL, because server will update user only with NULL password
                    user.password = null
                    updateUser(user)
                }
            }

            override fun onError(e: QBResponseException) {
                if (e.httpStatusCode == UNAUTHORIZED) {
                    signUp(user)
                } else {
                    hideProgress()
                    showToast(requireActivity(), "Chat login error")
                }
            }
        })
    }

    private fun updateUser(user: QBUser) {
        ChatHelper.updateUser(user, object : QBEntityCallback<QBUser> {
            override fun onSuccess(qbUser: QBUser, bundle: Bundle?) {
                loginToChat(user)
            }

            override fun onError(e: QBResponseException) {
                hideProgress()
                showToast(requireActivity(), "Chat login error")
            }
        })
    }

    private fun loginToChat(user: QBUser) {
        //Need to set password, because the server will not register to chat without password
        user.password = USER_DEFAULT_PASSWORD
        ChatHelper.loginToChat(user, object : QBEntityCallback<Void> {
            override fun onSuccess(void: Void?, bundle: Bundle?) {
                SharedPrefsHelper.saveQbUser(user)
                //if (!chbSave.isChecked) {
                clearDrafts()
                //}
                QbUsersHolder.putUser(user)
                hideProgress()
                startActivity(Intent(requireActivity(), FirstTimeHereActivity::class.java))
                requireActivity().finish()
                /*startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finish()*/
            }

            override fun onError(e: QBResponseException) {
                hideProgress()
                showToast(requireActivity(), "Chat login error")
            }
        })
    }

    private fun signUp(user: QBUser) {
        SharedPrefsHelper.removeQbUser()
        QBUsers.signUp(user).performAsync(object : QBEntityCallback<QBUser> {
            override fun onSuccess(p0: QBUser?, p1: Bundle?) {
                hideProgress()
                signIn(user)
            }

            override fun onError(exception: QBResponseException?) {
                hideProgress()
                showToast(requireActivity(), "Chat login error")
            }
        })
    }
}