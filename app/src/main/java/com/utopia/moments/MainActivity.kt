package com.utopia.moments

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.utopia.moments.ui.theme.MomentsTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
  private val repository = Repository(SPDataSource(this))
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MomentsTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
          AppBar(title = getString(R.string.app_name)) {
            Toast.makeText(this, "AddItem", Toast.LENGTH_SHORT).show()
          }
          val items = repository.getTasks().map(TaskData::toItem)
          Content(items)
        }
      }
    }
  }
}

@Composable
private fun AppBar(title: String, clickListener: () -> Unit) {
  TopAppBar(
    title = {
      Text(text = title)
    },
    actions = {
      IconButton(onClick = clickListener) {
        Icon(Icons.Filled.Add, "addItem")
      }
    }
  )
}

@Composable
private fun Content(dataList: List<ItemData>) {
  Column {
    dataList.map {
      Item(data = it)
    }
  }
}

private fun TaskData.toItem(): ItemData {
  val total = endTime - startTime
  val elapsed = System.currentTimeMillis() - startTime
  val progress = elapsed.toFloat() / total
  return ItemData(
    progress,
    title,
    "按年.${TimeUnit.DAYS.toDays(elapsed)}/${TimeUnit.DAYS.toDays(total)}.已过去",
    progress,
    "%"
  )
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
    Content(itemDataList)
  }
}