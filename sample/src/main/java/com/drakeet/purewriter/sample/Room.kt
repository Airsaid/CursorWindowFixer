package com.drakeet.purewriter.sample

import android.content.Context
import android.database.Cursor
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteQuery
import com.drakeet.purewriter.CursorWindowFixer

/**
 * @author Drakeet Xu
 */
@Database(entities = [Message::class], version = 1)
abstract class Room : RoomDatabase() {

  abstract fun messageDao(): MessageDao

  override fun query(query: SupportSQLiteQuery?): Cursor {
    return super.query(query).apply {
      if (enableCursorFix) {
        CursorWindowFixer.fixCursor(appContext, this)
      }
    }
  }

  override fun query(query: String?, args: Array<out Any>?): Cursor {
    return super.query(query, args).apply {
      if (enableCursorFix) {
        CursorWindowFixer.fixCursor(appContext, this)
      }
    }
  }

  companion object {
    var enableCursorFix: Boolean = false

    private lateinit var appContext: Context

    lateinit var db: Room
      private set

    fun init(context: Context) {
      appContext = context.applicationContext
      db = databaseBuilder(appContext, Room::class.java, "room.db").build()
    }
  }
}
