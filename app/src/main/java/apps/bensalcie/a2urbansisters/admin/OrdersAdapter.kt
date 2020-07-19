package apps.bensalcie.a2urbansisters.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import apps.bensalcie.a2urbansisters.R
import apps.bensalcie.a2urbansisters.productscontroller.DetailsActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso


class OrdersAdapter constructor(options: FirebaseRecyclerOptions<Order>) :
    FirebaseRecyclerAdapter<Order, OrdersAdapter.ArticleViewModel>(options) {

    class ArticleViewModel internal constructor(private var view: View) : RecyclerView.ViewHolder(view) {

        internal fun setProductName(
            productName: Order,
             holder: ArticleViewModel
        ) {


             val kbvLocation: ImageView = itemView.findViewById(R.id.kbvLocation)
             val textTitle: TextView = itemView.findViewById(R.id.textTitle)
             val textLocation: TextView = itemView.findViewById(R.id.textLocation)
             val textStarRating: TextView = itemView.findViewById(R.id.textStarRating)
            Picasso.get().load(productName.image).into(kbvLocation)

            textTitle.text = productName.name
            if (productName.isdelivered=="1"){
                textLocation.text = "Status: Delivered"

            }else{
                textLocation.text = "Status: Pending"

            }



            val price ="Ksh.${productName.amount}"
            textStarRating.text = price

            holder.itemView.setOnClickListener {
                val dialog= AlertDialog.Builder(itemView.context)
                dialog.setTitle("Order Item")
                dialog.setMessage("You are about to mark  ${productName.name} as Delivered\n\n Sure about this?")

                dialog.setPositiveButton("Accept") { _, _ ->
                    markAsDelivered(productName)

                }
                dialog.setNegativeButton("Decline", null)
                dialog.show()

            }

        }

        private fun markAsDelivered(productName: Order) {
            val ref=FirebaseDatabase.getInstance().reference.child("URBANSISTERS").child("orders").child(productName.userid!!)
            ref.child("isdelivered").setValue("1")
            Toast.makeText(itemView.context, "Marked as Delivered", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewModel {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ArticleViewModel(view)
    }


    override fun onBindViewHolder(holder: ArticleViewModel, position: Int, model: Order) {
        holder.setProductName(model,holder)
    }


}