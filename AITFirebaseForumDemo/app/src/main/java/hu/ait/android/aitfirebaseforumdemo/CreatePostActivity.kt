package hu.ait.android.aitfirebaseforumdemo

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import hu.ait.android.aitfirebaseforumdemo.data.Post
import kotlinx.android.synthetic.main.activity_create_post.*
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.util.*

class CreatePostActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
        private const val CAMERA_REQUEST_CODE = 102
    }
    var uploadBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        btnSend.setOnClickListener {
            if (uploadBitmap != null) {
                uploadPostWithImage()
            } else {
                uploadPost()
            }
        }

        btnAttach.setOnClickListener {
            startActivityForResult(
                Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                CAMERA_REQUEST_CODE)
        }


        btnAttach.isEnabled = false
        requestNeededPermission()
    }

    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {
                Toast.makeText(this,
                    "I need it for camera", Toast.LENGTH_SHORT).show()
            }

            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.CAMERA),
                1001)
        } else {
            // we already have this permission
            btnAttach.isEnabled = true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            1001 -> {
                if (grantResults.isNotEmpty() && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "CAMERA perm granted", Toast.LENGTH_SHORT).show()
                    btnAttach.isEnabled = true
                } else {
                    Toast.makeText(this, "CAMERA perm NOT granted", Toast.LENGTH_SHORT).show()
                    btnAttach.isEnabled = false
                }
            }
        }
    }


    private fun uploadPost(imgUrl: String = "") {
        val post = Post(
            FirebaseAuth.getInstance().currentUser!!.uid,
            FirebaseAuth.getInstance().currentUser!!.displayName!!,
            etTitle.text.toString(),
            etBody.text.toString(),
            imgUrl
        )

        val postsCollections = FirebaseFirestore.getInstance().collection("posts")

        postsCollections.add(post)
            .addOnSuccessListener {
                Toast.makeText(this@CreatePostActivity, "Post saved",
                    Toast.LENGTH_LONG).show()
            }.addOnFailureListener{
                Toast.makeText(this@CreatePostActivity, "Error ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            data?.let {
                uploadBitmap = it.extras.get("data") as Bitmap
                imgAttach.setImageBitmap(uploadBitmap)
                imgAttach.visibility = View.VISIBLE
            }
        }
    }

    @Throws(Exception::class)
    private fun uploadPostWithImage() {
        val baos = ByteArrayOutputStream()
        uploadBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageInBytes = baos.toByteArray()

        val storageRef = FirebaseStorage.getInstance().getReference()
        val newImage = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8") + ".jpg"
        val newImagesRef = storageRef.child("images/$newImage")

        newImagesRef.putBytes(imageInBytes)
            .addOnFailureListener { exception ->
                Toast.makeText(this@CreatePostActivity, exception.message, Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                newImagesRef.downloadUrl.addOnCompleteListener(object: OnCompleteListener<Uri> {
                    override fun onComplete(task: Task<Uri>) {
                        uploadPost(task.result.toString())
                    }
                })
            }
    }

}
