package io.kaif.mobile.json;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.os.Parcelable;
import auto.parcel.AutoParcel;

public class AutoParcelAdapterFactoryTest {

  static class Foo {
    String name;
  }

  static class Bar {
    String name;
    Foo data;
  }

  static class Hoge {
    String name;
    Bar data;
  }

  @AutoGson
  @AutoParcel
  static abstract class HogeAutoValue implements Parcelable {
    abstract String name();

    abstract Bar data();
  }

  Gson gson;

  @Before
  public void setUp() {
    Gson autoParcelGson = new GsonBuilder().registerTypeAdapterFactory(new AutoParcelAdapterFactory())
        .create();
    this.gson = new GsonBuilder().registerTypeHierarchyAdapter(Object.class,
        new ApiResponseDeserializer(autoParcelGson)).create();
  }

  @Test
  public void normal() {
    Foo foo = gson.fromJson("{ \"data\":{ \"name\":\"foo\"} }", Foo.class);
    assertEquals("foo", foo.name);
  }

  @Test
  public void nested() {
    Bar bar = gson.fromJson("{\"data\":{ \"name\":\"bar\", \"data\":{\"name\":\"foo\"}} }",
        Bar.class);
    assertEquals("bar", bar.name);
    assertEquals("foo", bar.data.name);
  }

  @Test
  public void nested_with_data_field_class() {
    Hoge hoge = gson.fromJson(
        "{\"data\":{ \"name\":\"hoge\", \"data\":{\"name\":\"bar\", \"data\":{\"name\":\"foo\"}}} }",
        Hoge.class);
    assertEquals("hoge", hoge.name);
    assertEquals("bar", hoge.data.name);
    assertEquals("foo", hoge.data.data.name);
  }

  @Test
  public void nested_auto_parcel_with_data_field_class() {
    HogeAutoValue hoge = gson.fromJson(
        "{\"data\":{ \"name\":\"hoge\", \"data\":{\"name\":\"bar\", \"data\":{\"name\":\"foo\"}}} }",
        HogeAutoValue.class);
    assertEquals("hoge", hoge.name());
    assertEquals("bar", hoge.data().name);
    assertEquals("foo", hoge.data().data.name);
  }


}