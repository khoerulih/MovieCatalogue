package com.example.moviecatalogue.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri
import com.example.moviecatalogue.R

class FavouriteShowWidget : AppWidgetProvider() {
    companion object {
        private const val TOAST_ACTION = "com.example.moviecatalogue.widget.TOAST_ACTION"
        const val EXTRA_ITEM = "com.example.moviecatalogue.widget.EXTRA_ITEM"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, FavouriteShowWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

            val views = RemoteViews(context.packageName, R.layout.favourite_show_widget)
            views.setRemoteAdapter(R.id.stack_show_view, intent)
            views.setEmptyView(R.id.stack_show_view, R.id.empty_show_view)

            val toastIntent = Intent(context, FavouriteShowWidget::class.java)
            toastIntent.action = TOAST_ACTION
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

            val toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.stack_show_view, toastPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

    }
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val thisWidget = ComponentName(context, FavouriteShowWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack_show_view)

        if(intent.action != null){
            if (intent.action == TOAST_ACTION){
                val showTitle = intent.getStringExtra(EXTRA_ITEM)
                Toast.makeText(context, showTitle, Toast.LENGTH_SHORT).show()
            }
        }
    }
}