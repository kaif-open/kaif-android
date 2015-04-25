package io.kaif.mobile.app;

import rx.Observable;
import rx.android.app.support.RxFragment;
import rx.android.lifecycle.LifecycleObservable;
import rx.android.schedulers.AndroidSchedulers;

public class BaseFragment extends RxFragment {

  protected <T> Observable<T> bind(Observable<T> observable) {
    return LifecycleObservable.bindFragmentLifecycle(lifecycle(),
        observable.observeOn(AndroidSchedulers.mainThread()));
  }

}
