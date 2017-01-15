package io.kaif.mobile.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.util.TypedValue
import io.kaif.mobile.R
import io.kaif.mobile.app.BaseActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout

class HonorActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HonorActivityUI().setContentView(this)
        setSupportActionBar(findOptional(io.kaif.mobile.R.id.tool_bar))
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, HonorFragment.newInstance())
                    .commit()
        }
    }
}

@SuppressLint("ParcelCreator")
class HonorActivityIntent(context: Context) : Intent(context, HonorActivity::class.java)

class HonorActivityUI : AnkoComponent<HonorActivity> {
    override fun createView(ui: AnkoContext<HonorActivity>) = with(ui) {
        coordinatorLayout {
            lparams(width = matchParent, height = matchParent)

            appBarLayout {
                lparams(width = matchParent)
                toolbar(R.style.ThemeOverlay_AppCompat_Dark_ActionBar) {
                    id = io.kaif.mobile.R.id.tool_bar
                }.lparams(width = matchParent) {
                    val tv = TypedValue()
                    if (ui.owner.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
                        height = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics);
                    }
                }
            }

            frameLayout {
                id = io.kaif.mobile.R.id.content_frame
            }.lparams(width = matchParent, height = matchParent) {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }
        }
    }
}
