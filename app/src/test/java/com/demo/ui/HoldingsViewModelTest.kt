package com.demo.ui

import app.cash.turbine.test
import com.demo.assignmentapplication.data.local.HoldingEntity
import com.demo.assignmentapplication.data.remote.Resource
import com.demo.assignmentapplication.data.repository.HoldingsRepository
import com.demo.assignmentapplication.ui.viewmodel.HoldingsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HoldingsViewModelTest {

    private val repository: HoldingsRepository = mockk()
    private lateinit var viewModel: HoldingsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `emits loading state`() = runTest {
        every { repository.getHoldings() } returns flowOf(Resource.loading())
        coEvery { repository.refreshHoldings() } returns Unit

        viewModel = HoldingsViewModel(repository)

        viewModel.state.test {
            val initial = awaitItem()
            assertFalse(initial.isLoading)
            assertTrue(initial.data.isEmpty())
            assertNull(initial.error)

            val loading = awaitItem()
            assertTrue(loading.isLoading)
            assertTrue(loading.data.isEmpty())
            assertNull(loading.error)

            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `emits success state with data`() = runTest {
        val testHoldings = listOf(
            HoldingEntity("TEST", 10, 100.0, 90.0, 95.0)
        )
        every { repository.getHoldings() } returns flowOf(Resource.success(testHoldings))
        coEvery { repository.refreshHoldings() } returns Unit

        viewModel = HoldingsViewModel(repository)

        viewModel.state.test {
            val initial = awaitItem()
            assertFalse(initial.isLoading)
            assertTrue(initial.data.isEmpty())
            assertNull(initial.error)

            val success = awaitItem()
            assertFalse(success.isLoading)
            assertEquals(1, success.data.size)
            assertEquals("TEST", success.data[0].symbol)
            assertNull(success.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits error state`() = runTest {
        val errorMessage = "Network error"
        every { repository.getHoldings() } returns flowOf(Resource.error(errorMessage))
        coEvery { repository.refreshHoldings() } returns Unit

        viewModel = HoldingsViewModel(repository)

        viewModel.state.test {
            val initial = awaitItem()
            assertFalse(initial.isLoading)
            assertTrue(initial.data.isEmpty())
            assertNull(initial.error)


            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertTrue(errorState.data.isEmpty())
            assertEquals(errorMessage, errorState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `refresh triggers repository refresh`() = runTest {
        every { repository.getHoldings() } returns flowOf(Resource.success(emptyList()))
        coEvery { repository.refreshHoldings() } returns Unit

        viewModel = HoldingsViewModel(repository)

        viewModel.refresh()
        advanceUntilIdle()

        coVerify(exactly = 2) { repository.refreshHoldings() }

    }
}
