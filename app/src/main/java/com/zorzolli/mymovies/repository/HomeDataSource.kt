package com.zorzolli.mymovies.repository

import com.zorzolli.mymovies.network.ErrorResponse
import com.zorzolli.mymovies.network.NetworkResponse
import com.zorzolli.mymovies.network.model.dto.MovieDTO
import kotlinx.coroutines.CoroutineDispatcher

interface HomeDataSource {
    suspend fun getListsOfMovies(dispatcher: CoroutineDispatcher, homeResultCallback: (result: NetworkResponse<List<List<MovieDTO>>, ErrorResponse>) -> Unit)
}