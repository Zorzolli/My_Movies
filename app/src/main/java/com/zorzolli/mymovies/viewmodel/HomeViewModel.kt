package com.zorzolli.mymovies.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zorzolli.mymovies.AppConstants
import com.zorzolli.mymovies.di.IoDispatcher
import com.zorzolli.mymovies.network.NetworkResponse
import com.zorzolli.mymovies.network.TmdbApi
import com.zorzolli.mymovies.network.model.dto.MovieDTO
import com.zorzolli.mymovies.repository.HomeDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val homeDataSource: HomeDataSource, @IoDispatcher private val dispatcher: CoroutineDispatcher): ViewModel() {

    private val _listsOfMovies: MutableLiveData<List<List<MovieDTO>>>? = MutableLiveData()
    val listsOfMovies: LiveData<List<List<MovieDTO>>>? = _listsOfMovies

    private val _errorMessage: MutableLiveData<String>? = MutableLiveData()
    val errorMessage: LiveData<String>? = _errorMessage

    private val _errorMessageVisibility: MutableLiveData<Boolean>? = MutableLiveData()
    val errorMessageVisibility: LiveData<Boolean>? = _errorMessageVisibility

    private val _isLoading: MutableLiveData<Boolean>? = MutableLiveData()
    val isLoading: LiveData<Boolean>? = _isLoading

    init {
        getListsOfMovies()
    }

    fun getListsOfMovies() {
        showErrorMessage(false)
        try {
            viewModelScope.launch(dispatcher) {
                homeDataSource.getListsOfMovies(dispatcher) { result ->
                    when (result) {
                        is NetworkResponse.Success -> {
                            _listsOfMovies?.postValue(result.body)
                            _isLoading?.postValue(false)
                            _errorMessageVisibility?.postValue(false)

                        }
                        is NetworkResponse.NetWorkError -> {
                            showErrorMessage(true, AppConstants.NETWORK_ERROR_MESSAGE)
                        }
                        is NetworkResponse.ApiError -> {
                            showErrorMessage(true, AppConstants.API_ERROR_MESSAGE)
                        }
                        is NetworkResponse.UnknownError -> {
                            showErrorMessage(true, AppConstants.UNKNOWN_ERROR_MESSAGE)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun showErrorMessage(show: Boolean, message: String? = null) {
        _isLoading?.postValue(!show)
        _errorMessageVisibility?.postValue(show)
        _errorMessage?.postValue(message)
    }
}