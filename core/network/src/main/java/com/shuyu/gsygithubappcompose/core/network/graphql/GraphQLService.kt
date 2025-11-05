package com.shuyu.gsygithubappcompose.core.network.graphql

import com.apollographql.apollo3.ApolloClient
import javax.inject.Inject

class GraphQLService @Inject constructor(private val apolloClient: ApolloClient) {

    suspend fun getRepository(owner: String, name: String) = apolloClient.query(
        GetRepositoryDetailQuery(owner, name)
    ).execute()

    suspend fun getTrendUser(location: String) =
        apolloClient.query(GetTrendUserQuery(location)).execute()
}
