package ua.com.hse.notifyhseq;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatDelegate;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class NotifyAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 2, mainIntent, 0);


        Intent notifyIntent = new Intent(context, NotifyNewActivity.class);
        notifyIntent.setAction("notifyButton");
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, 0);

        Intent photoIntent = new Intent(context, NotifyNewActivity.class);
        photoIntent.setAction("photoButton");
        PendingIntent photoPendingIntent = PendingIntent.getActivity(context, 1, photoIntent, 0);

        views.setOnClickPendingIntent(R.id.widgetBtnMain, mainPendingIntent);
        views.setOnClickPendingIntent(R.id.widgetBtnNotify, notifyPendingIntent);
        views.setOnClickPendingIntent(R.id.widgetBtnPhoto, photoPendingIntent);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

