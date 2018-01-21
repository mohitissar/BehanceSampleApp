package com.cbr.behancesampleapp.dagger.module

import android.content.Context

import com.cbr.behancesampleapp.BuildConfig
import com.cbr.behancesampleapp.domain.network.BehanceApiService
import com.cbr.behancesampleapp.domain.network.BehanceRepository
import com.cbr.behancesampleapp.domain.network.Urls
import com.cbr.behancesampleapp.domain.network.Urls.Base
import com.cbr.behancesampleapp.util.NetworkUtil
import com.google.gson.Gson

import java.io.IOException
import java.util.concurrent.TimeUnit

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/** Created by dimitrios on 22/08/2017. */
@Module
class NetModule {
    
    private val HEADER_CACHE_CONTROL = "Cache-Control"
    private val CACHE_SIZE = 50 * 1024 * 1024 //50 MB
    
    @Singleton
    @Provides
    fun providesCache(context: Context): Cache {
        return Cache(context.cacheDir, CACHE_SIZE.toLong())
    }
    
    @Singleton
    @Provides
    fun providesOkHttpInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            
            if (NetworkUtil.isNetworkAvailable(context)) {
                request = request.newBuilder().header(HEADER_CACHE_CONTROL, "public, max-age=" + 60 * 60 * 24).build()
            } else {
                request = request.newBuilder().header(HEADER_CACHE_CONTROL, "public, only-if-cached, max-stale=" + 60 * 60 * 24).build()
            }
            
            chain.proceed(request)
        }
    }
    
    @Singleton
    @Provides
    fun providesOkHttpClient(interceptor: Interceptor, cache: Cache): OkHttpClient {
        
        val client = OkHttpClient.Builder()
        
        val logging = HttpLoggingInterceptor()
        
        if (BuildConfig.DEBUG) {
            //For Web Debugging.
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logging.level = HttpLoggingInterceptor.Level.NONE
        }
        
        return client
                .addInterceptor(logging)
                .addNetworkInterceptor(interceptor)
                .cache(cache)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build()
    }
    
    @Singleton
    @Provides
    fun providesRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(Base.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }
    
    @Singleton
    @Provides
    fun providesBehanceApiService(retrofit: Retrofit): BehanceApiService {
        return retrofit.create(BehanceApiService::class.java)
    }
    
    @Singleton
    @Provides
    fun providesBehanceRepository(service: BehanceApiService): BehanceRepository {
        return BehanceRepository(service)
    }
}
