package com.example.worknear.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.worknear.R

@Composable
fun VideoBackground(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = {
            VideoView(it).apply {
                setVideoURI(Uri.parse("android.resource://${context.packageName}/${R.raw.intro_farmforce}"))
                setOnPreparedListener { mp ->
                    mp.isLooping = true
                    mp.start()
                }
            }
        }
    )
}
