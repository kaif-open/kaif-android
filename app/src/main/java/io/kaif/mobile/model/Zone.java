package io.kaif.mobile.model;

public class Zone {
  private String name;
  private String aliasName;

  public Zone(String name, String aliasName) {
    this.name = name;
    this.aliasName = aliasName;
  }

  public String getName() {
    return name;
  }

  public String getAliasName() {
    return aliasName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Zone zone = (Zone) o;

    return name.equals(zone.name);

  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
