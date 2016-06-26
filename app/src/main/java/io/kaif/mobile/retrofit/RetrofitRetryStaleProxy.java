package io.kaif.mobile.retrofit;

import java.lang.reflect.Proxy;

import javax.inject.Inject;

import retrofit2.Retrofit;

/**
 * TODO
 * Generate this using annotation processor
 */
public class RetrofitRetryStaleProxy {

  public static class RetrofitHolder {

    @Inject
    Retrofit retrofit;

    public RetrofitHolder(Retrofit retrofit) {
      this.retrofit = retrofit;
    }

    public <T> T create(Class<T> serviceClass) {
      return retrofit.create(serviceClass);
    }
  }

  @Inject
  RetrofitHolder retrofitHolder;

  public RetrofitRetryStaleProxy(RetrofitHolder retrofitHolder) {
    this.retrofitHolder = retrofitHolder;
  }

  public <T> T create(Class<T> serviceClass) {
    try {
      return serviceClass.cast(Proxy.newProxyInstance(serviceClass.getClassLoader(),
          new Class[]{serviceClass},
          new RetryStaleHandler(retrofitHolder.create(Class.forName(serviceClass.getName()
              + "$$RetryStale")))));
    } catch (ClassNotFoundException e) {
      return retrofitHolder.create(serviceClass);
    }
  }

}
