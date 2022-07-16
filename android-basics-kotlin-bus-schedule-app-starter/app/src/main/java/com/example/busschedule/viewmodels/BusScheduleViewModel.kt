package com.example.busschedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.busschedule.database.schedule.Schedule
import com.example.busschedule.database.schedule.ScheduleDao
import kotlinx.coroutines.flow.Flow

// 프래그먼트에서 schedule을 주입할 수 없다. 따라서 생성자에 넣어주고 팩토리 패턴을 이용해 인스턴스화하자
class BusScheduleViewModel(private val scheduleDao: ScheduleDao) : ViewModel() {

    fun fullSchedule(): Flow<List<Schedule>> = scheduleDao.getAll()

    fun scheduleForStopName(name: String): Flow<List<Schedule>> = scheduleDao.getByStopName(name)
}

class BusScheduleViewModelFactory(
    private val scheduleDao: ScheduleDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BusScheduleViewModel(scheduleDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}