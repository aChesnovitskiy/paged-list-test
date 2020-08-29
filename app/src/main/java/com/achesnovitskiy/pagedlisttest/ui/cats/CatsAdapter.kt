package com.achesnovitskiy.pagedlisttest.ui.cats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.achesnovitskiy.pagedlisttest.R
import com.achesnovitskiy.pagedlisttest.ui.entities.PresentationCat
import com.achesnovitskiy.pagedlisttest.ui.entities.errorCat
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_cat.view.*

class CatsAdapter(
    private val onCatLongClickListener: (PresentationCat) -> Unit,
    private val onLoadesIsVisibleListener: () -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var cats: MutableList<PresentationCat> = mutableListOf()

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

            TYPE_LOADER -> LoaderViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(
                        R.layout.item_loader,
                        parent,
                        false
                    )
            )

            else -> ErrorViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(
                        R.layout.item_error,
                        parent,
                        false
                    )
            )
        }

    override fun getItemCount(): Int = cats.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CatViewHolder -> holder.bind(cats[position])
            is LoaderViewHolder -> onLoadesIsVisibleListener()
        }
    }

    override fun getItemViewType(position: Int): Int =
        when {
            cats[position].isLoader -> TYPE_LOADER
            cats[position].isError -> TYPE_ERROR
            else -> TYPE_CAT
        }

    fun updateCats(data: List<PresentationCat>) {
        val diffCallback = object : DiffUtil.Callback() {

            override fun areItemsTheSame(oldPos: Int, newPos: Int) =
                cats[oldPos].id == data[newPos].id

            override fun areContentsTheSame(oldPos: Int, newPos: Int) =
                cats[oldPos] == data[newPos]

            override fun getOldListSize() = cats.size

            override fun getNewListSize() = data.size
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        cats = data.toMutableList()

        diffResult.dispatchUpdatesTo(this)
    }

    fun hideLoader() {
        val lastIndex = cats.size - 1

        if (cats.isNotEmpty() && cats[lastIndex].isLoader) {
            cats.removeAt(lastIndex)

            notifyItemRemoved(lastIndex)
        }
    }

    fun showError() {
        if (cats.isEmpty() || !cats[cats.size - 1].isError) {
            cats.add(errorCat)

            notifyItemInserted(cats.size)
        }
    }

    companion object {
        const val TYPE_CAT = 1
        const val TYPE_LOADER = 2
        const val TYPE_ERROR = 3
    }

    inner class CatViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

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

            if (cat.isSelected) {
                val backgroundColor: Int = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorSelectedItemBackground
                )

                itemView.setBackgroundColor(backgroundColor)
            }

            itemView.setOnLongClickListener {
                onCatLongClickListener.invoke(cat)

                true
            }
        }
    }

    class LoaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}