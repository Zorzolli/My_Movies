package com.zorzolli.mymovies.di

import com.zorzolli.mymovies.repository.HomeDataSource
import com.zorzolli.mymovies.repository.HomeDataSourceImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class DataModule {
    @Singleton
    @Binds
    abstract fun provideHomeDataSource(datasource: HomeDataSourceImpl): HomeDataSource
}