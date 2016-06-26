package io.kaif.mobile.service;

import android.app.Application;
import android.net.ConnectivityManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.kaif.mobile.BuildConfig;
import io.kaif.mobile.R;
import io.kaif.mobile.config.ApiConfiguration;
import io.kaif.mobile.json.ApiResponseDeserializer;
import io.kaif.mobile.model.oauth.AccessTokenInfo;
import io.kaif.mobile.model.oauth.AccessTokenManager;
import io.kaif.mobile.retrofit.RetrofitRetryStaleProxy;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

@Module
public class ServiceModule {

  private final Application application;

  private static final int CACHE_SIZE = 10 * 1024 * 1024;

  public ServiceModule(Application application) {
    this.application = application;
  }

  @Provides
  @Singleton
  public FeedService provideFeedService(
      @Named("apiRetrofit") RetrofitRetryStaleProxy restAdapter) {
    return restAdapter.create(FeedService.class);
  }

  @Provides
  @Singleton
  public AccountService provideAccountService(
      @Named("apiRetrofit") RetrofitRetryStaleProxy restAdapter) {
    return restAdapter.create(AccountService.class);
  }

  @Provides
  @Singleton
  public ZoneService provideZoneService(
      @Named("apiRetrofit") RetrofitRetryStaleProxy restAdapter) {
    return restAdapter.create(ZoneService.class);
  }

  @Provides
  @Singleton
  public VoteService provideVoteService(
      @Named("apiRetrofit") RetrofitRetryStaleProxy restAdapter) {
    return restAdapter.create(VoteService.class);
  }

  @Provides
  @Singleton
  public DebateService provideDebateService(
      @Named("apiRetrofit") RetrofitRetryStaleProxy restAdapter) {
    return restAdapter.create(DebateService.class);
  }

  @Provides
  @Singleton
  public ArticleService provideArticleService(
      @Named("apiRetrofit") RetrofitRetryStaleProxy restAdapter) {
    return restAdapter.create(ArticleService.class);
  }

  @Provides
  @Singleton
  public OauthService provideOauthService(@Named("oauthRetrofit") Retrofit retrofit) {
    return retrofit.create(OauthService.class);
  }

  @Provides
  @Singleton
  public Interceptor provideHeaderRequestInterceptor(AccessTokenManager accessTokenManager,
                                                     ConnectivityManager connectivityManager) {
    return chain -> {
      final AccessTokenInfo accountTokenInfo = accessTokenManager.findAccount();
      Request request = chain.request();
      if (accountTokenInfo != null) {
        request = request.newBuilder()
            .addHeader("Authorization", accountTokenInfo.getAuthorization())
            .addHeader("Content-Type", "application/json;charset=UTF-8")
            .build();
      }
      return chain.proceed(request);
    };
  }

  @Provides
  @Singleton
  public ApiConfiguration provideOauthConfiguration() {
    return new ApiConfiguration(application.getString(R.string.endpoint),
        BuildConfig.CLIENT_ID,
        BuildConfig.CLIENT_SECRET,
        application.getString(R.string.redirect_uri));
  }

  @Provides
  @Named("apiRetrofit")
  @Singleton
  RetrofitRetryStaleProxy provideApiRetrofit(Interceptor interceptor,
                                             ApiConfiguration apiConfiguration,
                                             OkHttpClient okHttpClient) {

    final Gson restApiGson = new GsonBuilder()
        .registerTypeHierarchyAdapter(Object.class, new ApiResponseDeserializer(new Gson()))
        .create();

    return new RetrofitRetryStaleProxy(new RetrofitRetryStaleProxy.RetrofitHolder(new Retrofit.Builder()
        .baseUrl(apiConfiguration.getEndPoint())
        .client(okHttpClient.newBuilder().addInterceptor(interceptor).build())
        .addConverterFactory(GsonConverterFactory.create(restApiGson))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
        .build()));
  }

  @Provides
  @Named("oauthRetrofit")
  @Singleton
  Retrofit provideOauthRetrofit(ApiConfiguration apiConfiguration, OkHttpClient client) {
    return new Retrofit.Builder().baseUrl(apiConfiguration.getEndPoint())
        .client(client.newBuilder().build())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
        .build();
  }

  @Provides
  @Singleton
  OkHttpClient provideOkClient(Cache cache) {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BASIC : HttpLoggingInterceptor.Level.NONE);
    return new OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .cache(cache).build();
  }

  @Provides
  @Singleton
  Cache provideOkHttpCache() {
    return new Cache(application.getExternalCacheDir(), CACHE_SIZE);
  }
}
