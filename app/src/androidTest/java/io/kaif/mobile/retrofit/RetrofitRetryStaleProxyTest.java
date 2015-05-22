package io.kaif.mobile.retrofit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

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
  private RestAdapter mockAdapter;

  @Mock
  private Foo$$RetryStale target;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void access_success() {
    when(mockAdapter.create(Foo$$RetryStale.class)).thenReturn(target);
    when(target.foo1("example")).thenReturn(Observable.just("success"));

    Foo foo = retrofitRetryStaleProxy.create(Foo.class);
    assertEquals("success", foo.foo1("example").toBlocking().single());
    verify(target, never()).foo1$$RetryStale("example");
  }

  @Test
  public void access_network_error() {
    when(mockAdapter.create(Foo$$RetryStale.class)).thenReturn(target);
    when(target.foo1("example")).thenReturn(Observable.error(RetrofitError.networkError("/foo",
        new IOException("failed"))));
    when(target.foo1$$RetryStale("example")).thenReturn(Observable.just("success"));

    Foo foo = retrofitRetryStaleProxy.create(Foo.class);
    assertEquals("success", foo.foo1("example").toBlocking().single());
  }

  @Test
  public void access_non_network_error() {
    when(mockAdapter.create(Foo$$RetryStale.class)).thenReturn(target);
    when(target.foo1("example")).thenReturn(Observable.error(RetrofitError.unexpectedError("/foo",
        new IllegalArgumentException("failed"))));

    Foo foo = retrofitRetryStaleProxy.create(Foo.class);
    try {
      foo.foo1("example").toBlocking().single();
      fail();
    } catch (RetrofitError expected) {
      assertEquals(RetrofitError.Kind.UNEXPECTED, expected.getKind());
      assertTrue(expected.getCause() instanceof IllegalArgumentException);
    }
    verify(target, never()).foo1$$RetryStale("example");
  }

  @Test
  public void access_non_rx_method() {
    when(mockAdapter.create(Foo$$RetryStale.class)).thenReturn(target);
    when(target.foo2("example")).thenReturn("success");

    Foo foo = retrofitRetryStaleProxy.create(Foo.class);
    assertEquals("success", foo.foo2("example"));
  }

  @Test
  public void access_no_cache_retry_method() {
    when(mockAdapter.create(Foo$$RetryStale.class)).thenReturn(target);
    when(target.foo3("example")).thenReturn(Observable.error(RetrofitError.networkError("/foo",
        new IOException("failed"))));

    Foo foo = retrofitRetryStaleProxy.create(Foo.class);
    try {
      foo.foo3("example").toBlocking().single();
      fail();
    } catch (RetrofitError expected) {
      assertTrue(expected.getCause() instanceof IOException);
    }
  }

  @Test
  public void access_network_error_and_cache_miss() {
    when(mockAdapter.create(Foo$$RetryStale.class)).thenReturn(target);
    when(target.foo1("example")).thenReturn(Observable.error(RetrofitError.networkError("/foo",
        new IOException("failed"))));
    when(target.foo1$$RetryStale("example")).thenReturn(Observable.error(RetrofitError.httpError(
        "/foo",
        new Response("/foo", 501, "stale", Collections.<Header>emptyList(), null),
        null,
        null)));

    Foo foo = retrofitRetryStaleProxy.create(Foo.class);
    try {
      foo.foo1("example").toBlocking().single();
      fail();
    } catch (RetrofitError expected) {
      assertEquals(RetrofitError.Kind.HTTP, expected.getKind());
    }
  }

}