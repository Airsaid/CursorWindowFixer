package com.drakeet.purewriter.sample

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
  @field:PrimaryKey(autoGenerate = true)
  val id: Int = 0,
  val body: String = ""
)