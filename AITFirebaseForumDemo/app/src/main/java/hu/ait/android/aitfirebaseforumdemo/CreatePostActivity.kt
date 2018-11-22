package hu.ait.android.aitfirebaseforumdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hu.ait.android.aitfirebaseforumdemo.data.Post
import kotlinx.android.synthetic.main.activity_create_post.*

class CreatePostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        btnSend.setOnClickListener {
            uploadPost()
        }
    }

    private fun uploadPost() {
        val post = Post(
            FirebaseAuth.getInstance().currentUser!!.uid,
            FirebaseAuth.getInstance().currentUser!!.displayName!!,
            etTitle.text.toString(),
            etBody.text.toString(),
            ""
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
}
