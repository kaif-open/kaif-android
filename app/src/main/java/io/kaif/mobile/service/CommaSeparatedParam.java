package io.kaif.mobile.service;

import java.util.Arrays;
import java.util.List;

import io.kaif.mobile.util.StringUtils;

public class CommaSeparatedParam {

  private String[] params;

  public static CommaSeparatedParam of(List<String> params) {
    return new CommaSeparatedParam(params.toArray(new String[params.size()]));
  }

  public CommaSeparatedParam(String[] params) {
    this.params = params;
  }

  @Override
  public String toString() {
    return StringUtils.join(",", params);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CommaSeparatedParam that = (CommaSeparatedParam) o;

    // Probably incorrect - comparing Object[] arrays with Arrays.equals
    return Arrays.equals(params, that.params);

  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(params);
  }
}
