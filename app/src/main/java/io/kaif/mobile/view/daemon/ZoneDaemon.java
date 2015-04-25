package io.kaif.mobile.view.daemon;

import java.util.List;

import javax.inject.Inject;

import io.kaif.mobile.model.Zone;
import io.kaif.mobile.service.ZoneService;
import rx.Observable;

public class ZoneDaemon {

  private final ZoneService zoneService;

  @Inject
  ZoneDaemon(ZoneService zoneService) {
    this.zoneService = zoneService;
  }

  public Observable<List<Zone>> listAll() {
    return zoneService.listAll();
  }

}
