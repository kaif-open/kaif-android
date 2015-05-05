package io.kaif.mobile.retrofit;

import java.lang.reflect.Proxy;

import javax.inject.Inject;

import retrofit.RestAdapter;

public class RetrofitCacheRetryProxy {

  @Inject
  RestAdapter restAdapter;

  public RetrofitCacheRetryProxy(RestAdapter restAdapter) {
    this.restAdapter = restAdapter;
  }

  public <T> T create(Class<T> serviceClass) {
    try {
      return serviceClass.cast(Proxy.newProxyInstance(serviceClass.getClassLoader(),
          new Class[] { serviceClass },
          new RetryHandler(restAdapter.create(Class.forName(serviceClass.getName()
              + "$$WithCacheRetry")))));
    } catch (ClassNotFoundException e) {
      return restAdapter.create(serviceClass);
    }
  }

}
