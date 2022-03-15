package com.example.moviecatalogue.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DataItems(
    var id: String? = null,
    var title: String? = null,
    var rating: String? = null,
    var release_date: String? = null,
    var overview: String? = null,
    var poster: String? = null,
    var background: String? = null
): Parcelable