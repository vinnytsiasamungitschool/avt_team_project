package com.company.archapp

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState

@Suppress("DEPRECATION")
class ResultActivity : AppCompatActivity() {

    private val slidingPanelLayout by lazy { findViewById<SlidingUpPanelLayout>(R.id.sliding_panel)!! }
    private val landmarkIv by lazy { findViewById<ImageView>(R.id.landmark_iv) }
    private val dragview by lazy { findViewById<LinearLayout>(R.id.dragview) }
    private val landmarkTv by lazy { findViewById<TextView>(R.id.landmark_tv) }
    private val resultPb by lazy { findViewById<ProgressBar>(R.id.result_pb) }

    private var nameOfLandmark: String? = null // Name of recognized landmark

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Get image from intent
        val intent = intent
        val imageUri = intent.getParcelableExtra<Uri>(WelcomeActivity.IMAGE_URI)

        // Analyze our image
        analyzeImage(MediaStore.Images.Media.getBitmap(contentResolver, imageUri))

        slidingPanelLayout.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                // Nothing to do here
            }

            override fun onPanelStateChanged(
                panel: View,
                previousState: PanelState,
                newState: PanelState
            ) {
                // When sliding panel transform to EXPANDED state, we remove radius from background
                if (newState == PanelState.EXPANDED) {
                    dragview.background = getDrawable(R.color.sliding_panel_color)
                } else {
                    // Otherwise we set to sliding panel radius
                    dragview.background = getDrawable(R.drawable.sliding_panel_radius)
                }
            }
        })
    }

    private fun analyzeImage(image: Bitmap?) {
        if (image == null) {
            // If no image we show Toast about error
            Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
            return
        }

        // Delete an image from the screen and show the progress Bar
        landmarkIv.setImageBitmap(null)
        showProgress()

        // Preparation for processing image
        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(image)
        val options = FirebaseVisionCloudDetectorOptions.Builder()
            .setMaxResults(5)
            .build()
        val landmarkDetector = FirebaseVision.getInstance().getVisionCloudLandmarkDetector(options)

        // Detect the image
        landmarkDetector.detectInImage(firebaseVisionImage)
            .addOnSuccessListener {
                // We convert the image into a bitmap image in order to display the image on the screen
                val mutableImage = image.copy(Bitmap.Config.ARGB_8888, true)

                // Recognize landmarks
                recognizeLandmarks(it, mutableImage)

                // Set our image, hide the ProgressBar and show the recognized landmark
                landmarkIv.setImageBitmap(mutableImage)
                hideProgress()
                landmarkTv.text = nameOfLandmark
            }
            .addOnFailureListener {
                // If we got error show a Toast about error
                Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
                hideProgress()
            }
    }



    private fun recognizeLandmarks(landmarks: List<FirebaseVisionCloudLandmark>?, image: Bitmap?) {
        if (landmarks == null || image == null) {
            // If no image or no landmarks show a Toast about error
            Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show()
            return
        }

        // Get landmarks
        for (landmark in landmarks) {
            nameOfLandmark = landmark.landmark
        }
    }

    private fun showProgress() {
        /** Show progressbar */
        slidingPanelLayout.visibility = View.GONE
        resultPb.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        /** Hide progressbar */
        slidingPanelLayout.visibility = View.VISIBLE
        resultPb.visibility = View.GONE
    }
}