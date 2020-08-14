package com.achesnovitskiy.pagedlisttest.ui.cats

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.achesnovitskiy.pagedlisttest.R
import com.achesnovitskiy.pagedlisttest.ui.entities.PresentationCat
import com.squareup.picasso.Callback
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_cat.view.*
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*


class CatsAdapter : ListAdapter<PresentationCat, CatViewHolder>(
    CatsDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder =
        CatViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.item_cat,
                    parent,
                    false
                )
        )

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CatsDiffCallback : DiffUtil.ItemCallback<PresentationCat>() {

    override fun areItemsTheSame(oldCat: PresentationCat, newCat: PresentationCat): Boolean =
        oldCat.id == newCat.id

    override fun areContentsTheSame(oldCat: PresentationCat, newCat: PresentationCat): Boolean =
        oldCat == newCat
}

class CatViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {

    private val itemCatImageView: ImageView = containerView.itemCatImageView

    private val itemCatIdTextView: TextView = containerView.itemCatIdTextView

    fun bind(cat: PresentationCat) {
        Picasso.get()
            .load(cat.image_url)
            .placeholder(R.drawable.ic_time_black_48)
            .error(R.drawable.ic_broken_image_black_48)
            .fit()
            .into(
                itemCatImageView,
                object : Callback {

                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {
                        Log.e("My", "${e}")
                    }
                }
            )

        itemCatIdTextView.text = cat.id
    }
}