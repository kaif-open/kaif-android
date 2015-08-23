package io.kaif.mobile.app;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class BaseActivity extends RxAppCompatActivity {

  protected <T> Observable<T> bind(Observable<T> observable) {
    return observable.compose(bindToLifecycle()).observeOn(AndroidSchedulers.mainThread());
  }

}
