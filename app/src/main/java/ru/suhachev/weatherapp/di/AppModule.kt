package ru.suhachev.weatherapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.suhachev.weatherapp.data.local.WeatherDatabase
import ru.suhachev.weatherapp.data.network.WeatherApiService
import ru.suhachev.weatherapp.data.repository.WeatherRepositoryImpl
import ru.suhachev.weatherapp.domain.repository.WeatherRepository
import ru.suhachev.weatherapp.domain.usecase.GetCurrentWeatherUseCase
import ru.suhachev.weatherapp.domain.usecase.GetHourlyWeatherUseCase
import javax.inject.Singleton
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://api.weatherapi.com/v1/"
    private const val API_KEY = "7fab0999bb4146c7956124829250706"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)

    @Provides
    @Singleton
    fun provideWeatherDatabase(
        @ApplicationContext context: Context
    ): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWeatherDao(database: WeatherDatabase) = database.weatherDao()

    @Provides
    @Singleton
    fun provideWeatherRepository(
        api: WeatherApiService,
        dao: ru.suhachev.weatherapp.data.local.WeatherDao
    ): WeatherRepository =
        WeatherRepositoryImpl(api, dao, API_KEY)

    @Provides
    @Singleton
    fun provideGetCurrentWeatherUseCase(repository: WeatherRepository): GetCurrentWeatherUseCase =
        GetCurrentWeatherUseCase(repository)

    @Provides
    @Singleton
    fun provideGetHourlyWeatherUseCase(repository: WeatherRepository): GetHourlyWeatherUseCase =
        GetHourlyWeatherUseCase(repository)
} 