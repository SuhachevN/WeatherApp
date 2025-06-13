package ru.suhachev.weatherapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.suhachev.weatherapp.data.local.WeatherDao
import ru.suhachev.weatherapp.data.local.WeatherDatabase
import ru.suhachev.weatherapp.data.network.WeatherApiService
import ru.suhachev.weatherapp.data.repository.WeatherRepositoryImpl
import ru.suhachev.weatherapp.domain.repository.WeatherRepository
import ru.suhachev.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import ru.suhachev.weatherapp.domain.usecase.GetHourlyWeatherUseCase
import ru.suhachev.weatherapp.domain.usecase.SearchCityUseCase
import ru.suhachev.weatherapp.util.LocationManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val API_KEY = "7fab0999bb4146c7956124829250706"

    @Provides
    @Singleton
    fun provideLocationManager(
        @ApplicationContext context: Context
    ): LocationManager = LocationManager()

    @Provides
    @Singleton
    fun provideWeatherDatabase(
        @ApplicationContext context: Context
    ): WeatherDatabase {
        return Room.databaseBuilder(
                context,
                WeatherDatabase::class.java,
                "weather.db"
            ).fallbackToDestructiveMigration(false).build()
    }

    @Provides
    @Singleton
    fun provideWeatherDao(db: WeatherDatabase): WeatherDao {
        return db.weatherDao()
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        api: WeatherApiService,
        dao: WeatherDao
    ): WeatherRepository {
        return WeatherRepositoryImpl(api, dao, API_KEY)
    }

    @Provides
    @Singleton
    fun provideGetCurrentWeatherUseCase(repository: WeatherRepository): GetCurrentWeatherUseCase =
        GetCurrentWeatherUseCase(repository)

    @Provides
    @Singleton
    fun provideGetHourlyWeatherUseCase(repository: WeatherRepository): GetHourlyWeatherUseCase =
        GetHourlyWeatherUseCase(repository)

    @Provides
    @Singleton
    fun provideSearchCityUseCase(api: WeatherApiService): SearchCityUseCase =
        SearchCityUseCase(api, API_KEY)
} 