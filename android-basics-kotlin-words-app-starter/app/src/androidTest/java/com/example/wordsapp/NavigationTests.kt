package com.example.wordsapp

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.runner.AndroidJUnit4
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


// test 실행자 지정
@RunWith(AndroidJUnit4::class)
class NavigationTests {

    @Test
    fun navigate_to_words_nav_component(){
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        //specify that we want to launch the LetterListFragment.
        val letterListScenario = launchFragmentInContainer<LetterListFragment>(themeResId = R.style.Theme_Words)
        letterListScenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.recycler_view))
            .perform(
                RecyclerViewActions
                .actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click()))

        assertEquals(navController.currentDestination?.id, R.id.wordListFragment)
    }



}