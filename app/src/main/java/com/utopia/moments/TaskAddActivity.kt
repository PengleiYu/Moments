package com.utopia.moments

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text

class TaskAddActivity : AppCompatActivity() {
  private lateinit var taskType: TaskType
  private val repository = Repository(SPDataSource(this))
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val extraTaskType = intent.getStringExtra(EXTRA_TASK_TYPE) ?: throw IllegalStateException()
    taskType = TaskType.valueOf(extraTaskType)

    setContent {
      Text(text = "Hello, taskType=$taskType")
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

