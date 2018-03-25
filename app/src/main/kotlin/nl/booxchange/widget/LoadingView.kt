package nl.booxchange.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.loading_view_layout.view.*
import nl.booxchange.R
import nl.booxchange.extension.getColorById
import nl.booxchange.extension.toGone
import nl.booxchange.extension.toVisible
import org.jetbrains.anko.withAlpha
import kotlin.properties.Delegates


/**
 * Created by Cristian Velinciuc on 3/24/18.
 */
class LoadingView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): FrameLayout(context, attrs, defStyleAttr) {

  var message: String by Delegates.observable("") { _, _, _ -> info_message?.text = message }

  init {
    View.inflate(context, R.layout.loading_view_layout, this)
    setBackgroundColor(Color.WHITE)
    (ContextCompat.getDrawable(context, R.mipmap.ic_logo_48dp) as? BitmapDrawable)?.bitmap?.let { bitmap ->
      val width = bitmap.width
      val height = bitmap.height
      val tintedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(tintedBitmap)
      val paint = Paint().apply {
        shader = LinearGradient(0f, width.toFloat(), 0f, height.toFloat(), context.getColorById(R.color.colorPrimary), context.getColorById(R.color.colorPrimaryDark), Shader.TileMode.CLAMP)
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
      }

      canvas.drawBitmap(bitmap, 0f, 0f, null)
      canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

      logo_image.setImageBitmap(tintedBitmap)
    }
    progress_bar.indeterminateDrawable.setColorFilter(context.getColorById(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
  }

  fun show(smooth: Boolean = true) {
    toVisible()
    if (smooth) {
      animate().alpha(1f).setDuration(100).start()
    }
  }

  fun hide(smooth: Boolean = true, delayed: Boolean = true) {
    val delay = if (delayed) 500L else 1L
    val animation = if (smooth) 200L else 1L
    postDelayed({
      animate().alpha(0f).setDuration(animation).withEndAction { toGone() }.start()
    }, delay)
  }
}