package com.example.tapapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    private val loveMessages = listOf(
        "❤️ Thinking about you 💕",
        "💖 Missing you right now",
        "😘 Sending you a virtual kiss",
        "💝 You mean the world to me",
        "💞 Can't wait to see you again",
        "🥰 You make my heart smile",
        "💘 Every moment with you is magical",
        "💓 Just wanted to brighten your day",
        "💗 You're always in my thoughts",
        "🧸 Just hugged my pillow pretending it was you",
        "🌙 Dreaming of you even when I'm awake",
        "🦋 You give me butterflies… and also indigestion (the good kind)",
        "✨ I fell for you harder than my WiFi signal drops",
        "💋 If kisses were pixels, I’d send you 4K resolution",
        "🌹 Just a daily reminder that you're my favorite notification",
        "🫶 You're the peanut butter to my existential dread",
        "🎶 My heart has a playlist and it's just you on loop",
        "📦 If I could, I’d Amazon Prime my hugs to you",
        "🧁 You're sweeter than a triple-chocolate lava cake on Valentine's Day"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NtfyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen {
                        sendNtfyNotification()
                    }
                }
            }
        }
    }

    private fun sendNtfyNotification() {
        lifecycleScope.launch {
            try {
                val randomMessage = loveMessages.random()
                val emojiTitle = randomMessage.substringBefore(" ")
                val messageContent = randomMessage.substringAfter(" ")

                val success = withContext(Dispatchers.IO) {
                    sendNotification(
                        // topic = "your_subscribed_topic",
                        // topic = "your_partners subscribed_topic",
                        title = emojiTitle,
                        message = messageContent,
                        priority = "default"
                    )
                }

                if (success) {
                    Toast.makeText(this@MainActivity, "Love sent! ❤️", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to send notification", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun sendNotification(
        topic: String,
        title: String,
        message: String,
        priority: String = "default",
        tags: String? = null
    ): Boolean {
        return try {
            val url = URL("https://ntfy.sh/$topic")
            val connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "text/plain")
                setRequestProperty("Title", title)
                setRequestProperty("Priority", priority)
                tags?.let { setRequestProperty("Tags", it) }
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(message)
                writer.flush()
            }

            val responseCode = connection.responseCode
            connection.disconnect()

            responseCode == HttpURLConnection.HTTP_OK
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

@Composable
fun MainScreen(onButtonClick: () -> Unit) {
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            onClick = {
                isLoading = true
                onButtonClick()
                kotlinx.coroutines.GlobalScope.launch {
                    kotlinx.coroutines.delay(2000)
                    isLoading = false
                }
            },
            modifier = Modifier.size(140.dp),
            enabled = !isLoading,
            shape = CircleShape,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            } else {
                Text(
                    text = "❤️",
                    style = TextStyle(fontSize = 48.sp), // Bigger heart
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun NtfyAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color.Black,
            onPrimary = Color.White,
            primary = Color.DarkGray,
            surface = Color.Black
        ),
        typography = Typography(),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    NtfyAppTheme {
        MainScreen {}
    }
}
