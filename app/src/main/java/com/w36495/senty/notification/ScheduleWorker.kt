package com.w36495.senty.notification

import android.content.Context
import android.util.Log
import com.w36495.senty.domain.repository.AnniversaryRepository
import com.w36495.senty.util.DateUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

//@HiltWorker
//class ScheduleWorker @AssistedInject constructor(
//    @Assisted private val context: Context,
//    @Assisted workerParams: WorkerParameters,
//    private val anniversaryRepository: AnniversaryRepository,
//) : CoroutineWorker(context, workerParams) {
//
//    override suspend fun doWork(): Result {
//        return try {
//            val result = anniversaryRepository.getSchedules().first().run {
//                filter { DateUtil.calRemainDate(it.date) == 1}
//            }
//
//            Log.d("ScheduleWorker", result.toString())
//
//
//
//            Result.success()
//        } catch (exception: Exception) {
//            Result.failure()
//        }
//    }
//
//    private fun registerPeriodWorker() {
//        val workRequest = PeriodicWorkRequestBuilder<ScheduleWorker>(1, TimeUnit.DAYS)
//            .setInitialDelay(9, TimeUnit.HOURS) // 오전 9시에 실행
//            .build()
//
//        // 작업을 실행하도록 지시 (큐에 추가)
//        WorkManager.getInstance(context)
//            .enqueueUniquePeriodicWork(
//                SCHEDULE_PERIOD_WORKER_NAME,
//                ExistingPeriodicWorkPolicy.UPDATE,
//                workRequest
//            )
//    }
//
//    companion object {
//        private const val SCHEDULE_PERIOD_WORKER_NAME: String = "senty_schedule_period_worker"
//    }
//}