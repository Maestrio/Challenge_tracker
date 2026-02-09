package com.example.challengetracker.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.room.Room
import com.example.challengetracker.R
import com.example.challengetracker.data.AppDatabase
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ChallengeWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ChallengeWidgetFactory(applicationContext)
    }
}

private class ChallengeWidgetFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {
    private var challenges: List<WidgetChallenge> = emptyList()

    override fun onCreate() = Unit

    override fun onDataSetChanged() {
        val database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "challenge-tracker.db"
        ).build()
        val activeChallenges = runBlocking {
            database.challengeDao().getActiveChallenges()
        }
        challenges = activeChallenges.map {
            val startDate = LocalDate.parse(it.challenge.startDate)
            val today = LocalDate.now()
            val dayCount = when {
                today.isBefore(startDate) -> 0
                today.isAfter(startDate.plusDays(it.challenge.durationDays.toLong() - 1)) -> it.challenge.durationDays
                else -> ChronoUnit.DAYS.between(startDate, today).toInt() + 1
            }
            WidgetChallenge(
                id = it.challenge.id,
                name = it.challenge.name,
                subtitle = "Day $dayCount of ${it.challenge.durationDays}"
            )
        }
    }

    override fun onDestroy() {
        challenges = emptyList()
    }

    override fun getCount(): Int = challenges.size

    override fun getViewAt(position: Int): RemoteViews {
        val item = challenges[position]
        val views = RemoteViews(context.packageName, R.layout.widget_challenge_item)
        views.setTextViewText(R.id.widget_challenge_name, item.name)
        views.setTextViewText(R.id.widget_challenge_subtitle, item.subtitle)
        val fillInIntent = Intent().apply {
            action = WidgetConstants.ACTION_CHECK_IN
            putExtra(WidgetConstants.EXTRA_CHALLENGE_ID, item.id)
        }
        views.setOnClickFillInIntent(R.id.widget_challenge_name, fillInIntent)
        views.setOnClickFillInIntent(R.id.widget_challenge_subtitle, fillInIntent)
        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = challenges[position].id

    override fun hasStableIds(): Boolean = true
}

private data class WidgetChallenge(
    val id: Long,
    val name: String,
    val subtitle: String
)
