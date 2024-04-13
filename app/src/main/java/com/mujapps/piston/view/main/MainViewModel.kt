package com.mujapps.piston.view.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.mujapps.piston.data.UserData
import com.mujapps.piston.utils.COLLECTION_USER
import com.mujapps.piston.utils.Event
import com.mujapps.piston.utils.LoggerUtils
import com.mujapps.piston.view.screens.Gender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
}