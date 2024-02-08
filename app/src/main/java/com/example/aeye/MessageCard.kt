package com.example.aeye

import android.content.ClipData
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import android.content.ClipboardManager
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.example.aeye.ui.theme.AEyeTheme
import com.google.accompanist.permissions.isGranted
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.material3.RichText

@Composable
fun MessageCard(message: IMessage) {
     if(message.type != MessageType.User && message.type != MessageType.Model)
        return; // Only render UI for user response messages

    val context = LocalContext.current

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .then(
                when (message.type) {
                    MessageType.User -> Modifier.padding(start = 24.dp)
                    MessageType.Model -> Modifier.padding(end = 24.dp)
                    else -> Modifier.padding(0.dp)
                },
            )
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
        ){
            Column()
            {
                RichText () {
                    Markdown (message.text.value)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(0.dp, 8.dp, 0.dp, 0.dp),
                    horizontalArrangement = if (message.type == MessageType.User) Arrangement.End else Arrangement.Start,
                )
                {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("A Eye message", message.text.value))
                        },
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically)
                        {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy"
                            )
                            Text(
                                "Copy",
                                modifier = Modifier.padding(start= 8.dp)
                            )
                        }
                    }
                    ReadAloudButton(
                        modifier = Modifier.padding(start= 8.dp),
                        message.text.value
                    )
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun MessageCardPreview() {
    AEyeTheme {
        MessageCard(Message("Hello there! this is a test with wvery long test lorem ipsum whatevervewrfgeryfgeraigerwogheqarughed9gheru9gyhre90hgeu9ghvpuer9ghepq9uh vr fberfgw euf", MessageType.User))
    }
}