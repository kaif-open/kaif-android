package io.kaif.mobile.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.kaif.mobile.app.BaseActivity
import org.jetbrains.anko.*

class HonorActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HonorActivityUI().setContentView(this)
    }
}

@SuppressLint("ParcelCreator")
class HonorActivityIntent(context: Context) : Intent(context, HonorActivity::class.java)

class HonorActivityUI : AnkoComponent<HonorActivity> {
    override fun createView(ui: AnkoContext<HonorActivity>) = with(ui) {
        verticalLayout {
            val name = editText()
            button("Say Hello") {
                onClick { ctx.toast("Hello, ${name.text}!") }
            }
        }
    }
}
