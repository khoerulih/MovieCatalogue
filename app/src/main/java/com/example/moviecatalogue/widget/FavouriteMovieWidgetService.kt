package com.example.moviecatalogue.widget

import android.content.Intent
import android.widget.RemoteViewsService

class FavouriteMovieWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        FavouriteMovieRemoteViewsFactory(this.applicationContext)

}
