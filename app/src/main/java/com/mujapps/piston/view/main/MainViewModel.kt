package com.mujapps.piston.view.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mujapps.piston.utils.COLLECTION_USER
import com.mujapps.piston.utils.Event
import com.mujapps.piston.utils.LoggerUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mAuth: FirebaseAuth,
    val mFireStore: FirebaseFirestore,
    val mFireStorage: FirebaseStorage
) : ViewModel() {

    private var _inProgressState = MutableStateFlow(false)
    val mInProgressState: MutableStateFlow<Boolean> = _inProgressState

    private var _popUpNotificationState = MutableStateFlow(Event(""))
    val mPopUpNotificationState: MutableStateFlow<Event<String>> = _popUpNotificationState

    init {
        //_inProgressState.value = true
        //_popUpNotificationState.value = Event("Colas")
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
                        //Create User profile in DB
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

    private fun handleException(exception: Exception? = null, customMessage: String = "") {
        LoggerUtils.logMessage("Piston Exception :$exception")
        exception?.printStackTrace()
        val errorMessage = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMessage else "$customMessage : $errorMessage"
        _popUpNotificationState.value = Event(message)
        _inProgressState.value = false
    }
}