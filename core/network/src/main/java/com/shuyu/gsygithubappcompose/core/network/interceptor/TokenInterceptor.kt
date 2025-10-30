package com.shuyu.gsygithubappcompose.core.network.interceptor

import com.shuyu.gsygithubappcompose.core.common.datastore.UserPreferencesDataStore
import okhttp3.Interceptor
import okhttp3.Response
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenInterceptor @Inject constructor(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : Interceptor {

    private val _token = MutableStateFlow<String?>(null)

    private val interceptorScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Initialize _token from DataStore on startup
        interceptorScope.launch {
            userPreferencesDataStore.authToken.collect { newToken ->
                _token.value = newToken
            }
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        var currentToken = _token.value

        // If token is not in memory, try to get it from DataStore synchronously
        if (currentToken == null) {
            currentToken = runBlocking { userPreferencesDataStore.authToken.first() }
            _token.value = currentToken // Update in-memory token
        }

        // Add Authorization header if token exists
        currentToken?.let { token ->
            request = request.newBuilder()
                .header("Authorization", token)
                .build()
        }

        return chain.proceed(request)
    }

    fun clearAuthorization() {
        interceptorScope.launch {
            userPreferencesDataStore.clearAuthToken()
            _token.value = null // Clear in-memory token
        }
    }
}
