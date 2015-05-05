package io.kaif.mobile.retrofit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodInfo {

  private Method method;

  private boolean isObservable;

  private boolean isGetMethod;

  public MethodInfo(Method method, boolean isObservable, boolean isGetMethod) {
    this.method = method;
    this.isObservable = isObservable;
    this.isGetMethod = isGetMethod;
  }

  public Object invoke(Object receiver, Object[] args)
      throws InvocationTargetException, IllegalAccessException {
    return method.invoke(receiver, args);
  }

  public boolean canRetry() {
    return isObservable && isGetMethod;
  }
}
