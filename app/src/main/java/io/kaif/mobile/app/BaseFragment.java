package io.kaif.mobile.app;

import com.trello.rxlifecycle.components.support.RxFragment;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BaseFragment extends RxFragment {

  protected <T> Observable<T> bind(Observable<T> observable) {
    return observable.compose(bindToLifecycle()).observeOn(AndroidSchedulers.mainThread());
  }

}
