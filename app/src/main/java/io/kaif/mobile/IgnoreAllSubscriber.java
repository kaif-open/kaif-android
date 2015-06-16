package io.kaif.mobile;

import rx.Subscriber;

public class IgnoreAllSubscriber<T> extends Subscriber<T> {

  @Override
  public void onCompleted() {

  }

  @Override
  public void onError(Throwable e) {

  }

  @Override
  public void onNext(T o) {

  }
}
