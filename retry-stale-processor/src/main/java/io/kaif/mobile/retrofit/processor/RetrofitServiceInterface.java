package io.kaif.mobile.retrofit.processor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import com.squareup.javapoet.TypeSpec;

public class RetrofitServiceInterface {

  public static final String RETRY_STALE_POSTFIX = "$$RetryStale";

  public TypeElement getAnnotatedClassElement() {
    return annotatedClassElement;
  }

  private final TypeElement annotatedClassElement;

  public String getQualifiedName() {
    return annotatedClassElement.getQualifiedName() + RETRY_STALE_POSTFIX;
  }

  public RetrofitServiceInterface(TypeElement classElement) throws IllegalArgumentException {
    this.annotatedClassElement = classElement;
  }

  public TypeSpec createRetryStaleInterface() {

    TypeSpec.Builder typeSpecBuilder = TypeSpec.interfaceBuilder(annotatedClassElement.getSimpleName()
        + RETRY_STALE_POSTFIX).addModifiers(Modifier.PUBLIC);

    annotatedClassElement.getEnclosedElements()
        .stream()
        .filter(element -> element.getKind() == ElementKind.METHOD)
        .map(element -> new RetrofitServiceMethod((ExecutableElement) element))
        .flatMap(methodInfo -> methodInfo.generateCodeWithRetryStaleIfRequired().stream())
        .forEach(typeSpecBuilder::addMethod);

    return typeSpecBuilder.build();
  }

  public static boolean isGenerated(Element element) {
    return element.getSimpleName().toString().contains(RETRY_STALE_POSTFIX);
  }

}
