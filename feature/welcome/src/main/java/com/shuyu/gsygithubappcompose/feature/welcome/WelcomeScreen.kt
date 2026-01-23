package com.shuyu.gsygithubappcompose.feature.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.shuyu.gsygithubappcompose.core.ui.LocalNavigator

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = hiltViewModel()
) {
    val navigator = LocalNavigator.current
    val destination by viewModel.navigationDestination.collectAsState()

    LaunchedEffect(destination) {
        destination?.let { dest ->
            navigator.replace(dest)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = "GSY GitHub Logo",
                contentScale = ContentScale.Inside,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp)
            ) {
                // Lottie Animation
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.Asset("launcher-lottie.json")
                )
                LottieAnimation(
                    composition = composition,
                    restartOnPlay = false,
                    speed = 4f,
                    modifier = Modifier
                        .size(300.dp)
                        .padding(bottom = 16.dp)
                )

                // App Title
                Text(
                    text = "",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Subtitle
                Text(
                    text = "Jetpack Compose",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}
