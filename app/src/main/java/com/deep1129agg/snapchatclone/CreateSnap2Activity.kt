package com.deep1129agg.snapchatclone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*

class CreateSnap2Activity : AppCompatActivity() {

    var createSnapImageView : ImageView? = null
    var messageEditText : EditText? = null
    val imageName = UUID.randomUUID().toString() + ".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap2)

        createSnapImageView = findViewById(R.id.createSnapImageView)
        messageEditText = findViewById(R.id.messageEditText)
    }

    fun getphoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    fun chooseImageClicked(view : View){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            getphoto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data!!.data

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                createSnapImageView?.setImageBitmap(bitmap)


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            { getphoto() }
        }
    }

    fun nextClicked(view: View){
        // Get the data from an ImageView as bytes
        createSnapImageView?.isDrawingCacheEnabled = true
        createSnapImageView?.buildDrawingCache()
        val bitmap = (createSnapImageView?.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

      /*  val uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this,"upload failed",Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
             val downloadUrl = taskSnapshot.uploadSessionUri
             Log.i("Url", downloadUrl.toString())

            /* var url: String? = null
             val downloadUri = taskSnapshot.getMetadata()?.getReference()?.getDownloadUrl()
             downloadUri?.addOnCompleteListener { exception ->
                 if (uploadTask.isSuccessful) {
                     // Task completed successfully
                     val result = uploadTask.result
                     url = downloadUri?.result.toString()
                     Log.i("Url", url)
                 } else {
                     // Task failed with an exception
                     val exception = uploadTask.exception
                 }

             }*/


            val intent = Intent(this,ChooseUserActivity::class.java)
            intent.putExtra("imageUrl",downloadUrl.toString())
            intent.putExtra("imageName",imageName)
            intent.putExtra("message",messageEditText?.text.toString())


            startActivity(intent)
        }*/
        var url: String? = null
        val ref = FirebaseStorage.getInstance().getReference().child("images").child(imageName)
        var uploadTask = ref.putBytes(data)

        val urlTask = uploadTask.continueWithTask(   Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    Toast.makeText(this , "Upload failed" , Toast.LENGTH_SHORT).show()
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                url = task.result.toString()
                Log.i("url",url)

                 val intent = Intent(this , ChooseUserActivity::class.java)
                 intent.putExtra("imageURL" ,url)
                 intent.putExtra("imageName" ,imageName)
                 intent.putExtra("message" ,messageEditText?.text.toString())
                 startActivity(intent)
            } else {
                // Handle failures
                // ...
            }
        }
    }
}
