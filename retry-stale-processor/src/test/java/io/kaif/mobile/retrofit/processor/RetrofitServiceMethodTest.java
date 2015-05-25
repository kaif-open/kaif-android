package io.kaif.mobile.retrofit.processor;

import static org.junit.Assert.*;

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import org.junit.Rule;
import org.junit.Test;

import com.google.testing.compile.CompilationRule;
import com.squareup.javapoet.MethodSpec;

import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;
import rx.Observable;

public class RetrofitServiceMethodTest {
  @Rule
  public final CompilationRule compilation = new CompilationRule();

  interface Foo1 {
    String bar(String hoge);
  }

  interface Foo2 {
    List<String> bar(String hoge, int bar);
  }

  interface Foo3 {
    List<String> bar(List<String> hoge, int bar);
  }

  interface Foo4 {
    @GET("/hoge")
    @Headers({ "A: B", "C: D" })
    List<String> bar(List<String> hoge, @Query("bar") int bar);
  }

  interface Foo5 {
    @GET("/hoge")
    Observable<String> bar(List<String> hoge, int bar);
  }

  interface Foo6 {
    @GET("/hoge")
    @Headers({ "A: B", "C: D" })
    Observable<String> bar(List<String> hoge, int bar);
  }

  RetrofitServiceMethod singleMethodInfo(Class<?> clazz) {
    TypeElement typeElement = compilation.getElements().getTypeElement(clazz.getCanonicalName());
    return new RetrofitServiceMethod(ElementFilter.methodsIn(typeElement.getEnclosedElements())
        .get(0));
  }

  @Test
  public void generateCodeWithRetryStaleIfRequired_normal() throws Exception {
    List<MethodSpec> methodSpecs = singleMethodInfo(Foo1.class).generateCodeWithRetryStaleIfRequired();
    assertEquals("public abstract java.lang.String bar(java.lang.String arg0);",
        methodSpecs.get(0).toString().trim());
  }

  @Test
  public void generateCodeWithRetryStaleIfRequired_generic_return() throws Exception {
    List<MethodSpec> methodSpecs = singleMethodInfo(Foo2.class).generateCodeWithRetryStaleIfRequired();
    assertEquals(
        "public abstract java.util.List<java.lang.String> bar(java.lang.String arg0, int arg1);",
        methodSpecs.get(0).toString().trim());
  }

  @Test
  public void generateCodeWithRetryStaleIfRequired_generic_params() throws Exception {
    List<MethodSpec> methodSpecs = singleMethodInfo(Foo3.class).generateCodeWithRetryStaleIfRequired();
    assertEquals(
        "public abstract java.util.List<java.lang.String> bar(java.util.List<java.lang.String> arg0, int arg1);",
        methodSpecs.get(0).toString().trim());
  }

  @Test
  public void generateCodeWithRetryStaleIfRequired_annotations() throws Exception {
    List<MethodSpec> methodSpecs = singleMethodInfo(Foo4.class).generateCodeWithRetryStaleIfRequired();
    assertEquals("@retrofit.http.GET(\"/hoge\")\n"
            + "@retrofit.http.Headers({\n"
            + "    \"A: B\",\n"
            + "    \"C: D\"\n"
            + "})\n"
            + "public abstract java.util.List<java.lang.String> bar(java.util.List<java.lang.String> arg0, @retrofit.http.Query(\"bar\") int arg1);",
        methodSpecs.get(0).toString().trim());
  }

  @Test
  public void generateCodeWithRetryStaleIfRequired_rx_get() throws Exception {
    List<MethodSpec> methodSpecs = singleMethodInfo(Foo5.class).generateCodeWithRetryStaleIfRequired();
    assertEquals("@retrofit.http.GET(\"/hoge\")\n"
            + "@retrofit.http.Headers(\"Cache-Control: max-stale=86400\")\n"
            + "public abstract rx.Observable<java.lang.String> bar$$RetryStale(java.util.List<java.lang.String> arg0, int arg1);",
        methodSpecs.get(1).toString().trim());
  }

  @Test
  public void generateCodeWithRetryStaleIfRequired_rx_get_with_header() throws Exception {
    List<MethodSpec> methodSpecs = singleMethodInfo(Foo6.class).generateCodeWithRetryStaleIfRequired();
    assertEquals("@retrofit.http.GET(\"/hoge\")\n"
            + "@retrofit.http.Headers({\n"
            + "    \"A: B\",\n"
            + "    \"C: D\",\n"
            + "    \"Cache-Control: max-stale=86400\"\n"
            + "})\n"
            + "public abstract rx.Observable<java.lang.String> bar$$RetryStale(java.util.List<java.lang.String> arg0, int arg1);",
        methodSpecs.get(1).toString().trim());
  }
}