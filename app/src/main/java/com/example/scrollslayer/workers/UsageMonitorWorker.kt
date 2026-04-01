package com.example.scrollslayer.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scrollslayer.data.local.database.DatabaseProvider
import com.example.scrollslayer.data.repository.MissionRepository
import com.example.scrollslayer.data.repository.UsageRepository
import com.example.scrollslayer.notifications.NotificationHelper

class UsageMonitorWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = DatabaseProvider.getDatabase(applicationContext)
            val missionRepository = MissionRepository(database.missionDao())
            val usageRepository = UsageRepository(applicationContext)

            val activeMission = missionRepository.getActiveMission()
            val socialUsage = usageRepository.getSocialUsage()
            val totalMinutes = socialUsage.sumOf { it.minutes }

            if (activeMission != null && totalMinutes > 0) {
                val title = "El enemigo ha avanzado"
                val message =
                    "Llevas $totalMinutes min en redes. Recuerda tu misión: ${activeMission.title}"

                NotificationHelper.showMissionReminderNotification(
                    context = applicationContext,
                    title = title,
                    message = message
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}