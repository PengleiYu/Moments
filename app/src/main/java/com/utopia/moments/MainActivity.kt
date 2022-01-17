package com.utopia.moments

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
  private val liveData = MutableLiveData<List<TaskData>>()
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
              Content(items.map(TaskData::toItem)) {
                val taskData = items[it]
                deleteTask(taskData)
              }
            }
          )
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    refresh()
  }

  private fun deleteTask(taskData: TaskData) {
    repository.deleteTask(taskData)
    refresh()
  }

  private fun openTaskAddPage(taskType: TaskType) {
    TaskAddActivity.start(this, taskType)
  }

  private fun refresh() {
    val items = repository.getTasks()
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
private fun Content(dataList: List<ItemData>, onItemClick: (Int) -> Unit) {
  Column {
    dataList.mapIndexed { index, itemData ->
      Box(modifier = Modifier.clickable { onItemClick(index) }) {
        Item(data = itemData)
      }
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
    "按年.${countDays(elapsed, day)}天/${countDays(total, day)}天.已过去",
    (progress * 100).toInt(),
    "%"
  )
}

private fun countDays(elapsed: Long, day: Long) =
  ((elapsed / day.toFloat()) + 0.5).toInt()

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
    val dataList = listOf(
      ItemData(.4f, "2022年进度", "按年.16天/365天.已过去", 42, "%"),
      ItemData(.14f, "2022年进度", "按年.16天/365天.已过去", 40, "%"),
      ItemData(.84f, "2022年进度", "按年.16天/365天.已过去", 44, "%"),
      ItemData(.94f, "2022年进度", "按年.16天/365天.已过去", 24, "%"),
      ItemData(.40f, "2022年进度", "按年.16天/365天.已过去", 94, "%"),
      ItemData(.49f, "2022年进度", "按年.16天/365天.已过去", 34, "%"),
    )

    Content(dataList) {}
  }
}