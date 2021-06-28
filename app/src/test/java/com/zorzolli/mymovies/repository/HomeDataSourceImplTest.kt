package com.zorzolli.mymovies.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.zorzolli.mymovies.AppConstants
import com.zorzolli.mymovies.network.ErrorResponse
import com.zorzolli.mymovies.network.NetworkResponse
import com.zorzolli.mymovies.network.TmdbApi
import com.zorzolli.mymovies.network.model.dto.MovieDTO
import com.zorzolli.mymovies.network.model.dto.MovieResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class HomeDataSourceImplTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val dispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var tmdbApi: TmdbApi

    private val movieResponseDTO = MovieResponseDTO(listOf(MovieDTO(0, "", "", "")))

    private lateinit var homeDataSourceImpl: HomeDataSourceImpl

    @Before
    fun init() {
        homeDataSourceImpl = HomeDataSourceImpl(tmdbApi)
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        dispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `when ALL 4 REQUESTS returns SUCCESSFULLY expect success network response`() = dispatcher.runBlockingTest {
         `when`(tmdbApi.getTrending(AppConstants.LANGUAGE, 1)).thenReturn(
             NetworkResponse.Success(movieResponseDTO)
         )
        `when`(tmdbApi.getUpcoming(AppConstants.LANGUAGE, 1)).thenReturn(
            NetworkResponse.Success(movieResponseDTO)
        )
        `when`(tmdbApi.getPopular(AppConstants.LANGUAGE, 1)).thenReturn(
            NetworkResponse.Success(movieResponseDTO)
        )
        `when`(tmdbApi.getTopRated(AppConstants.LANGUAGE, 1)).thenReturn(
            NetworkResponse.Success(movieResponseDTO)
        )

        var response: NetworkResponse<List<List<MovieDTO>>, ErrorResponse>? = null
        homeDataSourceImpl.getListsOfMovies(dispatcher) {
            response = it
        }

        assertTrue(response is NetworkResponse.Success)
        assertEquals(movieResponseDTO.results, (response as NetworkResponse.Success).body[0])
        assertEquals(movieResponseDTO.results, (response as NetworkResponse.Success).body[1])
        assertEquals(movieResponseDTO.results, (response as NetworkResponse.Success).body[2])
        assertEquals(movieResponseDTO.results, (response as NetworkResponse.Success).body[3])
    }

    @Test
    fun `when 1 OF THE REQUESTS returns API ERROR expect api error response`() = dispatcher.runBlockingTest {
        `when`(tmdbApi.getTrending(AppConstants.LANGUAGE, 1)).thenReturn(
            NetworkResponse.ApiError(ErrorResponse(), 400)
        )
        `when`(tmdbApi.getUpcoming(AppConstants.LANGUAGE, 1)).thenReturn(
            NetworkResponse.Success(movieResponseDTO)
        )
        `when`(tmdbApi.getPopular(AppConstants.LANGUAGE, 1)).thenReturn(
            NetworkResponse.Success(movieResponseDTO)
        )
        `when`(tmdbApi.getTopRated(AppConstants.LANGUAGE, 1)).thenReturn(
            NetworkResponse.Success(movieResponseDTO)
        )

        var response: NetworkResponse<List<List<MovieDTO>>, ErrorResponse>? = null
        homeDataSourceImpl.getListsOfMovies(dispatcher) {
            response = it
        }

        assertTrue(response is NetworkResponse.ApiError)
        assertEquals(NetworkResponse.ApiError(ErrorResponse(), 400).body, (response as NetworkResponse.ApiError).body)
    }

    @Test
    fun `when 1 OF THE REQUESTS returns NETWORK ERROR expect network error response`() = dispatcher.runBlockingTest {
        `when`(tmdbApi.getTrending(AppConstants.LANGUAGE, 1)).thenReturn(
            NetworkResponse.NetWorkError(IOException())
        )
        `when`(tmdbApi.getUpcoming(AppConstants.LANGUAGE, 1)).thenReturn(
            NetworkResponse.Success(movieResponseDTO)
        )
        `when`(tmdbApi.getPopular(AppConstants.LANGUAGE, 1)).thenReturn(
            NetworkResponse.Success(movieResponseDTO)
        )
        `when`(tmdbApi.getTopRated(AppConstants.LANGUAGE, 1)).thenReturn(
            NetworkResponse.Success(movieResponseDTO)
        )

        var response: NetworkResponse<List<List<MovieDTO>>, ErrorResponse>? = null
        homeDataSourceImpl.getListsOfMovies(dispatcher) {
            response = it
        }

        assertTrue(response is NetworkResponse.NetWorkError)
    }

    @Test
    fun `when 1 OF THE REQUESTS returns UNKNOWN ERROR expect unknown error response`() = dispatcher.runBlockingTest {
        `when`(tmdbApi.getTrending(AppConstants.LANGUAGE, 1)).thenReturn(
            NetworkResponse.UnknownError(Throwable())
        )
        `when`(tmdbApi.getUpcoming(AppConstants.LANGUAGE, 1)).thenReturn(
            NetworkResponse.Success(movieResponseDTO)
        )
        `when`(tmdbApi.getPopular(AppConstants.LANGUAGE, 1)).thenReturn(
            NetworkResponse.Success(movieResponseDTO)
        )
        `when`(tmdbApi.getTopRated(AppConstants.LANGUAGE, 1)).thenReturn(
            NetworkResponse.Success(movieResponseDTO)
        )

        var response: NetworkResponse<List<List<MovieDTO>>, ErrorResponse>? = null
        homeDataSourceImpl.getListsOfMovies(dispatcher) {
            response = it
        }

        assertTrue(response is NetworkResponse.UnknownError)
    }
}