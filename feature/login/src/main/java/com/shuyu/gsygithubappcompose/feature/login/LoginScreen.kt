package com.shuyu.gsygithubappcompose.feature.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shuyu.gsygithubappcompose.core.common.R
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val navigator = LocalNavigator.current
    val context = LocalContext.current

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            navigator.replace("home")
        }
    }

    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (uiState.showOAuthWebView) {
        OAuthScreen(onCodeReceived = { code ->
            viewModel.handleOAuthCode(
                BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET, code
            )
        }, onCancel = { viewModel.cancelOAuthFlow() }, onError = { viewModel.cancelOAuthFlow() })
    } else {
        LoginContent(
            uiState = uiState,
            onTokenChange = { viewModel.onTokenChange(it) },
            onLoginClick = { viewModel.login() },
            onOAuthClick = { viewModel.startOAuthFlow() })
    }
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onTokenChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onOAuthClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp)
                ) {
                    // Logo
                    Image(
                        painter = painterResource(id = R.mipmap.ic_launcher),
                        contentDescription = stringResource(id = R.string.app_name),
                        modifier = Modifier.size(90.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title
                    Text(
                        text = stringResource(id = R.string.login_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(id = R.string.login_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Token Input Field
                    OutlinedTextField(
                        value = uiState.token,
                        onValueChange = onTokenChange,
                        label = { Text(stringResource(id = R.string.login_token_label)) },
                        placeholder = { Text(stringResource(id = R.string.login_token_hint)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Key, contentDescription = "Token"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        enabled = !uiState.isLoading,
                        isError = uiState.error != null,
                        shape = MaterialTheme.shapes.medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Helper text
                    Text(
                        text = stringResource(id = R.string.login_token_helper),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Token Login Button
                        Button(
                            onClick = onLoginClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            enabled = !uiState.isLoading && uiState.token.isNotEmpty(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = stringResource(id = R.string.login_button),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // OAuth Button
                        Button(
                            onClick = onOAuthClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            enabled = !uiState.isLoading,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(
                                text = stringResource(id = R.string.oauth_button),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OAuthScreen(
    onCodeReceived: (String) -> Unit, onCancel: () -> Unit, onError: () -> Unit
) {
    val clientId = BuildConfig.CLIENT_ID
    val oauthUrl =
        "https://github.com/login/oauth/authorize?client_id=$clientId&state=app&scope=user,repo,gist,notifications,read:org,workflow&redirect_uri=gsygithubapp://authed"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.github_authorization)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.nav_search)
                        )
                    }
                })
        }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            OAuthWebView(
                url = oauthUrl, onCodeReceived = onCodeReceived, onError = onError
            )
        }
    }
}
