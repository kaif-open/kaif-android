package io.kaif.mobile.retrofit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import retrofit.RetrofitError;
import retrofit.http.GET;
import rx.Observable;
import rx.functions.Func1;

/**
 * TODO
 * Generate this using annotation processor
 */
class RetryStaleHandler implements InvocationHandler {

  private ConcurrentHashMap<Method, MethodInfo> methodCache;

  private ConcurrentHashMap<Method, Method> retryMethodCache;

  private Object target;

  public RetryStaleHandler(Object target) {
    this.target = target;
    this.methodCache = new ConcurrentHashMap<>();
    this.retryMethodCache = new ConcurrentHashMap<>();
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    MethodInfo methodInfo = getTargetMethodInfo(method);
    if (methodInfo.canRetry()) {
      Observable result = (Observable) methodInfo.invoke(target, args);
      return result.onErrorResumeNext((Func1<Throwable, Observable>) throwable -> {
        if (throwable instanceof RetrofitError
            && ((RetrofitError) throwable).getKind() == RetrofitError.Kind.NETWORK) {
          try {
            return (Observable) getTargetCacheMethod(method).invoke(target, args);
          } catch (Exception e) {
            return Observable.error(throwable);
          }
        }
        return Observable.error(throwable);
      });
    }

    return methodInfo.invoke(target, args);
  }

  private MethodInfo getTargetMethodInfo(Method method) throws NoSuchMethodException {
    MethodInfo methodInfo = methodCache.get(method);
    if (methodInfo == null) {
      Method targetMethod = target.getClass()
          .getMethod(method.getName(), method.getParameterTypes());
      methodInfo = new MethodInfo(targetMethod,
          method.getReturnType().isAssignableFrom(Observable.class),
          method.isAnnotationPresent(GET.class));

      methodCache.putIfAbsent(method, methodInfo);
    }
    return methodInfo;
  }

  private Method getTargetCacheMethod(Method method) throws NoSuchMethodException {
    Method targetMethod = retryMethodCache.get(method);
    if (targetMethod == null) {
      targetMethod = target.getClass()
          .getMethod(method.getName() + "$$RetryStale", method.getParameterTypes());
      retryMethodCache.putIfAbsent(method, targetMethod);
    }
    return targetMethod;
  }
}
