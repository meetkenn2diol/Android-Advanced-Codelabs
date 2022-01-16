package com.codelabs.state.data.source

import androidx.lifecycle.LiveData
import com.codelabs.state.data.Result
import com.codelabs.state.data.Task

/**
 * FakeDataSource, which will be a test double of a LocalDataSource and RemoteDataSource.
 *
 * @param tasks: This is the list of tasks that "fakes" being a database or server response
 */
class FakeDataSource(var tasks: MutableList<Task>? = mutableListOf()): TasksDataSource {
    override fun observeTasks(): LiveData<Result<List<Task>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTasks(): Result<List<Task>> {
        tasks?.let { return Result.Success(ArrayList(it)) }
        return Result.Error(
            Exception("Tasks not found")
        )
    }

    override suspend fun refreshTasks() {
        TODO("Not yet implemented")
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun saveTask(task: Task) {
       tasks?.add(task)
    }

    override suspend fun completeTask(task: Task) {
        TODO("Not yet implemented")
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
    tasks?.clear()
    }

    override suspend fun deleteTask(taskId: String) {
        TODO("Not yet implemented")
    }
}