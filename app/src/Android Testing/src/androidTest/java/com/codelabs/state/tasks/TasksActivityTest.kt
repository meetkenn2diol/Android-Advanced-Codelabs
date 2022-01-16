package com.codelabs.state.tasks


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.SmallTest
import com.codelabs.state.R
import com.codelabs.state.ServiceLocator
import com.codelabs.state.data.Task
import com.codelabs.state.data.source.TasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.codelabs.state.util.DataBindingIdlingResource
import com.codelabs.state.util.EspressoIdlingResource
import com.codelabs.state.util.monitorActivity
import org.hamcrest.core.IsNot.not

/**
 * A true end-to-end test.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class TasksActivityTest {
    // An idling resource that waits for Data Binding to have no pending bindings.
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    private lateinit var repository: TasksRepository

    @Before
    fun init() {
        repository = ServiceLocator.provideTasksRepository(getApplicationContext())
        runBlocking {
            repository.deleteAllTasks()
        }
    }
    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }
    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun editTask() = runBlocking {
        // Set initial state.
        repository.saveTask(Task("TITLE1", "DESCRIPTION"))

        // Start up Tasks screen.
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario) // LOOK HERE

        // Click on the task on the list and verify that all the data is correct.
        onView(withText("TITLE1")).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("TITLE1")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("DESCRIPTION")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

        // Click on the edit button, edit, and save.
        onView(withId(R.id.edit_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("NEW TITLE"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("NEW DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // Verify task is displayed on screen in the task list.
        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        // Verify previous task is not displayed.
        onView(withText("TITLE1")).check(doesNotExist())
        // Make sure the activity is closed before resetting the db.
        activityScenario.close()
    }
    @Test
    fun createOneTask_deleteTask() {

        //Start TasksActivity.
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        // Add active task
        onView(withId(R.id.add_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text))
            .perform(typeText("TITLE1"), closeSoftKeyboard())
        onView(withId(R.id.add_task_description_edit_text)).perform(typeText("DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // Open it in details view
        onView(withText("TITLE1")).perform(click())
        // Click delete task in menu
        onView(withId(R.id.menu_delete)).perform(click())

        // Verify it was deleted
        onView(withId(R.id.menu_filter)).perform(click())
        onView(withText(R.string.nav_all)).perform(click())
        onView(withText("TITLE1")).check(doesNotExist())
        // Make sure the activity is closed before resetting the db:
        activityScenario.close()

    }
}