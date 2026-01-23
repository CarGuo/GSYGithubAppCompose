package com.shuyu.gsygithubappcompose.core.network.graphql

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import com.shuyu.gsygithubappcompose.core.common.datastore.IUserPreferencesDataStore
import com.shuyu.gsygithubappcompose.core.network.interceptor.TokenInterceptor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Test
import org.junit.Assert.*

// Fake UserPreferencesDataStore for testing
class FakeUserPreferencesDataStore(private val token: String?) : IUserPreferencesDataStore {
    override val authToken: Flow<String?> = flowOf(token)
    override val username: Flow<String?> = flowOf(null)
    override val userId: Flow<String?> = flowOf(null)

    override suspend fun saveAuthToken(token: String) {}
    override suspend fun clearAuthToken() {}
    override suspend fun saveUsername(username: String) {}
    override suspend fun saveUserId(userId: String) {}
    override suspend fun clearAll() {}
}

class GraphQLServiceTest {

    @Test
    fun `getRepository returns repository details`() = runTest {
        // Given
        val owner = "octocat"
        val name = "Hello-World"
        val fakeToken = "YOUR_TOKEN" // Replace with a valid token for testing if needed

        val fakeUserPreferencesDataStore = FakeUserPreferencesDataStore(fakeToken)

        val okHttpClient =
            OkHttpClient.Builder().addInterceptor(TokenInterceptor(fakeUserPreferencesDataStore))
                .build()

        val apolloClient = ApolloClient.Builder().serverUrl("https://api.github.com/graphql")
            .okHttpClient(okHttpClient).build()

        val graphQLService = GraphQLService(apolloClient)

        // When
        val response = graphQLService.getRepository(owner, name)

        // Then
        assertNotNull(response)
        assertTrue(response.data != null)
        assertEquals("Hello-World", response.data?.repository?.comparisonFields?.name)
        assertEquals("octocat", response.data?.repository?.comparisonFields?.owner?.login)
    }
}
