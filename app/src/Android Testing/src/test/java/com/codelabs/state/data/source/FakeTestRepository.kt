package com.codelabs.state.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codelabs.state.data.Result
import com.codelabs.state.data.Task
import kotlinx.coroutines.runBlocking

/**
 * A fake TaskRepository class to mimic the real TaskRepository. TaskRepository was written to allow testing of DefaultTaskRepository.
 */
class FakeTestRepository : TasksRepository {
//A boolean flag used to test how to handle an error
    private var shouldReturnError = false

    //representing the current list of tasks
    var tasksServiceData: LinkedHashMap<String, Task> = LinkedHashMap()

    //represent observable tasks.
    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test exception"))
        }
        return Result.Success(tasksServiceData.values.toList())
    }

    override suspend fun refreshTasks() {
        observableTasks.value = getTasks()
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        runBlocking { refreshTasks() }
        return observableTasks
    }

    override suspend fun refreshTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        TODO("Not yet implemented")
    }

    /**
     * Relies on [getTasks] to fetch data and picks the task with the same ID.
     */
    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
       if(shouldReturnError){
           return Result.Error(Exception("Test exception"))
       }
        tasksServiceData[taskId]?.let{
            return Result.Success(it)
        }
        return Result.Error(Exception("Could not find task"))
    }

    /**
     * helper method specifically for tests that lets you add tasks
     */
    fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            tasksServiceData[task.id] = task
        }
        runBlocking { refreshTasks() }
    }

    override suspend fun saveTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun completeTask(task: Task) {
        val completedTask = task.copy(isCompleted = true)
        tasksServiceData[task.id] = completedTask
        refreshTasks()
    }

    override suspend fun completeTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearCompletedTasks() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllTasks() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(taskId: String) {
        TODO("Not yet implemented")
    }

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

}