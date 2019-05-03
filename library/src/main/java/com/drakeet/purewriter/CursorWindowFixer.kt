package com.drakeet.purewriter

import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.content.Context
import android.database.AbstractWindowedCursor
import android.database.Cursor
import android.database.CursorWindow
import android.os.Build
import android.util.Log

/**
 * @author Drakeet Xu
 */
object CursorWindowFixer {

  var logger: Logger? = null

  private val Int.MB: Int get() = this * 1024 * 1024

  var fixedCursorWindowSize = -1
    private set

  private val sCursorWindowSizeAccessibleField by lazy {
    CursorWindow::class.java.getDeclaredField("sCursorWindowSize").apply { isAccessible = true }
  }

  fun init(context: Context) {
    fixedCursorWindowSize = -1
    val info = refresh(context)
    logger?.log(Log.INFO, "totalMem: ${info.totalMem / 1.MB}MB")
    logger?.log(Log.INFO, "availMem: ${info.availMem / 1.MB}MB")
  }

  fun refresh(context: Context): MemoryInfo {
    val info = memoryInfo(context)
    val availMem = info.availMem
    val lastValue = fixedCursorWindowSize
    fixedCursorWindowSize = when {
      availMem > 2000.MB -> 112.MB
      availMem > 512.MB -> 88.MB
      availMem > 256.MB -> 50.MB
      availMem > 128.MB -> 20.MB
      else -> 10.MB
    }
    if (lastValue != fixedCursorWindowSize) {
      fix(logger != null)
    }
    return info
  }

  fun fixCursor(context: Context, cursor: Cursor?) {
    if (Build.VERSION.SDK_INT >= 28 && cursor != null) {
      (cursor as? AbstractWindowedCursor)?.apply {
        refresh(context)
        if (fixedCursorWindowSize != -1)
          window = CursorWindow("CompatFixed", fixedCursorWindowSize.toLong())
      }
    }
  }

  private fun fix(log: Boolean = true) {
    fix(fixedCursorWindowSize, log)
  }

  private fun memoryInfo(context: Context): MemoryInfo {
    val info = MemoryInfo()
    (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(info)
    return info
  }

  private fun fix(fixedSize: Int, log: Boolean) {
    try {
      sCursorWindowSizeAccessibleField.set(null, fixedSize)
    } catch (e: Exception) {
      logger?.log(Log.ERROR, e)
    }
    try {
      CursorWindow("FailFast").close()
      if (log) logger?.log(Log.INFO, "fixedSize: ${fixedSize / 1.MB}MB")
    } catch (e: Exception /* for CursorWindowAllocationException */) {
      e.printStackTrace()
      fix(fixedSize / 4, log)
    }
  }
}
