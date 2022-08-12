package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.Event
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class TasksViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @Test
    fun addNewTask_setsNewTaskEvent() {
        val taskViewModel = TasksViewModel(ApplicationProvider.getApplicationContext())
        taskViewModel.addNewTask()

        val value = taskViewModel.newTaskEvent.getOrAwaitValue()
        assertThat(value.getContentIfNotHandled(), not(nullValue()))


//        val observer = Observer<Event<Unit>> {}
//        try {
//            taskViewModel.newTaskEvent.observeForever(observer)
//            taskViewModel.addNewTask()
//            val value = taskViewModel.newTaskEvent.value
//
//            assertThat(value?.getContentIfNotHandled(), (not(nullValue())))
//        } finally {
//            taskViewModel.newTaskEvent.removeObserver(observer)
//        }
    }
}