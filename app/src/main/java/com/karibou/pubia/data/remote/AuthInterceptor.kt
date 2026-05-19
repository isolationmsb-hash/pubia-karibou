package com.karibou.pubia.data.remote

import com.karibou.pubia.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ajoute automatiquement le header X-API-Key sur toutes les requetes
 * (sauf /health). La cle vient de BuildConfig (definie via gradle.properties
 * ou local.properties).
 */
@Singleton
class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (original.url.encodedPath.endsWith("/health")) {
            return chain.proceed(original)
        }
        val withKey = original.newBuilder()
            .header("X-API-Key", BuildConfig.API_SHARED_SECRET)
            .build()
        return chain.proceed(withKey)
    }
}
