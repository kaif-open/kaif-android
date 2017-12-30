package io.kaif.mobile.view

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import io.kaif.mobile.R
import org.jetbrains.anko.*

class HonorAdapter : RecyclerView.Adapter<HonorViewHolder>() {

    private val honors = arrayOf("123", "456", "789", "123", "456", "789", "123", "456", "789", "123", "456", "789", "123", "456", "789", "123", "456", "789")

    override fun getItemCount(): Int {
        return honors.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): HonorViewHolder {
        return HonorViewHolder(HonorView().createView(AnkoContext.create(parent!!.context, parent)))
    }

    override fun onBindViewHolder(holder: HonorViewHolder?, position: Int) {
        holder!!.bind(honors[position])
    }
}

class HonorView : AnkoComponent<ViewGroup> {
    override fun createView(ui: AnkoContext<ViewGroup>): View {
        return with(ui) {
            linearLayout {
                lparams(width = matchParent, height = dip(48))
                orientation = LinearLayout.HORIZONTAL
                textView {
                    id = R.id.title
                    textSize = 16f
                }.lparams(width = matchParent, height = dip(48))
            }
        }
    }
}

class HonorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title: TextView? = itemView.findViewById(R.id.title)

    fun bind(data: String) {
        title?.text = "hi $data"
    }
}
