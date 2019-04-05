package pe.speira.barcode

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cvScan.setOnClickListener {

            startActivity(Intent(this@MainActivity, ScanActivity::class.java))
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)

        }
    }
}
