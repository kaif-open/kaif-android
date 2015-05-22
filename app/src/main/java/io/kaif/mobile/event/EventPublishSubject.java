package io.kaif.mobile.event;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public class EventPublishSubject<T> {

  private final PublishSubject<T> subject;

  public EventPublishSubject() {
    this.subject = PublishSubject.create();
  }

  public Observable<T> getSubject(Class<?>... classes) {
    return subject.asObservable().filter(event -> {
      for (Class<?> clazz : classes) {
        if (clazz.isInstance(event)) {
          return true;
        }
      }
      return false;
    }).observeOn(AndroidSchedulers.mainThread());
  }

  public <E extends T> Observable<E> getSubject(Class<E> clazz) {
    return subject.asObservable()
        .filter(clazz::isInstance)
        .cast(clazz)
        .observeOn(AndroidSchedulers.mainThread());
  }

  public <E extends T> void onNext(E event) {
    subject.onNext(event);
  }
}
