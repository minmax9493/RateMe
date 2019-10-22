package com.minmax.rateme_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import io.youvr.android.pivo.util.rateme.RateMeDialog
import io.youvr.android.pivo.util.rateme.RateMeListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.rate_me_button).setOnClickListener {
            RateMeDialog
                .getInstance(this, "example@example.com")
                .setListener(object : RateMeListener {
                    override fun onPositiveReview(numOfStars: Int) {
                        Log.e(javaClass.simpleName, "number Of stars: $numOfStars")
                    }

                    override fun onNegativeReview(numOfStars: Int) {
                        Log.e(javaClass.simpleName, "number of stars: $numOfStars")
                    }

                    override fun onLaterClick() {
                    }

                    override fun onOkClick() {
                    }
                })
                .show()
        }
    }
}
