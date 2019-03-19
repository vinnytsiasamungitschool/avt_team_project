package com.company.archapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Передаёт изображение в битмапу

        val bmp = BitmapFactory.decodeResource(resources, R.drawable.blagovesh_1)
        val image = VisionImage.imageFromBitmap(bmp)
        LandmarkRecognitionActivity.recognizeLandmarksCloud(image!!)

        /** Весь код что ниже не входит до основного проекта это просто проверка или расспознавание работает,
         * вы можете его удалять или редактировать
         **/

        //Находим кнопку и добавлем ей ей действия

        val bu: Button = findViewById(R.id.buttonPanel)
        bu.setOnClickListener(View.OnClickListener {
            val tv: TextView = findViewById(R.id.landmarkNameone)
            tv.text = LandmarkRecognitionActivity.nameOfLandmark
        })
    }
}









