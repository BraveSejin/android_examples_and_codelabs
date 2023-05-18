/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.advancedcoroutines

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.android.advancedcoroutines.util.CacheOnSuccess
import com.example.android.advancedcoroutines.utils.ComparablePair
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

/**
 * Repository module for handling data operations.
 *
 * This PlantRepository exposes two UI-observable database queries [plants] and
 * [getPlantsWithGrowZone].
 *
 * To update the plants cache, call [tryUpdateRecentPlantsForGrowZoneCache] or
 * [tryUpdateRecentPlantsCache].
 */
class PlantRepository private constructor(
    private val plantDao: PlantDao,
    private val plantService: NetworkService,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    private var plantsListSortOrderCache = CacheOnSuccess(onErrorFallback = { listOf<String>() }) {
        plantService.customPlantSortOrder()
    }

    private fun List<Plant>.applySort(customSortOrder: List<String>): List<Plant> {
        return sortedBy { plant -> // selector를 넘김. selector에 명시된 값을 이용해서 정렬해준다.
            val positionForItem = customSortOrder.indexOf(plant.plantId).let { order ->
                if (order > -1) order else Int.MAX_VALUE
            }
            ComparablePair(positionForItem, plant.name)
        }
    }

    @AnyThread
    suspend fun List<Plant>.applyMainSafeSort(customSortOrder: List<String>) =
        withContext(defaultDispatcher) {
            this@applyMainSafeSort.applySort(customSortOrder)
        }

    /**
     * Fetch a list of [Plant]s from the database.
     * Returns a LiveData-wrapped List of Plants.
     */
    val plants: LiveData<List<Plant>> = liveData<List<Plant>> {
        val plantsLiveData = plantDao.getPlants()
        val customSortOrder = plantsListSortOrderCache.getOrAwait()
        emitSource(plantsLiveData.map { plantsList ->
            plantsList.applySort(customSortOrder)
        })
    }
    val plantsFlow: Flow<List<Plant>>
        get() = plantDao.getPlantsFlow()
            .combine(customSortFlow) { plants, sortOrder ->
                plants.applySort(sortOrder)
            }.flowOn(defaultDispatcher)
            .conflate() // flowOn의 버퍼가 최신값만을 저장하게 한다.
    // flowOn : 함수  실행 "전에" 실행되고 수집될 플로우들이 돌아가는 스레드를 결정해준다.
    // introduces a buffer to send results from the new coroutine to later calls
    // flowOn뒤에있는 flow에 버퍼 값을 방출해준다.
    // 이 경우에는 라이브데이터에 값 방출해주는 것.

    private val customSortFlow = flow { emit(plantsListSortOrderCache.getOrAwait()) }
//        .onStart { // 이렇게 하면 flow 에서 값을 두번 방출하니까, 동작을 좀더 직관적으로 알 수 있음.
//            emit(listOf())
//            delay(1500) //
//        }
//    // Create a flow that calls a single function
//    private val customSortFlow = plantsListSortOrderCache::getOrAwait.asFlow()

    fun getPlantsWithGrowZoneFlow(growZoneNumber: GrowZone): Flow<List<Plant>> {
        // 여기서 이뤄지는 연산들 모두 main-safe함.
        return plantDao.getPlantsWithGrowZoneNumberFlow(growZoneNumber.number)
            .map { plantsList ->
                val sortOrderFromNetwork = plantsListSortOrderCache.getOrAwait()
                val nexValue = plantsList.applyMainSafeSort(sortOrderFromNetwork)
                nexValue
            }
    }
    // 이렇게 하면 디비에서 읽으면서 정렬순서를 받아올수도 있다.
    fun getPlantsWithGrowZoneFlow_mine(growZoneNumber: GrowZone): Flow<List<Plant>> {
        return plantDao.getPlantsWithGrowZoneNumberFlow(growZoneNumber.number)
            .combine(customSortFlow) { plants, sortOrder ->
                plants.applySort(sortOrder)
            }.flowOn(defaultDispatcher)
            .conflate()
    }

    /**
     * Fetch a list of [Plant]s from the database that matches a given [GrowZone].
     * Returns a LiveData-wrapped List of Plants.
     */
    fun getPlantsWithGrowZoneOld(growZone: GrowZone) = liveData {
        val plantsGrowZoneLiveData = plantDao.getPlantsWithGrowZoneNumber(growZone.number)
        val customSortOrder = plantsListSortOrderCache.getOrAwait()
        emitSource(plantsGrowZoneLiveData.map { plantsList ->
            plantsList.applySort(customSortOrder)
        })
    }

    fun getPlantsWithGrowZone(growZone: GrowZone) =
        plantDao.getPlantsWithGrowZoneNumber(growZone.number)
            .switchMap { plantsList ->
                liveData {
                    val customSortOrder = plantsListSortOrderCache.getOrAwait()
                    emit(plantsList.applyMainSafeSort(customSortOrder)) // 비동기 정렬
                }
            }

    /**
     * Returns true if we should make a network request.
     */
    private fun shouldUpdatePlantsCache(): Boolean {
        // suspending function, so you can e.g. check the status of the database here
        return true
    }

    /**
     * Update the plants cache.
     *
     * This function may decide to avoid making a network requests on every call based on a
     * cache-invalidation policy.
     */
    suspend fun tryUpdateRecentPlantsCache() {
        if (shouldUpdatePlantsCache()) fetchRecentPlants()
    }

    /**
     * Update the plants cache for a specific grow zone.
     *
     * This function may decide to avoid making a network requests on every call based on a
     * cache-invalidation policy.
     */
    suspend fun tryUpdateRecentPlantsForGrowZoneCache(growZoneNumber: GrowZone) {
        if (shouldUpdatePlantsCache()) fetchPlantsForGrowZone(growZoneNumber)
    }

    /**
     * Fetch a new list of plants from the network, and append them to [plantDao]
     */
    private suspend fun fetchRecentPlants() {
        val plants = plantService.allPlants()
        plantDao.insertAll(plants)
    }

    /**
     * Fetch a list of plants for a grow zone from the network, and append them to [plantDao]
     */
    private suspend fun fetchPlantsForGrowZone(growZone: GrowZone) {
        val plants = plantService.plantsByGrowZone(growZone)
        plantDao.insertAll(plants)
    }

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: PlantRepository? = null

        fun getInstance(plantDao: PlantDao, plantService: NetworkService) =
            instance ?: synchronized(this) {
                instance ?: PlantRepository(plantDao, plantService).also { instance = it }
            }
    }
}
