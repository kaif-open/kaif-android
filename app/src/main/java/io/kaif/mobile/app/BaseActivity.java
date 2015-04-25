package io.kaif.mobile.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import rx.Observable;
import rx.android.lifecycle.LifecycleEvent;
import rx.android.lifecycle.LifecycleObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.BehaviorSubject;

public class BaseActivity extends AppCompatActivity {

  private final BehaviorSubject<LifecycleEvent> lifecycleSubject = BehaviorSubject.create();

  public Observable<LifecycleEvent> lifecycle() {
    return lifecycleSubject.asObservable();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    lifecycleSubject.onNext(LifecycleEvent.CREATE);
  }

  @Override
  protected void onStart() {
    super.onStart();
    lifecycleSubject.onNext(LifecycleEvent.START);
  }

  @Override
  protected void onResume() {
    super.onResume();
    lifecycleSubject.onNext(LifecycleEvent.RESUME);
  }

  @Override
  protected void onPause() {
    lifecycleSubject.onNext(LifecycleEvent.PAUSE);
    super.onPause();
  }

  @Override
  protected void onStop() {
    lifecycleSubject.onNext(LifecycleEvent.STOP);
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    lifecycleSubject.onNext(LifecycleEvent.DESTROY);
    super.onDestroy();
  }

  protected <T> Observable<T> bind(Observable<T> observable) {
    return LifecycleObservable.bindActivityLifecycle(lifecycle(),
        observable.observeOn(AndroidSchedulers.mainThread()));
  }

}
