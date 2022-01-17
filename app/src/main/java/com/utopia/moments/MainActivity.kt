package com.utopia.moments

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.utopia.moments.ui.theme.MomentsTheme
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
  private val repository = Repository(SPDataSource(this))
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MomentsTheme {
        Surface(color = MaterialTheme.colors.background) {
          Column {
            AppBar(getString(R.string.app_name)) {
              val items = TaskType.values().map(TaskType::toString)
              DropDownMenu(items) {
                val taskType = TaskType.valueOf(items[it])
                openTaskAddPage(taskType)
              }
            }

            val items = repository.getTasks().map(TaskData::toItem)
            Content(items)
          }
        }
      }
    }
  }

  private fun openTaskAddPage(taskType: TaskType) {
    TaskAddActivity.start(this, taskType)
  }
}


@Composable
private fun AppBar(
  title: String, actions: @Composable (RowScope.() -> Unit)
) {
  TopAppBar(
    title = {
      Text(text = title)
    },
    actions = actions
  )
}

@Composable
private fun DropDownMenu(items: List<String>, onItemClick: (Int) -> Unit) {
  val expanded = remember { mutableStateOf(false) }
  Column {
    IconButton(onClick = { expanded.value = true }) {
      Icon(Icons.Default.MoreVert, "more")
    }
    DropdownMenu(
      expanded = expanded.value,
      onDismissRequest = { expanded.value = false },
    ) {
      items.forEachIndexed { index, text ->
        DropdownMenuItem(onClick = { onItemClick(index) }) {
          Text(text = text)
        }
      }
    }
  }
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