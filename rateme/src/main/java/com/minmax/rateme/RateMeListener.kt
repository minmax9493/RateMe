package io.youvr.android.pivo.util.rateme

interface RateMeListener {
    fun onPositiveReview(numOfStars:Int)
    fun onNegativeReview(numOfStars: Int)
    fun onLaterClick()
    fun onOkClick()
}