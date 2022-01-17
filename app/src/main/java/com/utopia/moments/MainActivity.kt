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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.MutableLiveData
import com.utopia.moments.ui.theme.MomentsTheme
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
  private val repository = Repository(SPDataSource(this))
  private val liveData = MutableLiveData<List<ItemData>>()
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MomentsTheme {
        Surface(color = MaterialTheme.colors.background) {
          Scaffold(
            topBar = {
              AppBar(getString(R.string.app_name)) {
                val items = TaskType.values().map(TaskType::toString)
                DropDownMenu(items) {
                  val taskType = TaskType.valueOf(items[it])
                  openTaskAddPage(taskType)
                }
              }
            },
            content = {
              val items by liveData.observeAsState(initial = listOf())
              Content(items)
            }
          )
        }
      }
    }
  }

  private fun openTaskAddPage(taskType: TaskType) {
    TaskAddActivity.start(this, taskType)
  }

  override fun onResume() {
    super.onResume()
    val items = repository.getTasks().map(TaskData::toItem)
    liveData.value = items
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
        DropdownMenuItem(onClick = {
          onItemClick(index)
          expanded.value = false
        }) {
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
      Divider()
    }
  }
}

private fun TaskData.toItem(): ItemData {
  val day = TimeUnit.DAYS.toMillis(1)
  val total = endTime - startTime
  val elapsed = System.currentTimeMillis() - startTime
  val progress = elapsed.toFloat() / total.toFloat()
  return ItemData(
    progress,
    title,
    "按年.${elapsed / day}天/${total / day}天.已过去",
    (progress * 100).toInt(),
    "%"
  )
}

data class ItemData(
  val progress: Float,
  val title: String,
  val caption: String,
  val number: Int,
  val unit: String
)

@Composable
fun Item(data: ItemData) {
  Row {
    CircularProgressIndicator(
      progress = data.progress,
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