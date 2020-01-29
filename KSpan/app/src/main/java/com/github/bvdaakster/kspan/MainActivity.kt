package com.github.bvdaakster.kspan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sampleTextView.text = kspan(R.array.kspan_sample) {
            foregroundColor(R.color.colorPrimary, 1, 3)
            backgroundColor(R.color.colorAccent, 4)
            sampleTextView.click(0) {
                Toast.makeText(this@MainActivity, "Hello", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
