package com.twitter.challenge.datasource

import com.twitter.challenge.network.WeatherAPI
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun providesWeatherDataSource(
            weatherAPI: WeatherAPI,
            @Named("io") ioDispatcher: CoroutineDispatcher
    ): WeatherDataSource {
        return WeatherDataSourceImpl(weatherAPI, ioDispatcher)
    }

    @Provides
    @Named("io")
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Named("default")
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}