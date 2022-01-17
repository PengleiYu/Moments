package com.utopia.moments

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class TaskAddActivity : AppCompatActivity() {

  companion object {
    private const val EXTRA_TASK_TYPE = "extra_task_type"
    fun start(activity: AppCompatActivity, taskType: TaskType) {
      val intent = Intent(activity, TaskAddActivity::class.java)
      intent.putExtra(EXTRA_TASK_TYPE, taskType.name)
      activity.startActivity(intent)
    }
  }

  private lateinit var taskType: TaskType
  private val repository = Repository(SPDataSource(this))
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val extraTaskType = intent.getStringExtra(EXTRA_TASK_TYPE) ?: throw IllegalStateException()
    taskType = TaskType.valueOf(extraTaskType)

    setContent {
      val textTitle = rememberSaveable { mutableStateOf("Text") }
      val fromDate = rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
      val toDate = rememberSaveable { mutableStateOf(System.currentTimeMillis()) }

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
            actions = {
              IconButton(onClick = {
                saveTask(taskType, textTitle.value, fromDate.value, toDate.value)
              }) {
                Icon(Icons.Outlined.Done, "保存")
              }
            }
          )
        },
        content = {
          Column {
            TextField(
              value = textTitle.value,
              onValueChange = { textTitle.value = it },
              label = {
                Text(text = "命名你的进度")
              }
            )

            Text(text = "开始时间")
            TimePickerButton(timeState = fromDate, activity = this@TaskAddActivity)

            Text(text = "结束时间")
            TimePickerButton(timeState = toDate, activity = this@TaskAddActivity)
          }
        }
      )
    }
  }

  private fun saveTask(taskType: TaskType, title: String, fromDate: Long, toDate: Long) {
    val taskData = TaskData(-1, title, taskType, fromDate, toDate)
    repository.addTask(taskData)
    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
    finish()
  }
}

@Composable
private fun TimePickerButton(timeState: MutableState<Long>, activity: AppCompatActivity) {
  Button(onClick = {
    showDatePicker(activity) {
      val time = it ?: return@showDatePicker
      timeState.value = time
    }
  }) {
    Text(text = convertLongToDate(timeState.value))
  }
}

private fun convertLongToDate(time: Long): String {
  val date = Date(time)
  val format = SimpleDateFormat("yyyy.MM.dd", Locale.CHINA)
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