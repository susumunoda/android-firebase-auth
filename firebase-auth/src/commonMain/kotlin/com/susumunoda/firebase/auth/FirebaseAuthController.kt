package com.susumunoda.firebase.auth

import com.susumunoda.auth.AuthController
import com.susumunoda.auth.Session
import com.susumunoda.auth.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FirebaseAuthController(coroutineScope: CoroutineScope) : AuthController {
    private val auth = Firebase.auth
    private var _sessionFlow = MutableStateFlow(Session.UNKNOWN)
    override val sessionFlow = _sessionFlow.asStateFlow()

    init {
        coroutineScope.launch {
            auth.authStateChanged.collect { firebaseUser ->
                if (firebaseUser != null) {
                    _sessionFlow.update { Session(User(firebaseUser.uid)) }
                } else {
                    _sessionFlow.update { Session.LOGGED_OUT }
                }
            }
        }
    }

    override suspend fun createUser(email: String, password: String): User {
        assert(_sessionFlow.value == Session.LOGGED_OUT) { "Cannot create user while already logged in" }
        val authResult = auth.createUserWithEmailAndPassword(email, password)
        if (authResult.user == null) {
            throw IllegalArgumentException("Could not create user")
        }
        return User(authResult.user!!.uid)
    }

    override suspend fun login(email: String, password: String): User {
        assert(_sessionFlow.value == Session.LOGGED_OUT) { "Cannot login user while already logged in" }
        val authResult = auth.signInWithEmailAndPassword(email, password)
        if (authResult.user == null) {
            throw IllegalArgumentException("Could not log in user")
        }
        return User(authResult.user!!.uid)
    }

    override suspend fun logout() {
        auth.signOut()
    }
}

// Copied from kotlin-stdlib
private inline fun assert(value: Boolean, lazyMessage: () -> Any) {
    if (!value) {
        val message = lazyMessage()
        throw AssertionError(message)
    }
}