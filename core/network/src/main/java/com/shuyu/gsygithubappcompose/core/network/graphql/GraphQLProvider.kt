package com.shuyu.gsygithubappcompose.core.network.graphql

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import com.shuyu.gsygithubappcompose.core.network.interceptor.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GraphQLProvider {

    @Provides
    @Singleton
    fun provideApolloClient(tokenInterceptor: TokenInterceptor): ApolloClient {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .build()

        return ApolloClient.Builder()
            .serverUrl("https://api.github.com/graphql")
            .okHttpClient(okHttpClient)
            .build()
    }
}
