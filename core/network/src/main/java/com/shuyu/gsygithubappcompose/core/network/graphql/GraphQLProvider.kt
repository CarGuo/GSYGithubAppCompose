package com.shuyu.gsygithubappcompose.core.network.graphql

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.http.LoggingInterceptor
import com.apollographql.apollo3.network.okHttpClient
import com.shuyu.gsygithubappcompose.core.network.config.NetworkConfig
import com.shuyu.gsygithubappcompose.core.network.interceptor.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GraphQLProvider {

    @Provides
    @Singleton
    fun provideApolloClient(tokenInterceptor: TokenInterceptor): ApolloClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .addInterceptor(tokenInterceptor).build()

        return ApolloClient.Builder().serverUrl(NetworkConfig.BASE_GRAPHQL_URL)
            .okHttpClient(okHttpClient).build()
    }
}
