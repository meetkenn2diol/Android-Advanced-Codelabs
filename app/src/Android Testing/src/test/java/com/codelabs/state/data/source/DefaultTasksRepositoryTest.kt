package com.codelabs.state.data.source

import com.codelabs.state.MainCoroutineRule
import com.codelabs.state.data.Task
import com.codelabs.state.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class DefaultTasksRepositoryTest() {
    //set the main coroutines dispatcher for unit testing...
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2")
    private val task3 = Task("Title3", "Description3")
    private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
    private val localTasks = listOf(task3).sortedBy { it.id }
    private val newTasks = listOf(task3).sortedBy { it.id }

    private lateinit var tasksRemoteDataSource: FakeDataSource
    private lateinit var tasksLocalDataSource: FakeDataSource


    // Class under test
    private lateinit var tasksRepository: DefaultTasksRepository

    @Before
    fun createRepository(){
        tasksRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
        tasksLocalDataSource = FakeDataSource(localTasks.toMutableList())

        // STEP: Given a DefaultTasksRepository
        // Get a reference to the class under test
        tasksRepository = DefaultTasksRepository(
            // TODO Dispatchers.Unconfined should be replaced with Dispatchers.Main
            //  this requires understanding more about coroutines + testing
            //  so we will keep this as Unconfined for now.
            //Dispatchers.Main will be used now because it has been modified in the MainCoroutineRule()
            tasksRemoteDataSource, tasksLocalDataSource, Dispatchers.Main
        )
    }

   //Generally, only create one TestCoroutineDispatcher to run a test.
    @Test
    fun getTasks_requestsAllTasksFromRemoteDataSource() = mainCoroutineRule.runBlockingTest{
        // STEP: When tasks are requested from the tasks repository
        val tasks = tasksRepository.getTasks(true) as Result.Success

        // STEP: Then tasks are loaded from the remote data source
        assertThat(tasks.data, IsEqual(remoteTasks))
    }

}