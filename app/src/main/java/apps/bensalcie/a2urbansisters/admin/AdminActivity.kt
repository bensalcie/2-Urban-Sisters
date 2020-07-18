package apps.bensalcie.a2urbansisters.admin

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import apps.bensalcie.a2urbansisters.MainActivity
import apps.bensalcie.a2urbansisters.R
import apps.bensalcie.a2urbansisters.productscontroller.HomeActivity
import apps.bensalcie.a2urbansisters.productscontroller.MyOrdersActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.content_admin.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class AdminActivity : AppCompatActivity() {
    private  val GALLERY_REQUEST_CODE: Int=4563
    private  var mOrderssDatabase: DatabaseReference?=null


    private var auth :FirebaseAuth?=null
    private  var mExtenstionsDatabase: DatabaseReference?=null
    private var imageUri:Uri?=null
    private var imageView:ImageView?=null
    private var  extensionStorage : StorageReference?=null
    private var uploadTask:UploadTask?=null
    private var dati:ByteArray?=null
    private var instock=0
    private var orders=0
    private var totalProducts=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        setSupportActionBar(findViewById(R.id.toolbar))
        auth= FirebaseAuth.getInstance()
        mExtenstionsDatabase=FirebaseDatabase.getInstance().reference.child("URBANSISTERS").child("extensions")
        extensionStorage=FirebaseStorage.getInstance().reference.child("URBANSISTERS").child("images")
        mOrderssDatabase= FirebaseDatabase.getInstance().reference.child("URBANSISTERS").child("orders")

        fab.setOnClickListener {
            showUploadDialog()
        }
        initAllMethods()

    }

    private fun initAllMethods() {
        loadProductsCount()
        loadOrdersCount()
        instock =totalProducts-orders
        if (instock>0) {
            tvInstock.text = instock.toString()
        }
        loadDelivered()
    }

    private fun loadDelivered() {


        val query: Query = mOrderssDatabase!!.orderByChild("isdelivered").equalTo("1")

        query.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                tvDelivered.text=snapshot.childrenCount.toString()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
//        mOrderssDatabase?.addValueEventListener(object :ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//                val count = snapshot.childrenCount
//                tvOrders.text=count.toString()
//                orders=count.toInt()
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//                Toast.makeText(this@AdminActivity, "Something went wrong...", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        })
//
//
//        val removedListener =
//            query.addChildEventListener(object : ChildEventListener {
//                override fun onChildAdded(
//                    @NonNull dataSnapshot: DataSnapshot,
//                    @Nullable s: String?
//                ) {
//                    if (dataSnapshot.exists()) {
//                        val count = dataSnapshot.childrenCount
//
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//
//                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                }
//
//                override fun onChildChanged(
//                    @NonNull dataSnapshot: DataSnapshot,
//                    @Nullable s: String?
//                ) {
//                    if (dataSnapshot.exists()) {
//                        val count = dataSnapshot.childrenCount
//                    }
//                }
//
//                override fun onChildRemoved(@NonNull dataSnapshot: DataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        val count = dataSnapshot.childrenCount
//                    }
//                }
//
//            }



    }

    private fun loadOrdersCount() {
        mOrderssDatabase?.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val count = snapshot.childrenCount
                tvOrders.text=count.toString()
                orders=count.toInt()
                dashboardItemHolder.setOnClickListener {
                    sendToOrders()
                }

            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(this@AdminActivity, "Something went wrong...", Toast.LENGTH_SHORT)
                    .show()
            }
        })


    }

    private fun sendToOrders() {
        val intent = Intent(this, MyOrdersActivity::class.java)
        startActivity(intent)
    }

    private fun loadProductsCount() {
        mExtenstionsDatabase?.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val count = snapshot.childrenCount
                tvSumProducts.text=count.toString()
                totalProducts= count.toInt()

            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(this@AdminActivity, "Something went wrong...", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        productsHolder.setOnClickListener {
            sendToProducts()
        }

    }

    private fun sendToProducts() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun showUploadDialog() {
        val dialog = Dialog(
            this,
            R.style.ThemeOverlay_MaterialComponents
        )
        val window: Window? = dialog.window
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
        dialog.setTitle(null)
        dialog.setContentView(R.layout.pop_send_dialog)
        dialog.setCancelable(true)
        dialog.show()
        val etAmount = dialog.findViewById<EditText>(R.id.etAmount)
        val btnUpload = dialog.findViewById<Button>(R.id.btnUpload)
         imageView = dialog.findViewById(R.id.imageView)
        val etDescription = dialog.findViewById<EditText>(R.id.etDescription)
        val etTitle = dialog.findViewById<EditText>(R.id.etTitle)

        val progressBar = dialog.findViewById<ProgressBar>(R.id.progressBar)
        imageView?.setOnClickListener {
            val galleryIntent =Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent,GALLERY_REQUEST_CODE)
        }

        btnUpload.setOnClickListener {

            val amount = etAmount.text.toString()
            val desc = etDescription.text.toString()
            val title = etTitle.text.toString()

            if (amount.isNotEmpty() && desc.isNotEmpty() && title.isNotBlank())
            {
               if (dati!=null){
                   uploadProduct(amount,desc,title,progressBar, dati!!,dialog,it)
               }else{
                   Toast.makeText(this, "Please add image to your product...", Toast.LENGTH_SHORT)
                       .show()
               }
            }else{
                Toast.makeText(this, "Check input fields...", Toast.LENGTH_SHORT).show()
            }




        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun uploadProduct(
        amount: String,
        desc: String,
        title: String,
        progressBar: ProgressBar,
        dati: ByteArray,
        dialog: Dialog,
        it: View
    ) {
        progressBar.visibility=View.VISIBLE
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())


        val ref = extensionStorage!!.child("${UUID.randomUUID()}.jpg")
        uploadTask = ref.putBytes(dati)
        uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result

                val map = HashMap<String, Any>()
                val productId = mExtenstionsDatabase!!.push().key

                map["name"] = title
                map["description"] = desc
                map["image"] = downloadUri.toString()
                map["time"] = currentDate
                map["amount"] = amount
                map["by"] = auth!!.currentUser!!.uid
                map["pid"] = productId!!

                mExtenstionsDatabase!!.child(productId).updateChildren(map)
                    .addOnCompleteListener { task1 ->
                        if (task1.isSuccessful) {
                            progressBar.visibility = View.GONE
                            dialog.dismiss()
                            Snackbar.make(it, "Uploaded Successfully", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Upload", "createUserWithEmail:success")

                        } else {
                            progressBar.visibility = View.GONE
                            dialog.dismiss()
                            // If sign in fails, display a message to the user.
                            Log.w("Upload", "failure : ${task1.exception}", task.exception)
                            Toast.makeText(
                                this, "Upload failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }
            }else{
                dialog.dismiss()
                Toast.makeText(this, "Image Task Failed: ${task.exception}", Toast.LENGTH_SHORT).show()

            }

             }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==GALLERY_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            if (data != null) {
                imageUri=data.data
                imageView?.setImageURI(imageUri)
                //ne
                imageView!!.isDrawingCacheEnabled   = true
                imageView!!.buildDrawingCache()
                val bitmap = (imageView!!.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                 dati = baos.toByteArray()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.menu_logout->{
                auth?.signOut()
                sendToHome()

            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun sendToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}