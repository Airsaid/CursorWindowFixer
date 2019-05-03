package com.drakeet.purewriter

/**
 * @author Drakeet Xu
 */
interface Logger {
  fun log(level: Int, message: String) {}
  fun log(level: Int, e: Throwable) {}
}