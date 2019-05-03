package com.drakeet.purewriter.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.drakeet.purewriter.CursorWindowFixer
import com.drakeet.purewriter.Logger
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author Drakeet Xu
 */
@SuppressLint("SetTextI18n", "CheckResult")
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    Room.init(this)
    CursorWindowFixer.logger = object : Logger {
      override fun log(level: Int, e: Throwable) {
        log.text = "Log: $level: ${Log.getStackTraceString(e)}"
      }

      override fun log(level: Int, message: String) {
        log.text = "Log: $level: $message"
      }
    }
  }

  fun insertLargeMessage(v: View) {
    Completable.fromAction {
      val largeBody = StringBuilder()
      repeat(10_000_000) { largeBody.append("w") }
      Room.db.messageDao().insert(Message(body = largeBody.toString()))
    }
      .subscribeOn(io())
      .observeOn(mainThread())
      .subscribe { resultView.text = "insertLargeMessage: done" }
  }

  fun queryAll(v: View) {
    Room.db.messageDao().singleAll()
      .subscribeOn(io())
      .observeOn(mainThread())
      .filter { it.isNotEmpty() }
      .subscribe({ list ->
        resultView.text = """
            queryAll:
            list.size = ${list.size}
            first.body.length = ${list[0].body.length}
          """.trimIndent()
      }) { error ->
        error.printStackTrace()
        resultView.text = error.toString()
      }
  }

  fun init(v: View) {
    CursorWindowFixer.init(this)
    sCursorWindowSizeStateView.text = "sCursorWindowSize = ${CursorWindowFixer.fixedCursorWindowSize / 1024 / 1024}MB"
    queryAll(v)
  }

  fun refresh(v: View) {
    CursorWindowFixer.refresh(this)
    queryAll(v)
  }

  fun fixCursor(v: View) {
    Room.enableCursorFix = true
    queryAll(v)
  }
}
