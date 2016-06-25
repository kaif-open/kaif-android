package io.kaif.mobile.retrofit;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RetrofitRetryStaleProxyTest {

  interface Foo {
    @GET("/foo")
    Observable<String> foo1(@Query("bar") String bar);

    @GET("/foo")
    String foo2(@Query("bar") String bar);

    @GET("/foo")
    Observable<String> foo3(@Query("bar") String bar);
  }

  interface Foo$$RetryStale {
    @GET("/foo")
    Observable<String> foo1(@Query("bar") String bar);

    @GET("/foo")
    Observable<String> foo1$$RetryStale(@Query("bar") String bar);

    @GET("/foo")
    String foo2(@Query("bar") String bar);

    @GET("/foo")
    Observable<String> foo3(@Query("bar") String bar);

  }

  @InjectMocks
  private RetrofitRetryStaleProxy retrofitRetryStaleProxy;

  @Mock
  private RetrofitRetryStaleProxy.RetrofitHolder mockRetrofitHolder;

  @Mock
  private Foo$$RetryStale target;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void access_success() {
    when(mockRetrofitHolder.create(Foo$$RetryStale.class)).thenReturn(target);
    when(target.foo1("example")).thenReturn(Observable.just("success"));

    Foo foo = retrofitRetryStaleProxy.create(Foo.class);
    assertEquals("success", foo.foo1("example").toBlocking().single());
    verify(target, never()).foo1$$RetryStale("example");
  }

  @Test
  public void access_network_error() {
    when(mockRetrofitHolder.create(Foo$$RetryStale.class)).thenReturn(target);
    when(target.foo1("example")).thenReturn(Observable.error(
        new HttpException(Response.error(404,
            ResponseBody.create(MediaType.parse("application/json"), "{}"))))
    );
    when(target.foo1$$RetryStale("example")).thenReturn(Observable.just("success"));

    Foo foo = retrofitRetryStaleProxy.create(Foo.class);
    assertEquals("success", foo.foo1("example").toBlocking().single());
  }

  @Test
  public void access_non_network_error() {
    when(mockRetrofitHolder.create(Foo$$RetryStale.class)).thenReturn(target);
    when(target.foo1("example")).thenReturn(Observable.error(new IOException("other exception")));

    Foo foo = retrofitRetryStaleProxy.create(Foo.class);
    try {
      foo.foo1("example").toBlocking().single();
      fail();
    } catch (Exception expected) {
      assertTrue(expected.getCause() instanceof IOException);
    }
    verify(target, never()).foo1$$RetryStale("example");
  }

  @Test
  public void access_non_rx_method() {
    when(mockRetrofitHolder.create(Foo$$RetryStale.class)).thenReturn(target);
    when(target.foo2("example")).thenReturn("success");

    Foo foo = retrofitRetryStaleProxy.create(Foo.class);
    assertEquals("success", foo.foo2("example"));
  }

  @Test
  public void access_no_cache_retry_method() {
    when(mockRetrofitHolder.create(Foo$$RetryStale.class)).thenReturn(target);
    when(target.foo3("example")).thenReturn(Observable.error(
        new HttpException(Response.error(404,
            ResponseBody.create(MediaType.parse("application/json"), "{}")))));

    Foo foo = retrofitRetryStaleProxy.create(Foo.class);
    try {
      foo.foo3("example").toBlocking().single();
      fail();
    } catch (Exception expected) {
      assertEquals(HttpException.class, expected.getCause().getClass());
    }
  }

  @Test
  public void access_network_error_and_cache_miss() {
    when(mockRetrofitHolder.create(Foo$$RetryStale.class)).thenReturn(target);
    when(target.foo1("example")).thenReturn(Observable.error(
        new HttpException(Response.error(404,
            ResponseBody.create(MediaType.parse("application/json"), "{}")))));

    when(target.foo1$$RetryStale("example")).thenReturn(Observable.error(
        new HttpException(Response.error(501,
            ResponseBody.create(MediaType.parse("application/json"), "{}")))));

    Foo foo = retrofitRetryStaleProxy.create(Foo.class);
    try {
      foo.foo1("example").toBlocking().single();
      fail();
    } catch (Exception expected) {
      assertEquals(HttpException.class, expected.getCause().getClass());
    }
  }

}