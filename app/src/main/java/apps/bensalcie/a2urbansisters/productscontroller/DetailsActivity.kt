package apps.bensalcie.a2urbansisters.productscontroller

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import apps.bensalcie.a2urbansisters.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_details.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DetailsActivity : AppCompatActivity() {
    private  var mExtenstionsDatabase: DatabaseReference?=null

    private var auth : FirebaseAuth?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)


        val name =intent.getStringExtra("name")
        val image =intent.getStringExtra("image")
        val desc =intent.getStringExtra("desc")
        val time =intent.getStringExtra("time")
        val price =intent.getStringExtra("price")
        val pid =intent.getStringExtra("pid")

        auth= FirebaseAuth.getInstance()
        mExtenstionsDatabase= FirebaseDatabase.getInstance().reference.child("URBANSISTERS").child("orders")

        tvTitle.text=name
        tvBody.text=desc
        val footertext ="Date : $time \n\nProduct Code:  $pid"
        tvFooter.text=footertext
        if (!image.isNullOrBlank()){
            iv_one.visibility = View.GONE
            Picasso.get().load(image).into(iv_one)
            iv_one.visibility = View.VISIBLE
        }

        val prices="Ksh. $price"
        tvPrice.text=prices
        btn_hire_me.setOnClickListener {

            btn_hire_me.text=getString(R.string.loading)
            val dialog= AlertDialog.Builder(this)
            dialog.setTitle("Order Item")
            dialog.setMessage("You are about to order $name\n\n Sure about this?")

            dialog.setPositiveButton("Accept") { _, _ ->
               orderItems(name,image,desc,price,pid,it)

            }
            dialog.setNegativeButton("Decline", null)
            dialog.show()
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun orderItems(
        name: String?,
        image: String?,
        desc: String?,
        price: String?,
        pid: String?,
        it: View
    ) {
        var qty="1"

        val q=tvQty.text.toString()
        if (q.isNotEmpty()){
            qty=q
        }
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val productId = mExtenstionsDatabase!!.push().key

                val map = HashMap<String, Any>()
                map["name"] = name!!
                map["description"] = desc!!
                map["image"] = image!!
                map["time"] = currentDate
                map["amount"] = price!!
                map["by"] = auth!!.currentUser!!.uid
                map["pid"] = pid!!
                map["userid"] = auth!!.uid!!
                map["quantity"] = qty
                map["isdelivered"] = "0"
        mExtenstionsDatabase!!.child(productId!!).updateChildren(map)
                    .addOnCompleteListener { task1 ->
                        if (task1.isSuccessful) {
                            Snackbar.make(it, "Order Placed Successfully", Snackbar.LENGTH_LONG)
                                .setAction("Accept", null).show()
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Order", "createUserWithEmail:success")
                            finish()
                        } else {
                            Log.w("Order", "failure : ${task1.exception}", task1.exception)
                            Toast.makeText(
                                this, "Order failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
    }
}