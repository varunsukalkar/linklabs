package com.example.myapplication
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }
    }


    @Composable
    fun SplashScreen() {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.jay))
        val progress by animateLottieCompositionAsState(composition = composition)

        // Navigate to MainActivity after animation ends
        LaunchedEffect(progress) {
            if (progress == 1f) {
                // Optional delay for smooth transition
                delay(500)
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish() // Close SplashActivity
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // Set background color for better visibility
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Link Labs" ,
                    fontWeight = FontWeight.Bold,
                   fontSize=30.sp,
                    modifier = Modifier.padding(bottom = 16.dp) // Space between text and animation
                )
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(300.dp)
                )
            }
        }
    }
}
