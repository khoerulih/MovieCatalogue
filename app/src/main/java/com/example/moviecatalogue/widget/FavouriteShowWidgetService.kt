package com.example.moviecatalogue.widget

import android.content.Intent
import android.widget.RemoteViewsService

class FavouriteShowWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        FavouriteShowRemoteViewsFactory(this.applicationContext)
}
