package io.kaif.mobile.retrofit.processor;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Optional;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import retrofit.http.GET;
import retrofit.http.Headers;
import rx.Observable;

public class MethodInfo {

  private final ExecutableElement methodElement;

  private final TypeMirror returnTypeMirror;

  private static final String CACHE_STALE_HEADER = "Cache-Control: public, only-if-cached, max-stale=86400";

  public MethodInfo(ExecutableElement methodElement) {
    this.methodElement = methodElement;
    returnTypeMirror = methodElement.getReturnType();
  }

  public List<MethodSpec> generateCodeWithRetryStaleIfRequired() {

    MethodSpec methodSpec = generateCode(false);
    if (!canAppendStaleHeader()) {
      return singletonList(methodSpec);
    }
    return asList(methodSpec, generateCode(true));
  }

  private MethodSpec generateCode(boolean withRetryStaleHeader) {

    MethodSpec.Builder builder = MethodSpec.methodBuilder(getMethodName(withRetryStaleHeader));

    builder.addModifiers(Modifier.ABSTRACT).addModifiers(Modifier.PUBLIC);
    methodElement.getParameters().stream().map(variableElement -> {
      ParameterSpec.Builder parameterBuilder = ParameterSpec.builder(TypeName.get(variableElement.asType()),
          variableElement.getSimpleName().toString());
      variableElement.getAnnotationMirrors()
          .stream()
          .map(AnnotationSpectUtil::generate)
          .forEach(parameterBuilder::addAnnotation);
      return parameterBuilder.build();
    }).forEach(builder::addParameter);

    List<AnnotationSpec> annotationSpecs = methodElement.getAnnotationMirrors()
        .stream()
        .map(AnnotationSpectUtil::generate)
        .collect(toList());

    if (withRetryStaleHeader) {
      Optional<AnnotationSpec> header = annotationSpecs.stream().filter(this::isHeader).findAny();
      if (header.isPresent()) {
        annotationSpecs.forEach(annotationSpec -> {
          if (isHeader(annotationSpec)) {
            AnnotationSpec.Builder replace = AnnotationSpec.builder((ClassName) annotationSpec.type);
            annotationSpec.members.forEach((s, codeBlocks) -> {
              codeBlocks.forEach(codeBlock -> {
                replace.addMember(s, codeBlock);
              });
            });
            replace.addMember("value", "$S", CACHE_STALE_HEADER);
            builder.addAnnotation(replace.build());
          } else {
            builder.addAnnotation(annotationSpec);
          }
        });
      } else {
        annotationSpecs.forEach(builder::addAnnotation);
        builder.addAnnotation(AnnotationSpec.builder(Headers.class)
            .addMember("value", "$S", CACHE_STALE_HEADER)
            .build());
      }
    } else {
      annotationSpecs.forEach(builder::addAnnotation);
    }

    TypeName returnTypeName = TypeName.get(returnTypeMirror);
    return builder.returns(returnTypeName).build();
  }

  private boolean isHeader(AnnotationSpec annotationSpec) {
    return annotationSpec.type.toString().equals(Headers.class.getCanonicalName());
  }

  private String getMethodName(boolean withRetryStaleHeader) {
    return methodElement.getSimpleName().toString() + (withRetryStaleHeader ? "$$RetryStale" : "");
  }

  private boolean canAppendStaleHeader() {
    if (methodElement.getAnnotation(GET.class) == null) {
      return false;
    }
    if (returnTypeMirror.getKind() != TypeKind.DECLARED) {
      return false;
    }
    String rawName = ((DeclaredType) returnTypeMirror).asElement().toString();
    return rawName.equals(Observable.class.getCanonicalName());

  }

}

