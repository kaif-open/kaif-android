package io.kaif.mobile.view.daemon;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.kaif.mobile.BuildConfig;
import io.kaif.mobile.model.Zone;
import io.kaif.mobile.service.ZoneService;
import rx.Observable;

@Singleton
public class ZoneDaemon {

  private final ZoneService zoneService;

  @Inject
  ZoneDaemon(ZoneService zoneService) {
    this.zoneService = zoneService;
  }

  public Observable<List<Zone>> listAll() {

    Observable<List<Zone>> result = zoneService.listAll();
    if (BuildConfig.DEBUG) {
      return result.map(zones -> {
        zones.add(new Zone("test", "測試專區"));
        return zones;
      });
    }
    return result;
  }

}
