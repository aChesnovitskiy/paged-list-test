package com.achesnovitskiy.pagedlisttest.ui.cats

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
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_cat.view.*

class CatsAdapter : ListAdapter<PresentationCat, RecyclerView.ViewHolder>(
    CatsDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_CAT -> CatViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(
                        R.layout.item_cat,
                        parent,
                        false
                    )
            )

            TYPE_LOADER -> ServiceViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(
                        R.layout.item_loader,
                        parent,
                        false
                    )
            )

            else -> ServiceViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(
                        R.layout.item_error,
                        parent,
                        false
                    )
            )
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CatViewHolder) {
            holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int =
        when {
            getItem(position).isLoader -> TYPE_LOADER
            getItem(position).isError -> TYPE_ERROR
            else -> TYPE_CAT
        }

    companion object {
        const val TYPE_CAT = 1
        const val TYPE_LOADER = 2
        const val TYPE_ERROR = 3
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
            .error(R.drawable.ic_broken_image_black_48)
            .resize(200, 200)
            .centerCrop()
            .into(itemCatImageView)

        itemCatIdTextView.text = cat.id
    }
}

class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)