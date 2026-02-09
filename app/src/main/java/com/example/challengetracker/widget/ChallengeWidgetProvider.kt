package com.example.challengetracker.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.challengetracker.MainActivity
import com.example.challengetracker.R

class ChallengeWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget_challenges)
            val serviceIntent = Intent(context, ChallengeWidgetService::class.java)
            views.setRemoteAdapter(R.id.widget_list, serviceIntent)
            views.setEmptyView(R.id.widget_list, R.id.widget_empty)

            val templateIntent = Intent(context, MainActivity::class.java).apply {
                action = WidgetConstants.ACTION_CHECK_IN
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                templateIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setPendingIntentTemplate(R.id.widget_list, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
