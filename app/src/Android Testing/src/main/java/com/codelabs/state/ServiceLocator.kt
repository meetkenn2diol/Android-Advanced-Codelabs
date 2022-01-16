package com.codelabs.state

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.codelabs.state.data.source.DefaultTasksRepository
import com.codelabs.state.data.source.TasksDataSource
import com.codelabs.state.data.source.TasksRepository
import com.codelabs.state.data.source.local.TasksLocalDataSource
import com.codelabs.state.data.source.local.ToDoDatabase
import com.codelabs.state.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.runBlocking

object ServiceLocator {

    private var database: ToDoDatabase? = null

    private val lock = Any()

    @Volatile
    var tasksRepository: TasksRepository? = null
        @VisibleForTesting set

    fun provideTasksRepository(context: Context): TasksRepository {
        synchronized(this) {
            return tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): TasksRepository {
        val newRepo =
            DefaultTasksRepository(TasksRemoteDataSource, createTaskLocalDataSource(context))
        tasksRepository = newRepo
        return newRepo
    }

    private fun createTaskLocalDataSource(context: Context): TasksDataSource {
        val database = database ?: createDataBase(context)
        return TasksLocalDataSource(database.taskDao())
    }

    private fun createDataBase(context: Context): ToDoDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java, "Tasks.db"
        ).build()
        database = result
        return result
    }

    /**
     * A testing-specific method called resetRepository which clears out the database and sets both the repository and database to null.
     */
    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                TasksRemoteDataSource.deleteAllTasks()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tasksRepository = null
        }
    }
}