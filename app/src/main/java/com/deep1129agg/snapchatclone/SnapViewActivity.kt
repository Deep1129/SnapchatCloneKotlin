package com.deep1129agg.snapchatclone

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.deep1129agg.snapchatclone.R.id.snappImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class SnapViewActivity : AppCompatActivity() {

    var messageTextView: TextView? = null
    var snappImageView: ImageView? = null
    var myImage : Bitmap? = null
    val mAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap_view)

        messageTextView = findViewById(R.id.messageTextView)
        snappImageView = findViewById(R.id.snappImageView)

        messageTextView?.text = intent.getStringExtra("message")

         val task = ImageDownloader()
//        myImage = Bitmap
//        var myImage = BitmapFactory.decodeResource(getResources(), R.color.background_floating_material_dark);
        try {
            myImage = task.execute(intent.getStringExtra("imageURL")).get()
            snappImageView?.setImageBitmap(myImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ImageDownloader : AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg urls: String?): Bitmap? {
            try {
                val url = URL(urls[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val input = connection.inputStream
                return BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }
    }

    override fun onBackPressed() {

        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser!!.uid).child("snaps").child(intent.getStringExtra("snapKey")).removeValue()

        FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("imageName")).delete()
        super.onBackPressed()
    }
}
