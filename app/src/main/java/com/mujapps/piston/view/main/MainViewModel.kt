package com.mujapps.piston.view.main

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.mujapps.piston.data.ChatData
import com.mujapps.piston.data.ChatUser
import com.mujapps.piston.data.UserData
import com.mujapps.piston.utils.COLLECTION_CHAT
import com.mujapps.piston.utils.COLLECTION_USER
import com.mujapps.piston.utils.Event
import com.mujapps.piston.utils.LoggerUtils
import com.mujapps.piston.view.screens.Gender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mAuth: FirebaseAuth,
    private val mFireStore: FirebaseFirestore,
    val mFireStorage: FirebaseStorage
) : ViewModel() {

    private var _inProgressState = MutableStateFlow(false)
    val mInProgressState: MutableStateFlow<Boolean> = _inProgressState

    private var _popUpNotificationState = MutableStateFlow(Event(""))
    val mPopUpNotificationState: MutableStateFlow<Event<String>> = _popUpNotificationState

    private var _signedInState = MutableStateFlow(false)
    val mSignedInState: MutableStateFlow<Boolean> = _signedInState

    private var _userDataState = MutableStateFlow<UserData?>(UserData())
    val mUserDataState: MutableStateFlow<UserData?> = _userDataState

    private val _matchSwipeScreenState = MutableStateFlow<SwipeScreen?>(SwipeScreen())
    val mMatchSwipeScreenState: MutableStateFlow<SwipeScreen?> = _matchSwipeScreenState

    private var _inProgressProfiles = MutableStateFlow(false)
    val mInProgressProfiles: MutableStateFlow<Boolean> = _inProgressProfiles

    init {
        val currentUser = mAuth.currentUser
        _signedInState.value = currentUser != null
        currentUser?.uid?.let { uid ->
            LoggerUtils.logMessage("Check User Init")
            getUserData(uid)
        }
    }

    fun signUpUser(userName: String, email: String, password: String) {
        if (userName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            handleException(customMessage = "Please fill in all the fields..!!")
            return
        }
        _inProgressState.value = true

        mFireStore.collection(COLLECTION_USER).whereEqualTo("username", userName).get().addOnSuccessListener {
            if (it.isEmpty) {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _inProgressState.value = false
                        _signedInState.value = true
                        createOrUpdateProfile(username = userName)
                    } else {
                        handleException(task.exception, "SignUp Failed")
                    }
                }
            } else {
                handleException(customMessage = "User Name Already Exists")
            }
        }.addOnFailureListener { fail ->
            handleException(fail)
        }
    }

    fun onLogin(email: String, password: String) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please Fill all fields")
            return
        }
        _inProgressState.value = true
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _signedInState.value = true
                _inProgressState.value = true
                mAuth.currentUser?.uid?.let {
                    getUserData(it)
                }
            } else {
                handleException(task.exception, "Login Failed")
            }
        }.addOnFailureListener { fail ->
            handleException(fail, "Login Failed")
        }
    }

    private fun createOrUpdateProfile(
        name: String? = null,
        username: String? = null,
        bio: String? = null,
        imageUrl: String? = null,
        gender: Gender? = null,
        genderPreference: Gender? = null
    ) {
        val userId = mAuth.currentUser?.uid
        val userData = UserData(
            userId = userId,
            name = name ?: _userDataState.value?.name,
            userName = username ?: _userDataState.value?.userName,
            imageUrl = imageUrl ?: _userDataState.value?.imageUrl,
            bio = bio ?: _userDataState.value?.bio,
            gender = gender?.toString() ?: _userDataState.value?.gender,
            genderPreference = genderPreference?.toString() ?: _userDataState.value?.genderPreference,
        )

        userId?.let { uid ->
            _inProgressState.value = true
            mFireStore.collection(COLLECTION_USER).document(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    it.reference.update(userData.toMap()).addOnSuccessListener {
                        _userDataState.value = userData //automatically update data on State upon retrieve
                        _inProgressState.value = false

                        //Refresh cards
                        populateCards()
                    }.addOnFailureListener { ex ->
                        handleException(ex, "Cannot Update User")
                    }
                } else {
                    mFireStore.collection(COLLECTION_USER).document(uid).set(userData)
                    _inProgressState.value = false
                    LoggerUtils.logMessage("Check User Create")
                    getUserData(uid)
                }
            }.addOnFailureListener { ex ->
                handleException(ex, "Cannot Create User")
            }
        }
    }

    private fun getUserData(uid: String) {
        _inProgressState.value = true
        mFireStore.collection(COLLECTION_USER).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error, "Cannot Retrieve User Data")
            }
            if (value != null) {
                val user = value.toObject<UserData>()
                if (user != null) {
                    _userDataState.value = user
                    _inProgressState.value = false
                    //Refresh cards
                    populateCards()
                }
            }
        }
    }

    private fun handleException(exception: Exception? = null, customMessage: String = "") {
        LoggerUtils.logMessage("Piston Exception :$exception")
        exception?.printStackTrace()
        val errorMessage = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMessage else "$customMessage : $errorMessage"
        _popUpNotificationState.value = Event(message)
        _inProgressState.value = false
    }

    fun onLogOut() {
        mAuth.signOut()
        _signedInState.value = false
        _userDataState.value = null
        _popUpNotificationState.value = Event("Logged Out")
    }

    fun updateProfileData(name: String, userName: String, bio: String, gender: Gender, genderPreference: Gender) {
        createOrUpdateProfile(name = name, username = userName, bio = bio, gender = gender, genderPreference = genderPreference)
    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {
        _inProgressState.value = true

        val storageRef = mFireStorage.reference
        val uuid = UUID.randomUUID()

        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener(onSuccess)
        }.addOnFailureListener { error ->
            handleException(error)
        }
    }

    fun uploadProfilePic(uri: Uri) {
        uploadImage(uri) {
            createOrUpdateProfile(imageUrl = it.toString())
        }
    }

    private fun populateCards() {
        _inProgressProfiles.value = true

        val gender = if (mUserDataState.value?.gender.isNullOrEmpty()) "ANY"
        else mUserDataState.value?.gender!!.uppercase()

        val genderPref = if (mUserDataState.value?.genderPreference.isNullOrEmpty()) "ANY"
        else mUserDataState.value?.genderPreference!!.uppercase()

        val cardsQuery = when (Gender.valueOf(genderPref)) {
            Gender.CAT -> mFireStore.collection(COLLECTION_USER).whereEqualTo("gender", Gender.CAT)
            Gender.DOG -> mFireStore.collection(COLLECTION_USER).whereEqualTo("gender", Gender.DOG)
            Gender.ANY -> mFireStore.collection(COLLECTION_USER)
        }

        val userGender = Gender.valueOf(gender) //Current users Gender

        //Improve query
        cardsQuery.where(
            Filter.and(
                Filter.notEqualTo("userId", mUserDataState.value?.userId),
                /*Filter.or(
                    Filter.equalTo("genderPreference", userGender),
                    Filter.equalTo("genderPreference", Gender.ANY)
                )*/
            )
        ).addSnapshotListener { value, error ->
            if (error != null) {
                LoggerUtils.logMessage("Query Error SnapShot: $error")
                handleException(error)
            }
            LoggerUtils.logMessage("Query Value SnapShot: ${value?.documents?.size ?: 0}")

            if (value != null) {
                val potentials = mutableListOf<UserData>()
                value.documents.forEach {
                    it.toObject<UserData>()?.let { potential ->
                        var showUser = true
                        if (mUserDataState.value?.swipeLeft?.contains(potential.userId) == true
                            || mUserDataState.value?.swipeRight?.contains(potential.userId) == true
                            || mUserDataState.value?.matches?.contains(potential.userId) == true
                        ) {
                            showUser = false
                        }

                        if (showUser) potentials.add(potential)

                        LoggerUtils.logMessage("Potential Size: ${potentials.size}")
                    }
                }

                _matchSwipeScreenState.update {
                    it?.copy(mData = ArrayList(potentials), resetUI = true, totalCount = potentials.size, nowCount = 1)
                }
                _inProgressProfiles.value = false
            }
        }
    }

    fun onDisLike(selectedUser: UserData) {
        mFireStore.collection(COLLECTION_USER).document(mUserDataState.value?.userId ?: "")
            .update("swipesLeft", FieldValue.arrayUnion(selectedUser.userId))
    }

    fun onLike(selectedUser: UserData) {
        //First Check is a Match
        val reciprocalMatch = selectedUser.swipeRight.contains(mUserDataState.value?.userId)
        if (!reciprocalMatch) {
            mFireStore.collection(COLLECTION_USER).document(mUserDataState.value?.userId ?: "")
                .update("swipesRight", FieldValue.arrayUnion(selectedUser.userId))
        } else {
            _popUpNotificationState.value = Event("Match!")

            mFireStore.collection(COLLECTION_USER).document(selectedUser.userId ?: "")
                .update("swipesRight", FieldValue.arrayRemove(mUserDataState.value?.userId ?: ""))

            mFireStore.collection(COLLECTION_USER).document(selectedUser.userId ?: "")
                .update("matches", FieldValue.arrayUnion(mUserDataState.value?.userId ?: ""))

            mFireStore.collection(COLLECTION_USER).document(mUserDataState.value?.userId ?: "")
                .update("matches", FieldValue.arrayUnion(selectedUser.userId ?: ""))

            //Creating a Chat channel

            val chatKey = mFireStore.collection(COLLECTION_CHAT).document().id
            val chatData = ChatData(
                chatKey,
                ChatUser(
                    mUserDataState.value?.userId,
                    if (mUserDataState.value?.name.isNullOrEmpty()) mUserDataState.value?.userName else mUserDataState.value?.name,
                    mUserDataState.value?.imageUrl
                ),
                ChatUser(
                    selectedUser.userId,
                    if (selectedUser.name.isNullOrEmpty()) selectedUser.userName else selectedUser.name,
                    selectedUser.imageUrl
                )
            )
            mFireStore.collection(COLLECTION_CHAT).document(chatKey).set(chatData)
        }
    }

    fun onSwiped(profId: String?) {
        val profiles = _matchSwipeScreenState.value?.mData ?: arrayListOf()
        if (profiles.isEmpty().not() && profId.isNullOrEmpty().not()) {
            val temp = profiles.filter {
                it.userId != profId
            }

            _matchSwipeScreenState.update { swipeScreen ->
                swipeScreen?.copy(
                    mData = ArrayList(temp),
                    resetUI = true,
                    nowCount = if (temp.isNotEmpty()) (swipeScreen.nowCount + 1) else swipeScreen.nowCount
                )
            }
        }
    }

    data class SwipeScreen(
        var mData: ArrayList<UserData> = arrayListOf(),
        var resetUI: Boolean = true,
        val totalCount: Int = 0,
        var nowCount: Int = 0,
    )
}