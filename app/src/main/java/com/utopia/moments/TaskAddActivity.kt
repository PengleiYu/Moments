package com.utopia.moments

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class TaskAddActivity : AppCompatActivity() {
  private lateinit var taskType: TaskType
  private val repository = Repository(SPDataSource(this))
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val extraTaskType = intent.getStringExtra(EXTRA_TASK_TYPE) ?: throw IllegalStateException()
    taskType = TaskType.valueOf(extraTaskType)

    setContent {
      Scaffold(
        topBar = {
          TopAppBar(
            title = {
              Text(text = "新建")
            },
            navigationIcon = {
              IconButton(onClick = { finish() }) {
                Icon(Icons.Default.ArrowBack, "back")
              }
            },
          )
        },
        content = {
          Column {
            val text = rememberSaveable { mutableStateOf("Text") }
            TextField(
              value = text.value,
              onValueChange = { text.value = it },
              label = {
                Text(text = "命名你的进度")
              }
            )

            Text(text = "开始时间")
            val fromDate = remember { mutableStateOf(System.currentTimeMillis()) }
            Button(onClick = {
              showDatePicker(this@TaskAddActivity) {
                val time = it ?: return@showDatePicker
                fromDate.value = time
              }
            }) {
              Text(text = convertLongToDate(fromDate.value))
            }
          }
        }
      )
    }
  }

  companion object {
    private const val EXTRA_TASK_TYPE = "extra_task_type"
    fun start(activity: AppCompatActivity, taskType: TaskType) {
      val intent = Intent(activity, TaskAddActivity::class.java)
      intent.putExtra(EXTRA_TASK_TYPE, taskType.name)
      activity.startActivity(intent)
    }
  }
}

private fun convertLongToDate(time: Long): String {
  val date = Date(time)
  val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.CHINA)
  return format.format(date)
}

private fun showDatePicker(
  activity: AppCompatActivity,
  updateDate: (Long?) -> Unit
) {
  val picker = MaterialDatePicker.Builder.datePicker().build()
  picker.show(activity.supportFragmentManager, picker.toString())
  picker.addOnPositiveButtonClickListener {
    updateDate(it)
  }
}

@Preview(
  showBackground = true
)
@Composable
private fun PreviewTaskAdd() {
}