package com.codelabs.state.util

import androidx.test.espresso.idling.CountingIdlingResource

/**
 * You've written an idling resource, so that Espresso waits for data to be loaded.What this means is that if your countingIdlingResource has a count greater than zero, or if there are pending data binding layouts, Espresso will wait.
 */
object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    /**
     * When the counter is greater than zero, the app is considered working.
     */
    fun increment() {
        countingIdlingResource.increment()
    }

    /**
     *   When the counter is zero, the app is considered idle.
     */
    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

/**
 * You can use `inline` keyword when you want to wrap a function
 */
inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    // Espresso does not work well with coroutines yet. See
    // https://github.com/Kotlin/kotlinx.coroutines/issues/982
    EspressoIdlingResource.increment() // Set app as busy.
    return try {
        function()
    } finally {
        EspressoIdlingResource.decrement() // Set app as idle.
    }
}