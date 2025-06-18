package ru.suhachev.weatherapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.suhachev.weatherapp.data.dao.WeatherDao
import ru.suhachev.weatherapp.data.dao.GeocodingDao
import ru.suhachev.weatherapp.data.database.WeatherDatabase
import ru.suhachev.weatherapp.data.network.WeatherApiService
import ru.suhachev.weatherapp.data.network.GeocodingApiService
import ru.suhachev.weatherapp.data.repository.WeatherRepositoryImpl
import ru.suhachev.weatherapp.domain.repository.WeatherRepository
import ru.suhachev.weatherapp.presentation.util.LocationManager
import ru.suhachev.weatherapp.presentation.util.AndroidGeocoder
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocationManager(
        @ApplicationContext context: Context
    ): LocationManager = LocationManager(context)

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
    fun provideGeocodingDao(db: WeatherDatabase): GeocodingDao {
        return db.geocodingDao()
    }

    @Provides
    @Singleton
    fun provideAndroidGeocoder(
        @ApplicationContext context: Context,
        geocodingDao: GeocodingDao
    ): AndroidGeocoder = AndroidGeocoder(context, geocodingDao)

    @Provides
    @Singleton
    fun provideWeatherRepository(
        weatherApi: WeatherApiService,
        geocodingApi: GeocodingApiService,
        dao: WeatherDao,
        androidGeocoder: AndroidGeocoder
    ): WeatherRepository {
        return WeatherRepositoryImpl(weatherApi, geocodingApi, dao, androidGeocoder)
    }
} 