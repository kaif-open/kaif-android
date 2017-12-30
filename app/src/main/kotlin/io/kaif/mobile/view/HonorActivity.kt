package io.kaif.mobile.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import io.kaif.mobile.R
import io.kaif.mobile.app.BaseActivity
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.themedToolbar
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.themedAppBarLayout

class HonorActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HonorActivityUI().setContentView(this)
        setSupportActionBar(find(io.kaif.mobile.R.id.tool_bar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, HonorFragment.newInstance())
                    .commit()
        }
    }
}

class HonorActivityIntent(context: Context) : Intent(context, HonorActivity::class.java)

class HonorActivityUI : AnkoComponent<HonorActivity> {
    override fun createView(ui: AnkoContext<HonorActivity>) = with(ui) {
        coordinatorLayout {
            lparams(width = matchParent, height = matchParent)
            themedAppBarLayout {
                lparams(width = matchParent, height = wrapContent)
                themedToolbar(R.style.ThemeOverlay_AppCompat_Dark_ActionBar) {
                    id = io.kaif.mobile.R.id.tool_bar
                    popupTheme = R.style.ThemeOverlay_AppCompat_Light
                    title = resources.getString(R.string.honor)
                }.lparams(width = matchParent, height = wrapContent) {
                    scrollFlags = R.id.enterAlways
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
