package com.example.aeye

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aeye.ui.theme.AEyeTheme

@Composable
fun ImageCard(image: ByteArray) {
    val bitmap = remember {
        BitmapFactory.decodeByteArray(image, 0, image.size)
    }
    val imageBitmap = remember { bitmap.asImageBitmap() }

    Image(
        bitmap = imageBitmap,
        contentDescription = "An input image captured by camera", // alt text
        modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
    )
}