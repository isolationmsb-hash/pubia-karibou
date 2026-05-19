package com.karibou.pubia.di

import com.karibou.pubia.BuildConfig
import com.karibou.pubia.data.remote.AuthInterceptor
import com.karibou.pubia.data.remote.BackendApi
import com.karibou.pubia.util.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.HEADERS // BODY trop verbeux pour uploads multipart
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        auth: AuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(auth)
        .addInterceptor(logging)
        .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideBackendApi(retrofit: Retrofit): BackendApi =
        retrofit.create(BackendApi::class.java)
}
