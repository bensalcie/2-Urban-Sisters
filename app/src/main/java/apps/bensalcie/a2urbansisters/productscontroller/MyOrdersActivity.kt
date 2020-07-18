package apps.bensalcie.a2urbansisters.productscontroller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import apps.bensalcie.a2urbansisters.R
import apps.bensalcie.a2urbansisters.admin.Order
import apps.bensalcie.a2urbansisters.admin.OrdersAdapter
import apps.bensalcie.a2urbansisters.admin.Product
import apps.bensalcie.a2urbansisters.admin.ProductsAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

class MyOrdersActivity : AppCompatActivity() {
    private var recentRecyclerView : RecyclerView?=null
    private var adapter: OrdersAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)
        recentRecyclerView = findViewById(R.id.recentRecycler)
        recentRecyclerView!!.layoutManager = GridLayoutManager(this,2)
        val query= FirebaseDatabase.getInstance().reference.child("URBANSISTERS").child("orders")
        val options = FirebaseRecyclerOptions.Builder<Order>().setQuery(query, Order::class.java).build()
        adapter = OrdersAdapter(options)
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
}