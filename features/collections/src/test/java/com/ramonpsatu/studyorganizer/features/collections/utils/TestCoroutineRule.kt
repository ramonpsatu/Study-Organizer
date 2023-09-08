package com.ramonpsatu.studyorganizer.features.collections.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement


@Suppress("DEPRECATION")
@ExperimentalCoroutinesApi
class TestCoroutineRule :TestRule {

  private val testCoroutineDispatcher = TestCoroutineDispatcher()

  private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

  override fun apply(base: Statement, description: Description): Statement = object : Statement() {
    @Throws(Throwable::class)
    override fun evaluate() {
      Dispatchers.setMain(testCoroutineDispatcher)

      base.evaluate()

      Dispatchers.resetMain()
      testCoroutineScope.cleanupTestCoroutines()
    }
  }

  fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) =
    testCoroutineScope.runBlockingTest { block() }
}


/*
class TestCoroutineRule : TestRule {

  private val testCoroutineDispatcher = TestCoroutineDispatcher()

  private val testCoroutineScope = TestCoroutineScope(testCoroutineDispatcher)

  override fun apply(base: Statement, description: Description?) = object : Statement() {
    @Throws(Throwable::class)
    override fun evaluate() {
      Dispatchers.setMain(testCoroutineDispatcher)

      base.evaluate()

      Dispatchers.resetMain()
      testCoroutineScope.cleanupTestCoroutines()
    }
  }

  fun runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) =
    testCoroutineScope.runBlockingTest { block() }
}

----------------
: TestWatcher() {

  val testDispatcher = StandardTestDispatcher()

  override fun starting(description: Description?) {
    super.starting(description)
    Dispatchers.setMain(testDispatcher)
  }

  override fun finished(description: Description?) {
    super.finished(description)
    Dispatchers.resetMain()
  }
}
*/
