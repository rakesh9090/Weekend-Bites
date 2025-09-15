package com.demo.assignmentapplication.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.demo.assignmentapplication.data.local.AppDatabase
import com.demo.assignmentapplication.data.local.HoldingDao
import com.demo.assignmentapplication.data.remote.ApiServices
import com.demo.assignmentapplication.data.remote.Status
import com.demo.assignmentapplication.data.repository.HoldingsRepositoryImpl
import com.google.gson.Gson
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class HoldingsRepositoryTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: ApiServices
    private lateinit var db: AppDatabase
    private lateinit var dao: HoldingDao
    private lateinit var repository: HoldingsRepositoryImpl

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
            .create(ApiServices::class.java)

        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = db.holdingDao()
        repository = HoldingsRepositoryImpl(api, dao)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
        db.close()
    }

    @Test
    fun testRefreshHoldingsHandlesApiErrorGracefully() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        repository.refreshHoldings()

        val result = repository.getHoldings().first { it.status != Status.LOADING }
        assertTrue(result.data!!.isEmpty())
    }


    @Test
    fun testRefreshHoldingsInsertsIntoDb() = runBlocking {
        val mockJson = """
    {
      "data": {
        "userHolding": [
          { "symbol": "TEST", "quantity": 10, "ltp": 100.0, "avgPrice": 90.0, "close": 95.0 }
        ]
      }
    }
    """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(mockJson).setResponseCode(200))

        repository.refreshHoldings()

        val holdings = withTimeout(1000) { dao.getAll().take(2).toList().last() }
        println(holdings) // debug: print what is actually in DB
        assertTrue(holdings.any { it.symbol == "TEST" })
    }


    @Test
    fun testRefreshHoldingsMapsApiResponseCorrectly() = runBlocking {
        enqueueResponse(
            """
            {
              "data": {
                "userHolding": [
                  { "symbol": "AAPL", "quantity": 5, "ltp": 150.0, "avgPrice": 140.0, "close": 145.0 },
                  { "symbol": "GOOGL", "quantity": 2, "ltp": 2800.0, "avgPrice": 2700.0, "close": 2750.0 }
                ]
              }
            }
            """
        )

        repository.refreshHoldings()

        val result = withTimeout(2_000) {
            repository.getHoldings().firstOrNull { it.data?.isNotEmpty() == true }
        }
        assertNotNull(result)
        val holdings = result!!.data!!
        assertEquals(2, holdings.size)

        val apple = holdings.first { it.symbol == "AAPL" }
        assertEquals(5, apple.quantity)
        assertEquals(150.0, apple.ltp, 0.001)

        val google = holdings.first { it.symbol == "GOOGL" }
        assertEquals(2, google.quantity)
        assertEquals(2800.0, google.ltp, 0.001)
    }

    // --- Helper ---
    private fun enqueueResponse(body: String, code: Int = 200) {
        mockWebServer.enqueue(MockResponse().setBody(body.trimIndent()).setResponseCode(code))
    }
}