package io.kaif.mobile.view

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.kaif.mobile.KaifApplication
import io.kaif.mobile.app.BaseFragment
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.frameLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx

class HonorFragment : BaseFragment() {

    companion object {
        fun newInstance(): HonorFragment {
            return HonorFragment()
        }
    }

    lateinit var honors: RecyclerView
    lateinit var honorsAdapter: HonorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KaifApplication.getInstance().beans().inject(this)
        honorsAdapter = HonorAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return HonorFragmentUI(honorsAdapter).createView(AnkoContext.create(ctx, this))
    }

}


class HonorFragmentUI(val honorAdapter: HonorAdapter) : AnkoComponent<HonorFragment> {
    override fun createView(ui: AnkoContext<HonorFragment>) = with(ui) {
        frameLayout {
            lparams(width = matchParent, height = matchParent)
            owner.honors = recyclerView {
                lparams(width = matchParent, height = matchParent)
                layoutManager = LinearLayoutManager(ctx)
                adapter = honorAdapter
            }
        }
    }
}
