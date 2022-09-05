/*
 * Copyright (C) 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.kotlincoroutines.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.kotlincoroutines.fakes.MainNetworkFake
import com.example.android.kotlincoroutines.fakes.TitleDaoFake
import com.example.android.kotlincoroutines.main.utils.MainCoroutineScopeRule
import com.example.android.kotlincoroutines.main.utils.getValueForTest
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    @get:Rule // rule: a way to run code before & after the execution of a test in Junit
            /*
            * Dispatchers.Main 이 TestCoroutineDispatcher를 이용하도록 함.
            * allows tests to advance a virtual-clock for testing
            * 이건 가상 시계를 앞당긴다는 말인데, delay 관련한 문제를 해결하는데 사용하는 듯하다.
            * TestCoroutineDispatcher : from kotlinx-coroutine-test
            * */
    val coroutineScope = MainCoroutineScopeRule()

    @get:Rule
            /**
             * architecture component - related background 작업을 동일한 스레드에서 돌게해서
             * 동기적 처리 가능하게 함. (LiveData의 PostValue등은 백그라운드에서 작동하므로,
             * 값 변경 전에 테스트가 끝날 수 있음)
             * */
    //
    val instantTaskExecutorRule = InstantTaskExecutorRule()// Livedata

    lateinit var subject: MainViewModel

    @Before
    fun setup() {
        subject = MainViewModel(
            TitleRepository(
                MainNetworkFake("OK"),
                TitleDaoFake("initial")
            )
        )
    }

    @Test
    fun whenMainClicked_updatesTaps() {
        subject.onMainViewClicked()
        Truth.assertThat(subject.taps.getValueForTest()).isEqualTo("0 taps")
        // 1초후 resume되기로 스케줄된 코루틴을 바로 실행한다.
        coroutineScope.advanceTimeBy(1_000)
        Truth.assertThat(subject.taps.getValueForTest()).isEqualTo("1 taps")

    }
}