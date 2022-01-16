package com.utopia.moments

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.utopia.moments.ui.theme.MomentsTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MomentsTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
          val data = ItemData(50f, "Title", "caption", 50f, "%")
          Item(data = data)
        }
      }
    }
  }
}

data class ItemData(
  val progress: Float,
  val title: String,
  val caption: String,
  val number: Float,
  val unit: String
)

@Composable
fun Item(data: ItemData) {
  Row {
    CircularProgressIndicator(
      progress = data.progress / 100f,
    )
    Column(
      Modifier.weight(1f),
    ) {
      Text(text = data.title)
      Text(text = data.caption)
    }
    Column(horizontalAlignment = Alignment.End) {
      Text(text = data.number.toString())
      Text(text = data.unit)
    }
  }
}

@Preview(
  showBackground = true,
  showSystemUi = true,
)
@Composable
fun DefaultPreview() {
  MomentsTheme {
    val data = ItemData(50f, "Title", "caption", 50f, "%")
    Item(data = data)
  }
}