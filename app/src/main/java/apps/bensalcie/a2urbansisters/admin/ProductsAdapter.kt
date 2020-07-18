package apps.bensalcie.a2urbansisters.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import apps.bensalcie.a2urbansisters.R
import apps.bensalcie.a2urbansisters.productscontroller.DetailsActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.squareup.picasso.Picasso


class ProductsAdapter constructor(options: FirebaseRecyclerOptions<Product>) :
    FirebaseRecyclerAdapter<Product, ProductsAdapter.ArticleViewModel>(options) {

    class ArticleViewModel internal constructor(private var view: View) : RecyclerView.ViewHolder(view) {

        internal fun setProductName(
            productName: Product,
             holder: ArticleViewModel
        ) {


             val kbvLocation: ImageView = itemView.findViewById(R.id.kbvLocation)
             val textTitle: TextView = itemView.findViewById(R.id.textTitle)
             val textLocation: TextView = itemView.findViewById(R.id.textLocation)
             val textStarRating: TextView = itemView.findViewById(R.id.textStarRating)
            Picasso.get().load(productName.image).into(kbvLocation)

            textTitle.text = productName.name

                textLocation.text = productName.description


            val price ="Ksh.${productName.amount}"
            textStarRating.text = price

            holder.itemView.setOnClickListener {
                val detailsIntent= Intent(holder.itemView.context,DetailsActivity::class.java)
                detailsIntent.putExtra("name",productName.name)
                detailsIntent.putExtra("image",productName.image)
                detailsIntent.putExtra("desc",productName.description)
                detailsIntent.putExtra("time",productName.time)
                detailsIntent.putExtra("price",productName.amount)
                detailsIntent.putExtra("pid",productName.pid)
                itemView.context.startActivity(detailsIntent)

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewModel {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ArticleViewModel(view)
    }


    override fun onBindViewHolder(holder: ArticleViewModel, position: Int, model: Product) {
        holder.setProductName(model,holder)
    }


}