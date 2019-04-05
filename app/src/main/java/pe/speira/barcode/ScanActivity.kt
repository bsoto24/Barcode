package pe.speira.barcode

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_scan.*

class ScanActivity : AppCompatActivity() {

    private val TAG = "ScanActivity"
    private lateinit var detector: BarcodeDetector
    private lateinit var camera: CameraSource

    private val CAMERA_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        detector = BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        detector.setProcessor(object : Detector.Processor<Barcode> {

            override fun release() {
                Log.e(TAG, "Detector: release")
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                Log.e(TAG, "Detector: receiveDetections")
                val barcodes = detections?.detectedItems
                if (barcodes!!.size() != 0){
                    Log.e(TAG, "Barcode: ${barcodes.valueAt(0).displayValue}")

                    val intent = Intent(this@ScanActivity, ResultActivity::class.java)
                    intent.putExtra("BARCODE", barcodes.valueAt(0).displayValue)
                    this@ScanActivity.startActivity(intent)
                    this@ScanActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                    detector.release()

                }

            }

        })

        camera = CameraSource.Builder(this, detector).setRequestedPreviewSize(1024, 768).setRequestedFps(25f).setAutoFocusEnabled(true).build()
        svBarcode.holder.addCallback(object : SurfaceHolder.Callback2 {
            override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {
                Log.e(TAG, "Camera: surfaceRedrawNeeded")
            }

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                Log.e(TAG, "Camera: surfaceChanged")

            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                Log.e(TAG, "Camera: surfaceDestroyed")
                camera.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                Log.e(TAG, "Camera: surfaceCreated")

                if(ContextCompat.checkSelfPermission(this@ScanActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    camera.start(svBarcode.holder)
                } else{
                    ActivityCompat.requestPermissions(this@ScanActivity, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
                }

            }

        })

        imgCancel.setOnClickListener {

            finish()
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)

        }

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.e(TAG, "Permision: onRequestPermissionsResult")
        if (requestCode == CAMERA_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                camera.start(svBarcode.holder)
            } else {
                Toast.makeText(this, "Scanner won't work without permission", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
