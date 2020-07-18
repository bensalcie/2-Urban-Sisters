package apps.bensalcie.a2urbansisters.productscontroller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import apps.bensalcie.a2urbansisters.MainActivity
import apps.bensalcie.a2urbansisters.R
import apps.bensalcie.a2urbansisters.admin.Product
import apps.bensalcie.a2urbansisters.admin.ProductsAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : AppCompatActivity() {
    private var recentRecyclerView : RecyclerView?=null
    private var adapter: ProductsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        recentRecyclerView = findViewById(R.id.recentRecycler)
        recentRecyclerView!!.layoutManager = GridLayoutManager(this,2)
        val query=FirebaseDatabase.getInstance().reference.child("URBANSISTERS").child("extensions")

        val options = FirebaseRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).build()
        adapter = ProductsAdapter(options)
        recentRecyclerView!!.adapter = adapter
    }
    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()

        if (adapter != null) {
            adapter!!.stopListening()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_orders,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.menu_logout->{
                sendToMyOrders()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendToMyOrders() {
        val intent = Intent(this, MyOrdersActivity::class.java)
        startActivity(intent)
    }

}