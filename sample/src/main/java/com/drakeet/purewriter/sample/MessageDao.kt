package com.drakeet.purewriter.sample

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single

/**
 * @author Drakeet Xu
 */
@Dao
interface MessageDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(vararg message: Message)

  @Query("SELECT * FROM Message")
  fun singleAll(): Single<List<Message>>
}
