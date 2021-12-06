package com.auditionstreet.artist.ui.projects.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.auditionstreet.artist.BuildConfig
import com.auditionstreet.artist.R
import com.auditionstreet.artist.api.ApiConstant
import com.auditionstreet.artist.databinding.FragmentProfileBinding
import com.auditionstreet.artist.model.response.DeleteMediaResponse
import com.auditionstreet.artist.model.response.GetBodyTypeLanguageResponse
import com.auditionstreet.artist.model.response.ProfileResponse
import com.auditionstreet.artist.model.response.UploadMediaResponse
import com.auditionstreet.artist.storage.preference.Preferences
import com.auditionstreet.artist.ui.login_signup.AuthorizedUserActivity
import com.auditionstreet.artist.ui.profile.viewmodel.ProfileViewModel
import com.auditionstreet.artist.ui.projects.adapter.WorkListAdapter
import com.auditionstreet.artist.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.esafirm.imagepicker.features.ImagePicker
import com.google.gson.Gson
import com.leo.wikireviews.utils.livedata.EventObserver
import com.silo.model.request.LogoutRequest
import com.silo.model.request.SupportRequest
import com.silo.model.request.WorkGalleryRequest
import com.silo.utils.AppBaseFragment
import com.silo.utils.network.Resource
import com.silo.utils.network.Status
import com.silo.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.sql.DataSource
import kotlin.collections.ArrayList
import kotlin.collections.set


@AndroidEntryPoint
class ProfileFragment : AppBaseFragment(R.layout.fragment_profile), View.OnClickListener {
    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var profileAdapter: WorkListAdapter
    private val picker = 2000
    private val picker_gallery = 4000
    private var totalGalleryImages = 0
    private var totalGalleryVideos = 0
    var listGallery = ArrayList<WorkGalleryRequest>()
    val uploadMedia = ArrayList<WorkGalleryRequest>()

    private var images: MutableList<com.esafirm.imagepicker.model.Image> = mutableListOf()
    private var profileImageFile: File? = null
    private var compressImage = CompressFile()

    @Inject
    lateinit var preferences: Preferences
    private lateinit var profileResponse: ProfileResponse

    private lateinit var uploadMediaResponse: UploadMediaResponse
    private lateinit var mediaDelete: DeleteMediaResponse
    private var selectedImage = ""
    private var isIntroVideo: Boolean = false
    private var isImageDelete: Boolean = false
    private var deleteMediaPos: Int = 0

