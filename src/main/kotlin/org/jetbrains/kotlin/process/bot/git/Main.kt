@file:JvmName("Main")

package org.jetbrains.kotlin.process.bot.git

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import org.jetbrains.kotlin.process.botutil.errorMessage
import org.jetbrains.kotlin.process.plugin.actions.merge.MergeAction
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory

const val SERVICE_NAME = "git merge branch"
const val schedule = "0/10 * * * * ?" //TODO: change cron

fun main() {
    try {
        val scheduler = StdSchedulerFactory.getDefaultScheduler()
        scheduler.start()

        val job = JobBuilder
            .newJob(IssuesJob::class.java)
            .build()

        val trigger = TriggerBuilder.newTrigger()
            .withSchedule(CronScheduleBuilder.cronSchedule(schedule))
            .startNow()
            .build()

        println("is started: ${scheduler.isStarted}")
        println("name: ${scheduler.schedulerName}")

        scheduler.scheduleJob(job, trigger)
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

class IssuesJob : Job {
    override fun execute(context: JobExecutionContext?) {
        try {
            Notifications.Bus.notify(
                Notification(
                    "Kotlin Process", "Success",
                    "You can merge!", NotificationType.INFORMATION
                ).addAction(MergeAction()).setImportant(true)
            )
        } catch (e: Throwable) {
            val errorMessage = e.errorMessage(SERVICE_NAME)
            Notifications.Bus.notify(
                Notification(
                    "Kotlin Process", "Error",
                    errorMessage, NotificationType.ERROR
                )
            )
        }
    }
}