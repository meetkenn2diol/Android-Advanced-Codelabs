package com.codelabs.state.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.codelabs.state.Event
import com.codelabs.state.MainCoroutineRule
import com.codelabs.state.R
import com.codelabs.state.data.Task
import com.codelabs.state.data.source.FakeTestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsNot.not
import org.hamcrest.core.IsNull.nullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//info: Add @RunWith(AndroidJUnit4::class) when using ApplicationProvider.getApplicationContext
//@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Use a fake repository to be injected into the viewmodel
    private lateinit var tasksRepository: FakeTestRepository

    // Subject under test
    private lateinit var tasksViewModel: TasksViewModel

    @Before
            /**
             * STEP: Given a TasksViewModel
             */
    fun setUpViewModel(){
        // We initialise the tasks to 3, with one active and two completed
        tasksRepository = FakeTestRepository()
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        tasksRepository.addTasks(task1, task2, task3)

        tasksViewModel = TasksViewModel(tasksRepository)


      //  tasksViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())
    }


    @Test
    fun addNewTask_setsNewTaskEvent() {

        //STEP: Given a  TasksViewModel using Robolectric testing library
        //code with the @Before handles this step

        //STEP: When adding a new task
        tasksViewModel.addNewTask()

        //STEP: Then the new task event is triggered
        // TODO: 12/15/2021 test livedata
        /*
        *To test LiveData it's recommended you do two things:
        *
        * 1.Use InstantTaskExecutorRule
        * 2.Ensure LiveData observation
         */
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()

        assertThat(value.getContentIfNotHandled(), not(nullValue()))

    }

    @Test
    fun setFilterAllTasks_tasksAddViewVisible() {
        // STEP: Given a fresh ViewModel
        //code with the @Before handles this step

        // STEP: When the filter type is ALL_TASKS
        tasksViewModel.setFiltering(TasksFilterType.ACTIVE_TASKS)

        // STEP: Then the "Add task" action is visible
        assertThat(tasksViewModel.tasksAddViewVisible.getOrAwaitValue (), `is`(true))
    }

    @Test
    fun completeTask_dataAndSnackbarUpdated() {
        // Create an active task and add it to the repository.
        val task = Task("Title", "Description")
        tasksRepository.addTasks(task)

        // Mark the task as complete task.
        tasksViewModel.completeTask(task, true)

        // Verify the task is completed.
        assertThat(tasksRepository.tasksServiceData[task.id]?.isCompleted, `is`(true))

        // Assert that the snackbar has been updated with the correct text.
        val snackbarText: Event<Int> =  tasksViewModel.snackbarText.getOrAwaitValue()
        assertThat(snackbarText.getContentIfNotHandled(), `is`(R.string.task_marked_complete))
    }
}