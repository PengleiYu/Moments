package com.utopia.moments

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

enum class TaskType {
  DATE, PROGRESS, AGE
}

data class TaskData(
  val id: Long,
  val title: String,
  val type: TaskType,
  val startTime: Long,
  val endTime: Long,
)

interface DataSource {
  fun addItem(task: TaskData)
  fun deleteItem(taskId: Long)
  fun updateItem(task: TaskData)
  fun getItem(taskId: Long): TaskData?
  fun getAllItem(): List<TaskData>
}

class SPDataSource(private val context: Context) : DataSource {
  @Synchronized
  override fun addItem(task: TaskData) {
    checkDataLoaded()
    val maxId = allData.keys.map(this::convertKey2Id).maxOrNull() ?: 0L
    val nextId = maxId + 1
    putItem(task.copy(id = nextId))
  }

  @Synchronized
  override fun deleteItem(taskId: Long) {
    checkDataLoaded()
    removeItem(taskId)
  }

  @Synchronized
  override fun updateItem(task: TaskData) {
    checkDataLoaded()
    putItem(task)
  }

  @Synchronized
  override fun getItem(taskId: Long): TaskData? {
    checkDataLoaded()
    return allData[convertId2Key(taskId)]?.toTask()
  }

  @Synchronized
  override fun getAllItem(): List<TaskData> {
    checkDataLoaded()
    return allData.map { it.value.toTask() }.toList()
  }

  private val allData = mutableMapOf<String, String>()
  private val gson = Gson()

  @Volatile
  private var isLoaded = false

  private fun checkDataLoaded() {
//    if (isLoaded) return
    // TODO: 2022/1/17 暂时每次都重新读取

    getSP().all.onEach {
      allData[it.key] = it.value.toString()
    }
    isLoaded = true
  }

  private fun putItem(item: TaskData) {
    val key = convertId2Key(item.id)
    val json = item.toJson()
    allData[key] = json
    getSP().edit().putString(key, json).apply()
  }

  private fun removeItem(id: Long) {
    val key = convertId2Key(id)
    allData.remove(key = key)
    getSP().edit().remove(key).apply()
  }

  private fun String.toTask(): TaskData {
    return gson.fromJson(this, TaskData::class.java)
  }

  private fun TaskData.toJson(): String {
    return gson.toJson(this)
  }

  private fun getSP(): SharedPreferences {
    return context.getSharedPreferences("moment_task_sp", Context.MODE_PRIVATE)
  }

  private fun convertId2Key(taskId: Long): String {
    return "task$taskId"
  }

  private fun convertKey2Id(taskKey: String): Long {
    return taskKey.removePrefix("task").toLong()
  }
}

class Repository(private val dataSource: DataSource) {
  fun addTask(task: TaskData) {
    dataSource.addItem(task)
  }

  fun deleteTask(task: TaskData) {
    dataSource.deleteItem(task.id)
  }

  fun updateTask(task: TaskData) {
    dataSource.updateItem(task)
  }

  fun getTask(taskId: Long): TaskData? {
    return dataSource.getItem(taskId)
  }

  fun getTasks(): List<TaskData> {
    return dataSource.getAllItem()
  }

}