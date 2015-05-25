package io.kaif.mobile.retrofit.processor;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

import javax.tools.JavaFileObject;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.JavaFileObjects;

public class RetrofitServiceProcessorTest {

  @Test
  public void process_non_Observable() {
    String input = "package test;\n"
        + "import retrofit.http.GET;\n"
        + "import retrofit.http.Query;\n"
        + "interface Foo {\n"
        + "    @GET(\"/foo\")\n"
        + "    String foo1(@Query(\"bar\") String bar);\n"
        + "\n"
        + "    @GET(\"/foo\")\n"
        + "    String foo2(@Query(\"bar\") String bar);\n"
        + "\n"
        + "    @GET(\"/foo\")\n"
        + "    String foo3(@Query(\"bar\") String bar);\n"
        + "  }";

    String output = "package test;\n"
        + "\n"
        + "import java.lang.String;\n"
        + "import retrofit.http.GET;\n"
        + "import retrofit.http.Query;\n"
        + "\n"
        + "public interface Foo$$RetryStale {\n"
        + "  @GET(\"/foo\")\n"
        + "  String foo1(@Query(\"bar\") String bar);\n"
        + "\n"
        + "  @GET(\"/foo\")\n"
        + "  String foo2(@Query(\"bar\") String bar);\n"
        + "\n"
        + "  @GET(\"/foo\")\n"
        + "  String foo3(@Query(\"bar\") String bar);\n"
        + "}";

    JavaFileObject inputFile = JavaFileObjects.forSourceString("test.Foo", input);
    JavaFileObject outputFile = JavaFileObjects.forSourceString("test.Foo$$RetryStale", output);

    assert_().about(javaSources())
        .that(ImmutableList.of(inputFile))
        .processedWith(new io.kaif.mobile.retrofit.processor.RetrofitServiceProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(outputFile);
  }


  @Test
  public void process_with_stage_header() {
    String input = "package test;\n"
        + "import rx.Observable;\n"
        + "import retrofit.http.Headers;\n"
        + "import retrofit.http.GET;\n"
        + "import retrofit.http.Query;\n"
        + "import retrofit.http.POST;\n"
        + "interface Foo {\n"
        + "    @GET(\"/foo\")\n"
        + "    Observable<String> foo1(@Query(\"bar\") String bar);\n"
        + "\n"
        + "    @POST(\"/foo\")\n"
        + "    Observable<String> foo2(@Query(\"bar\") String bar);\n"
        + "\n"
        + "    @Headers(\"Cache-Control: max-age=640000\")\n"
        + "    @GET(\"/foo\")\n"
        + "    Observable<String> foo3(@Query(\"bar\") String bar);\n"
        + "\n"
        + "    @Headers({\n"
        + "        \"Accept: application/vnd.github.v3.full+json\",\n"
        + "        \"User-Agent: Retrofit-Sample-App\"\n"
        + "    })\n"
        + "    @GET(\"/foo\")\n"
        + "    Observable<String> foo4(@Query(\"bar\") String bar);\n"
        + "  }";

    String output = "package test;\n"
        + "\n"
        + "import java.lang.String;\n"
        + "import retrofit.http.GET;\n"
        + "import retrofit.http.Headers;\n"
        + "import retrofit.http.POST;\n"
        + "import retrofit.http.Query;\n"
        + "import rx.Observable;\n"
        + "\n"
        + "public interface Foo$$RetryStale {\n"
        + "  @GET(\"/foo\")\n"
        + "  Observable<String> foo1(@Query(\"bar\") String bar);\n"
        + "\n"
        + "  @GET(\"/foo\")\n"
        + "  @Headers(\"Cache-Control: max-stale=86400\")\n"
        + "  Observable<String> foo1$$RetryStale(@Query(\"bar\") String bar);\n"
        + "\n"
        + "  @POST(\"/foo\")\n"
        + "  Observable<String> foo2(@Query(\"bar\") String bar);\n"
        + "\n"
        + "  @Headers(\"Cache-Control: max-age=640000\")\n"
        + "  @GET(\"/foo\")\n"
        + "  Observable<String> foo3(@Query(\"bar\") String bar);\n"
        + "\n"
        + "  @Headers({\n"
        + "      \"Cache-Control: max-age=640000\",\n"
        + "      \"Cache-Control: max-stale=86400\"\n"
        + "  })\n"
        + "  @GET(\"/foo\")\n"
        + "  Observable<String> foo3$$RetryStale(@Query(\"bar\") String bar);\n"
        + "\n"
        + "  @Headers({\n"
        + "      \"Accept: application/vnd.github.v3.full+json\",\n"
        + "      \"User-Agent: Retrofit-Sample-App\"\n"
        + "  })\n"
        + "  @GET(\"/foo\")\n"
        + "  Observable<String> foo4(@Query(\"bar\") String bar);\n"
        + "\n"
        + "  @Headers({\n"
        + "      \"Accept: application/vnd.github.v3.full+json\",\n"
        + "      \"User-Agent: Retrofit-Sample-App\",\n"
        + "      \"Cache-Control: max-stale=86400\"\n"
        + "  })\n"
        + "  @GET(\"/foo\")\n"
        + "  Observable<String> foo4$$RetryStale(@Query(\"bar\") String bar);\n"
        + "}";

    JavaFileObject inputFile = JavaFileObjects.forSourceString("test.Foo", input);
    JavaFileObject outputFile = JavaFileObjects.forSourceString("test.Foo$$RetryStale", output);

    assert_().about(javaSources())
        .that(ImmutableList.of(inputFile))
        .processedWith(new io.kaif.mobile.retrofit.processor.RetrofitServiceProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(outputFile);
  }
}