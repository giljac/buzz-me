package com.technologil21.buzzme

//import kotlinx.android.synthetic.main.contact_item.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ContactsAdapter(private val mContactsList: ArrayList<ContactItem>) : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {
    private var mListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onDeleteClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    class ContactsViewHolder(itemView: View, listener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        var mTextView1: TextView
        var mTextView2: TextView
        var mDeleteImage: ImageView

        init {
            mTextView1 = itemView.findViewById(R.id.textName)
            mTextView2 = itemView.findViewById(R.id.textNum)
            mDeleteImage = itemView.findViewById(R.id.image_delete)

            itemView.setOnClickListener {
                if (listener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) listener.onItemClick(position)
                }
            }
            mDeleteImage.setOnClickListener {
                if (listener != null) {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) listener.onDeleteClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactsViewHolder(v, mListener)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val currentItem = mContactsList[position]
        holder.mTextView1.text = currentItem.nom
        holder.mTextView2.text = currentItem.num
    }

    override fun getItemCount(): Int {
        return mContactsList.size
    }
}

