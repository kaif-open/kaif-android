package io.kaif.mobile.retrofit;

import java.lang.reflect.Proxy;

import javax.inject.Inject;

import retrofit.RestAdapter;

/**
 * TODO
 * Generate this using annotation processor
 */
public class RetrofitRetryStaleProxy {

  @Inject
  RestAdapter restAdapter;

  public RetrofitRetryStaleProxy(RestAdapter restAdapter) {
    this.restAdapter = restAdapter;
  }

  public <T> T create(Class<T> serviceClass) {
    try {
      return serviceClass.cast(Proxy.newProxyInstance(serviceClass.getClassLoader(),
          new Class[] { serviceClass },
          new RetryStaleHandler(restAdapter.create(Class.forName(serviceClass.getName()
              + "$$RetryStale")))));
    } catch (ClassNotFoundException e) {
      return restAdapter.create(serviceClass);
    }
  }

}
