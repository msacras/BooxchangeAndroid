package nl.booxchange.screens

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageButton
import com.vcristian.combus.expect
import com.vcristian.combus.post
import kotlinx.android.synthetic.main.activity_main_fragment.*
import nl.booxchange.BooxchangeApp
import nl.booxchange.R
import nl.booxchange.extension.getColorCompat
import nl.booxchange.extension.setVisible
import nl.booxchange.model.BookOpenedEvent
import nl.booxchange.model.BooksListOpenedEvent
import nl.booxchange.model.ChatOpenedEvent
import nl.booxchange.model.MessageReceivedEvent
import nl.booxchange.screens.book.BookActivity
import nl.booxchange.screens.home.MoreFragment
import nl.booxchange.screens.library.SettingsActivity
import nl.booxchange.screens.messages.ChatsStateChangeEvent
import nl.booxchange.utilities.BaseFragment
import nl.booxchange.utilities.Constants

@Suppress("DEPRECATION")
class MainFragmentActivity : AppCompatActivity() {
    val screens by lazy {
        listOf(
                home_page to "home_page",
                message_page to "message_page",
                library_page to "library_page"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_fragment)

        settings.setImageResource(R.drawable.ic_settings_icon)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.title = ""
        toolbar.subtitle = ""

        settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        screens.forEachIndexed { _, (button, tag) ->
            button.setOnClickListener {
                showFragment(tag)

                settings.setVisible(tag == "library_page")

                if (tag == "message_page") {
                    message_page.setColorFilter(whiteColor)
                    message_page.setBackgroundColor(greenColor)
                } else {
                    message_page.setColorFilter(darkGreenColor)
                    message_page.setBackgroundColor(whiteColor)
                }

                if (tag == "library_page") {
                    library_page.setColorFilter(whiteColor)
                    library_page.setBackgroundColor(greenColor)
                } else {
                    library_page.setColorFilter(darkGreenColor)
                    library_page.setBackgroundColor(whiteColor)
                }

                if (tag == "home_page") {
                    fragment_title.text = "booxchange"
                    home_page.setColorFilter(whiteColor)
                    home_page.setBackgroundColor(greenColor)
                    fragment_title.setTextAppearance(this, R.style.mainPage)
                } else {
                    home_page.setColorFilter(darkGreenColor)
                    home_page.setBackgroundColor(whiteColor)
                    fragment_title.text = tag.split("_").first().capitalize()
                    fragment_title.setTextAppearance(this, R.style.restPage)
                }

/*                screens.forEach { (otherButton, _) ->
                    val colorButton = if (otherButton == button) greenColor else whiteColor
                    updateColor(otherButton as AppCompatImageButton, colorButton)
                }*/
/*                val targetPositionX = (currentSelectedIndex * focused_button_highlight.width).toFloat()
                val transitionDuration = (Math.abs(focused_button_highlight.translationX - targetPositionX) / (focused_button_highlight.width * 3)) * 450
                focused_button_highlight.animate().translationX(targetPositionX).setDuration(transitionDuration.toLong()).start()*/
            }
        }

        when (intent.getStringExtra(Constants.EXTRA_PARAM_TARGET_VIEW)) {
//            Constants.FRAGMENT_LIBRARY -> library_page.performClick()
            Constants.FRAGMENT_LIBRARY -> {
                library_page.performClick()
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            Constants.FRAGMENT_CHAT -> post(ChatOpenedEvent(chatId = intent.getStringExtra(Constants.EXTRA_PARAM_CHAT_ID)))
            else -> home_page.performClick()
        }

        expect(MessageReceivedEvent::class.java) { (messageModel) ->
            //                        if (messageModel.id == UserData.Session.id) return@expect
            //TODO: unread messages counter icon

            messageModel.content
        }

/*        expect(ChatsStateChangeEvent::class.java) { (count) ->

        }*/
    }

    private val greenColor by lazy { getColorCompat(R.color.springGreen) }
    private val whiteColor by lazy { getColorCompat(R.color.whiteGray) }
    private val darkGreenColor by lazy { getColorCompat(R.color.darkGreen) }
    private val colorEvaluator = ArgbEvaluator()

    private fun updateColor(button: AppCompatImageButton, toColor: Int) {
        val fromColor = button.tag as? Int ?: whiteColor
        ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                val currentColor = colorEvaluator.evaluate(it.animatedFraction, fromColor, toColor) as Int
                button.setBackgroundColor(currentColor)
                //button.tag =  currentColor
            }
            duration = 150
            start()
        }
    }

    @SuppressLint("CommitTransaction")
    fun showFragment(tag: String, exclusive: Boolean = true) {
        supportFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).apply {
            supportFragmentManager.fragments.forEach { fragment ->
                if (fragment.tag == tag) show(fragment) else if (exclusive) hide(fragment)
            }
            commit()
        }
    }

    fun hideFragment(targetTag: String) {
        supportFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).hide(supportFragmentManager.findFragmentByTag(targetTag)!!).commit()
    }

    override fun onResume() {
        super.onResume()
        BooxchangeApp.isInForeground = true

    }

    override fun onPause() {
        super.onPause()
        BooxchangeApp.isInForeground = false
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBackPressed() {
        if (supportFragmentManager.fragments.all { (it as? BaseFragment)?.onBackPressed() != false }) {
            super.onBackPressed()
        }
    }
}
