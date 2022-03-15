package com.example.moviecatalogue.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationItems(
    var title: String? = null
): Parcelable