    private var introVideoPath: String = ""
    private var languageList: ArrayList<GetBodyTypeLanguageResponse.Data.Language> ?= null
    private var bodyTypeList: ArrayList<GetBodyTypeLanguageResponse.Data.BodyType> ?= null
    private var skinToneList: ArrayList<GetBodyTypeLanguageResponse.Data.SkinTone> ?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        languageList = ArrayList()
        bodyTypeList = ArrayList()
        skinToneList = ArrayList()
        setListeners()
        init()
        setObservers()
        getUserProfile()
    }

    private fun getUserProfile() {
        viewModel.getLanguageBodyType(BuildConfig.BASE_URL + ApiConstant.GET_LANGUAGE_BODY_TYPE)
    }

    private fun setListeners() {
        binding.tvAddMedia.setOnClickListener(this)
        binding.imgEdit.setOnClickListener(this)
        binding.tvDone.setOnClickListener(this)
        binding.imgProfile.setOnClickListener(this)
        binding.imgIntroVideo.setOnClickListener(this)
        binding.imgPlay.setOnClickListener(this)
        binding.imgDelete.setOnClickListener(this)
        binding.etxLanguage.setOnClickListener(this)
        binding.etxBodyType.setOnClickListener(this)
        binding.etxSkinTone.setOnClickListener(this)
        binding.btnLogout.setOnClickListener(this)
        binding.btnSupport.setOnClickListener(this)
    }


    private fun setObservers() {
        viewModel.getProfile.observe(viewLifecycleOwner, EventObserver {
            handleApiCallback(it)
        })
        viewModel.uploadMedia.observe(viewLifecycleOwner, EventObserver {
            handleApiCallback(it)
        })
        viewModel.deleteMedia.observe(viewLifecycleOwner, EventObserver {
            handleApiCallback(it)
        })
        viewModel.bodyTypeLanguage.observe(viewLifecycleOwner, EventObserver {
            handleApiCallback(it)
        })
        viewModel.logout.observe(viewLifecycleOwner, EventObserver {
            handleApiCallback(it)
        })
        viewModel.support.observe(viewLifecycleOwner, EventObserver {
            handleApiCallback(it)
        })

    }

    private fun handleApiCallback(apiResponse: Resource<Any>) {
        when (apiResponse.status) {
            Status.SUCCESS -> {
                hideProgress()
                when (apiResponse.apiConstant) {
                    ApiConstant.GET_PROFILE -> {
                        profileResponse = apiResponse.data as ProfileResponse
                        setWorkAdapter(profileResponse)
                    }
                    ApiConstant.UPLOAD_MEDIA -> {
                        uploadMediaResponse = apiResponse.data as UploadMediaResponse
                        showToast(requireActivity(), uploadMediaResponse.msg.toString())
                        showImgEdit()
                        enableOrDisable(false)
                        getUserProfile()
                    }
                    ApiConstant.DELETE_MEDIA -> {
                        mediaDelete = apiResponse.data as DeleteMediaResponse
                        showToast(requireActivity(), mediaDelete.msg.toString())
                        if (isImageDelete)
                            totalGalleryImages--
                        else
                            totalGalleryVideos--
                        listGallery.removeAt(deleteMediaPos)
                        profileAdapter.notifyDataSetChanged()
                    }
                    ApiConstant.GET_LANGUAGE_BODY_TYPE -> {
                       val getBodyTypeLanguageResponse = apiResponse.data as GetBodyTypeLanguageResponse
                        languageList = getBodyTypeLanguageResponse.data.languages
                        bodyTypeList = getBodyTypeLanguageResponse.data.bodyTypes
                        skinToneList = getBodyTypeLanguageResponse.data.skinTones

                        viewModel.getProfile(
                            BuildConfig.BASE_URL + ApiConstant.GET_PROFILE + "/" + preferences.getString(
                                AppConstants.USER_ID
                            )
                        )
                    }
                    ApiConstant.LOGOUT ->{
                        preferences.clearPreferences()
                        val intent = Intent(requireContext(), AuthorizedUserActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        requireActivity().finish()
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

    private fun setWorkAdapter(profileResponse: ProfileResponse) {

        if (profileResponse.data!![0]!!.artistDetails!!.image!!.isNotEmpty()) {
            Glide.with(this).load(profileResponse.data[0]!!.artistDetails!!.image)
                .into(binding.imgProfile)
        }
        preferences.setString(
            AppConstants.USER_IMAGE,
            profileResponse.data[0]!!.artistDetails!!.image
        )
        binding.etxName.setText(profileResponse.data[0]!!.artistDetails!!.name)
        binding.etxSubName.setText(profileResponse.data[0]!!.artistDetails!!.gender)
        binding.etxYear.setText(profileResponse.data[0]!!.artistDetails!!.year)
        binding.etxAge.setText(profileResponse.data[0]!!.artistDetails!!.age)
        binding.etxHeightFt.setText(profileResponse.data[0]!!.artistDetails!!.heightFt)
        binding.etxHeightIn.setText(profileResponse.data[0]!!.artistDetails!!.heightIn)
        var languages = ""
        for (i in 0 until profileResponse.data[0]!!.artistDetails!!.language.size){
            for (y in 0 until languageList!!.size){
                if (languageList!![y].id == profileResponse.data[0]!!.artistDetails!!.language[i].id){
                    languageList!![y].isChecked = true
                    languages += languageList!![y].name + " ,"
                }
            }
        }
        if (languages.length >= 1)
            binding.etxLanguage.text = languages.substring(0, languages.length - 1)
        else
            binding.etxLanguage.text = ""

        var bodyTypes = ""
        for (i in 0 until profileResponse.data[0]!!.artistDetails!!.bodyType.size){
            for (y in 0 until bodyTypeList!!.size){
                if (bodyTypeList!![y].id == profileResponse.data[0]!!.artistDetails!!.bodyType[i].id){
                    bodyTypeList!![y].isChecked = true
                    bodyTypes += bodyTypeList!![y].name + " ,"
                }
            }
        }
        if (bodyTypes.length >= 1)
            binding.etxBodyType.text = bodyTypes.substring(0, bodyTypes.length - 1)
        else
            binding.etxBodyType.text = ""

        var skinTones = ""
        for (i in 0 until profileResponse.data[0]!!.artistDetails!!.skinTone.size){
            for (y in 0 until skinToneList!!.size){
                if (skinToneList!![y].id == profileResponse.data[0]!!.artistDetails!!.skinTone[i].id){
                    skinToneList!![y].isChecked = true
                    skinTones += skinToneList!![y].name + " ,"
                }
            }
        }
        if (skinTones.length >= 1)
            binding.etxSkinTone.text = skinTones.substring(0, skinTones.length - 1)
        else
            binding.etxSkinTone.text = ""
        /*binding.etxBodyType.setText(profileResponse.data[0]!!.artistDetails!!.bodyType)
        binding.etxSkinTone.setText(profileResponse.data[0]!!.artistDetails!!.skinTone)
        binding.etxLanguage.setText(profileResponse.data[0]!!.artistDetails!!.language)*/

        if (profileResponse.data[0]!!.artistDetails!!.video!!.isNotEmpty()) {
            /*Glide.with(this).load(profileResponse.data[0]!!.artistDetails!!.video)
                .into(binding.imgIntroVideo)*/
            Glide.with(this)
                .load(profileResponse.data[0]!!.artistDetails!!.video)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        @Nullable e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        showToast(requireActivity(), "Video loading failed")
                        binding.progress.setVisibility(View.GONE)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable?>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.imgPlay.visibility = View.VISIBLE
                        introVideoPath = profileResponse.data[0]!!.artistDetails!!.video.toString()
                        binding.progress.setVisibility(View.GONE)
                        return false
                    }
                })
                .into(binding.imgIntroVideo)
        }
        listGallery.clear()
        for (i in 0 until profileResponse.data[0]!!.media!!.size) {
            val request = WorkGalleryRequest()
            request.path = profileResponse.data[0]!!.media!![i]!!.mediaUrl!!
            request.isShowDeleteImage = false
            request.isLocal = false
            request.isImage =
                profileResponse.data[0]!!.media!![i]!!.mediaType.equals(resources.getString(R.string.str_img))
            listGallery.add(request)
        }
        if (listGallery.size > 0) {
            showGalleryView(true)
        } else {
            showGalleryView(false)
        }
        profileAdapter.notifyDataSetChanged()
        binding.etxBio.setText(profileResponse.data[0]!!.artistDetails!!.bio)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvAddMedia -> {
                mPermissionResult.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
            R.id.imgIntroVideo -> {

            }
            R.id.imgEdit -> {
                showDoneButton()
                enableOrDisable(true)
            }
            R.id.tvDone -> {
                if (introVideoPath.contains("http"))
                    introVideoPath = ""
                uploadMedia.clear()
                for (i in 0 until listGallery.size) {
                    if (listGallery[i].isLocal)
                        uploadMedia.add(listGallery[i])
                }
                viewModel.uploadMedia(
                    uploadMedia,
                    profileImageFile,
                    selectedImage,
                    introVideoPath,
                    requestProfileUpdate()
                )
            }
            R.id.imgProfile -> {
                pickImage(picker, true, 1)
            }
            R.id.imgPlay -> {
                showImageOrVideoDialog(requireActivity(), introVideoPath, false)
            }
            R.id.imgDelete -> {
                mPermissionIntroVideo.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                    )
                )
            }
            R.id.etxLanguage ->{
                showLanguageSelectionDialog(requireActivity(), languageList!!)
                {
                    val languageStringList = arrayListOf<String>()
                    var user = ""
                    for (i in 0 until languageList!!.size) {
                        if (languageList!![i].isChecked) {
                            languageStringList.add(languageList!![i].name)
                            user += languageList!![i].name + " ,"
                        }
                    }
                    if (user.length >= 1)
                        binding.etxLanguage.text = user.substring(0, user.length - 1)
                    else
                        binding.etxLanguage.text = ""
                }
            }
            R.id.etxBodyType ->{
                showBodyTypeSelectionDialog(requireActivity(), bodyTypeList!!)
                {
                    val bodyTypeStringList = arrayListOf<String>()
                    var user = ""
                    for (i in 0 until bodyTypeList!!.size) {
                        if (bodyTypeList!![i].isChecked) {
                            bodyTypeStringList.add(bodyTypeList!![i].name)
                            user += bodyTypeList!![i].name + " ,"
                        }
                    }
                    if (user.length >= 1)
                        binding.etxBodyType.text = user.substring(0, user.length - 1)
                    else
                        binding.etxBodyType.text = ""
                }
            }
            R.id.etxSkinTone ->{
                showSkinToneSelectionDialog(requireActivity(), skinToneList!!)
                {
                    val bodyTypeStringList = arrayListOf<String>()
                    var user = ""
                    for (i in 0 until skinToneList!!.size) {
                        if (skinToneList!![i].isChecked) {
                            bodyTypeStringList.add(skinToneList!![i].name)
                            user += skinToneList!![i].name + " ,"
                        }
                    }
                    if (user.length >= 1)
                        binding.etxSkinTone.text = user.substring(0, user.length - 1)
                    else
                        binding.etxSkinTone.text = ""
                }
            }
            R.id.btnSupport ->{
                showSupportDialog(requireActivity()){
                    val supportRequest = SupportRequest()
                    supportRequest.message = it
                    if(!preferences.getString(AppConstants.PHONE_NUMBER).isNullOrEmpty())
                    supportRequest.phoneNumber = preferences.getString(AppConstants.PHONE_NUMBER)
                    else supportRequest.phoneNumber = "Social"
                    supportRequest.userType = "Artist"
                    viewModel.supportApi(supportRequest)
                }
            }
            R.id.btnLogout ->{
                showLogoutDialog(requireActivity())
                {
                   val logoutRequest = LogoutRequest()
                    logoutRequest.userId = preferences.getString(AppConstants.USER_ID)
                    logoutRequest.userType = "artist"
                    viewModel.logout(logoutRequest)
                }
            }
        }
    }

    private fun showIntroVideoDialog() {

        showIntroVideoDialog(requireActivity())
        {
            if (it == 0) {
                isIntroVideo = true
                openVideoGallery()
            } else if (it == 1) {
                openCameraVideo()
            }
        }
    }

    private fun showMedia() {
        showMediaDialog(requireActivity())
        {
            if (it == 0)
                if (totalGalleryImages < 4)
                    pickImage(picker_gallery, false, 4 - totalGalleryImages)
                else
                    showVideoOrImageValidation(
                        requireActivity(),
                        resources.getString(R.string.str_image_error)
                    )
            else if (it == 1) {
                if (totalGalleryVideos < 1) {
                    isIntroVideo = false
                    openVideoGallery()
                } else
                    showVideoOrImageValidation(
                        requireActivity(),
                        resources.getString(R.string.str_video_error)
                    )
            }
        }
    }

    private fun openVideoGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_PICK
        Intent.createChooser(intent, "Select Video")
        startForResult.launch(intent)
    }

    private fun openCameraVideo() {
        val intent = Intent()
        intent.action = MediaStore.ACTION_VIDEO_CAPTURE
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        startForResult.launch(intent)
        isIntroVideo = true
    }

    private fun enableOrDisable(b: Boolean) {
        binding.etxName.isEnabled = b
        binding.etxYear.isEnabled = b
        binding.etxBio.isEnabled = b
        binding.imgProfile.isClickable = b
        binding.etxAge.isEnabled = b
        binding.etxHeightFt.isEnabled = b
        binding.etxHeightIn.isEnabled = b
        binding.etxBodyType.isEnabled = b
        binding.etxSkinTone.isEnabled = b
        binding.etxLanguage.isEnabled = b
    }

    private fun showDoneButton() {
        binding.imgEdit.visibility = View.GONE
        binding.tvDone.visibility = View.VISIBLE
        binding.tvAddMedia.visibility = View.VISIBLE

        for (i in 0 until listGallery.size) {
            listGallery.get(i).isShowDeleteImage = true
        }
        profileAdapter.notifyDataSetChanged()
        binding.imgDelete.visibility = View.VISIBLE
    }

    private fun showImgEdit() {
        binding.imgEdit.visibility = View.VISIBLE
        binding.tvDone.visibility = View.GONE
        binding.tvAddMedia.visibility = View.GONE
        binding.imgDelete.visibility = View.GONE
    }

    private fun init() {
        binding.imgProfile.isClickable = false
        binding.rvWork.apply {
            layoutManager = LinearLayoutManager(activity)
            profileAdapter = WorkListAdapter(requireActivity())
            { position: Int ->
                isImageDelete = listGallery[position].isImage
                deleteMediaPos = position
                if (listGallery[position].isLocal) {
                    listGallery.removeAt(position)
                } else {
                    viewModel.deleteMedia(
                        BuildConfig.BASE_URL + ApiConstant.DELETE_MEDIA + "/" + preferences.getString(
                            AppConstants.USER_ID
                        )
                    )
                }
                profileAdapter.notifyDataSetChanged()
                if (listGallery.size > 0) {
                    showGalleryView(true)
                } else {
                    showGalleryView(false)
                }
            }

            adapter = profileAdapter
            val gridLayoutManager = GridLayoutManager(requireActivity(), 3)
            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL) // set Horizontal Orientation
            binding.rvWork.setLayoutManager(gridLayoutManager)
            profileAdapter.submitList(listGallery)
        }
    }

    private fun pickImage(picker: Int, showCamera: Boolean, maxCount: Int) {
        ImagePicker.create(this)
            .showCamera(showCamera)
            .folderMode(false)
            .limit(maxCount)
            .toolbarFolderTitle(getString(R.string.folder))
            .toolbarImageTitle(getString(R.string.gallery_select_title_msg))
            .start(picker)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == picker && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            images = ImagePicker.getImages(data)
            profileImageFile = File(images[0].path)
            selectedImage = images[0].name
            /*profileImageFile =
                compressImage.getCompressedImageFile(profileImageFile!!, activity as Context)
*/            Glide.with(this).load(profileImageFile)
                .into(binding.imgProfile)
        } else if (requestCode == picker_gallery && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            images = ImagePicker.getImages(data)
            for (i in 0 until images.size) {
                totalGalleryImages++
                val request = WorkGalleryRequest()
                request.path = images[i].path
                request.isImage = true
                request.isShowDeleteImage = true
                request.isLocal = true
                listGallery.add(0, request)
                profileAdapter.notifyDataSetChanged()
                showGalleryView(true)
            }
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val request = WorkGalleryRequest()
                processVideo(result.data!!.data!!, requireActivity())
                { path: String ->
                    if (isIntroVideo) {
                       // binding.progress.setVisibility(View.GONE)
                        /*Glide.with(this).load(path)
                            .into(binding.imgIntroVideo)*/
                        Glide.with(this)
                            .load(path)
                            .listener(object : RequestListener<Drawable?> {
                                override fun onLoadFailed(
                                    @Nullable e: GlideException?,
                                    model: Any?,
                                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    showToast(requireActivity(), "Video loading failed")
                                    binding.progress.setVisibility(View.GONE)
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                    dataSource: com.bumptech.glide.load.DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    binding.imgPlay.visibility = View.VISIBLE
                                    introVideoPath = path
                                    binding.progress.setVisibility(View.GONE)
                                    return false
                                }
                            })
                            .into(binding.imgIntroVideo)
                        /*introVideoPath = path
                        binding.imgPlay.visibility = View.VISIBLE*/
                    } else {
                        request.path = path
                        request.isImage = false
                        request.isShowDeleteImage = true
                        request.isLocal = true
                        listGallery.add(0, request)
                        profileAdapter.notifyDataSetChanged()
                        totalGalleryVideos++
                        showGalleryView(true)
                    }
                }
            }
        }

    private fun showGalleryView(b: Boolean) {
        if (b) {
            binding.rvWork.visibility = View.VISIBLE
            binding.tvNoMedia.visibility = View.INVISIBLE
        } else {
            binding.rvWork.visibility = View.INVISIBLE
            binding.tvNoMedia.visibility = View.VISIBLE
        }
    }

    val mPermissionIntroVideo =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var permission: Int = 0
            permissions.entries.forEach {
                Log.e("DEBUG", "${it.key} = ${it.value}")
                if (it.value)
                    permission++
            }
            if (permission == 3)
                showIntroVideoDialog()
        }

    val mPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var permission: Int = 0
            permissions.entries.forEach {
                Log.e("DEBUG", "${it.key} = ${it.value}")
                if (it.value)
                    permission++
            }
            if (permission == 2)
                showMedia()
        }

    private fun requestProfileUpdate(
    ): HashMap<String, RequestBody> {
        val gson = Gson()
        val bodyTypeIdList = ArrayList<Int>()
        val languageIdList = ArrayList<Int>()
        val skinToneIdList = ArrayList<Int>()
        for (i in 0 until skinToneList!!.size){
            if (skinToneList!![i].isChecked){
                skinToneIdList.add(skinToneList!![i].id)
            }
        }
        for (i in 0 until bodyTypeList!!.size){
            if (bodyTypeList!![i].isChecked){
                bodyTypeIdList.add(bodyTypeList!![i].id)
            }
        }
        for (i in 0 until languageList!!.size){
            if (languageList!![i].isChecked){
                languageIdList.add(languageList!![i].id)
            }
        }
        val languageListString =  gson.convertToJsonString(languageIdList)
        val bodyTypeListString =  gson.convertToJsonString(bodyTypeIdList)
        val skinToneListString =  gson.convertToJsonString(skinToneIdList)
        val map = HashMap<String, RequestBody>()
        map[resources.getString(R.string.str_name_label)] =
            toRequestBody(etxName.text.toString())
        map[resources.getString(R.string.str_year_label)] =
            toRequestBody(etxYear.text.toString())
        map[resources.getString(R.string.str_age_label)] =
            toRequestBody(etxAge.text.toString())
        map[resources.getString(R.string.str_heightFt_label)] =
            toRequestBody(etxHeightFt.text.toString())
        map[resources.getString(R.string.str_heightIn_label)] =
            toRequestBody(etxHeightIn.text.toString())
        map[resources.getString(R.string.str_bodytype_label)] =
            toRequestBody(/*etxBodyType.text.toString()*/bodyTypeListString)
        map[resources.getString(R.string.str_skintone_label)] =
            toRequestBody(/*etxSkinTone.text.toString()*/skinToneListString)
        map[resources.getString(R.string.str_language_label)] =
            toRequestBody(/*etxLanguage.text.toString()*/languageListString)
        map[resources.getString(R.string.str_bio)] =
            toRequestBody(etxBio.text.toString())
        map[resources.getString(R.string.str_artistId_label)] =
            toRequestBody(preferences.getString(AppConstants.USER_ID))
        return map
    }

    private fun toRequestBody(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

}