package com.sebas.snapchat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.util.concurrent.ExecutionException

class ViewSnapActivity : AppCompatActivity() {
    val mAuth = FirebaseAuth.getInstance()
    var messageTextView: TextView? = null
    var snapImageView: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snap)

        messageTextView = findViewById(R.id.snapTextView)
        snapImageView = findViewById(R.id.snapImageView)
        messageTextView?.text = intent.getStringExtra("message")

        val task = ImageDownloader()
        val myImage: Bitmap
        try {
            myImage = task.execute(intent.getStringExtra("imageURL")).get()
            snapImageView?.setImageBitmap(myImage)
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        //remove snap after back press
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser?.uid).child("snaps").child(intent.getStringExtra("snapKey")).removeValue()
        FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("imageName")).delete()
    }

    inner class ImageDownloader : AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            try {
                val url = URL(urls[0])
                val urlConnection = url.openConnection() as URLConnection
                urlConnection.connect()

                val `in` = urlConnection.getInputStream()

                return BitmapFactory.decodeStream(`in`)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }
    };

}
