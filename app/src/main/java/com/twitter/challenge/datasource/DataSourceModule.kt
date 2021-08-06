package com.twitter.challenge.datasource

import android.app.Application
import com.twitter.challenge.WeatherApplication
import com.twitter.challenge.network.WeatherAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
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
            dataSourceScope: CoroutineScope,
            @Named("io") ioDispatcher: CoroutineDispatcher
    ): WeatherDataSource {
        return WeatherDataSourceImpl(weatherAPI, dataSourceScope, ioDispatcher)
    }

    @Provides
    fun providesDataSourceCoroutineScope(app: Application): CoroutineScope {
        return (app as WeatherApplication).scope
    }

    @Provides
    @Named("io")
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Named("default")
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}