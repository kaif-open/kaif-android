package io.kaif.mobile.retrofit.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.List;
import java.util.Optional;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import rx.Observable;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class RetrofitServiceMethod {

  private final ExecutableElement methodElement;

  private static final String CACHE_STALE_HEADER = "Cache-Control: max-stale=86400";

  public RetrofitServiceMethod(ExecutableElement methodElement) {
    this.methodElement = methodElement;
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
          .map(AnnotationSpecUtil::generate)
          .forEach(parameterBuilder::addAnnotation);
      return parameterBuilder.build();
    }).forEach(builder::addParameter);

    List<AnnotationSpec> annotationSpecs = methodElement.getAnnotationMirrors()
        .stream()
        .map(AnnotationSpecUtil::generate)
        .collect(toList());

    if (withRetryStaleHeader) {
      Optional<AnnotationSpec> header = annotationSpecs.stream()
          .filter(annotationSpec -> isHeaderAnnotation(annotationSpec))
          .findAny();
      if (header.isPresent()) {
        annotationSpecs.forEach(annotationSpec -> {
          if (isHeaderAnnotation(annotationSpec)) {
            AnnotationSpec.Builder replace = AnnotationSpec.builder((ClassName) annotationSpec.type);
            annotationSpec.members.forEach((String s, List<CodeBlock> codeBlocks) -> {
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

    return builder.returns(TypeName.get(methodElement.getReturnType())).build();
  }

  private String getMethodName(boolean withRetryStaleHeader) {
    return methodElement.getSimpleName().toString() + (withRetryStaleHeader ? "$$RetryStale" : "");
  }

  private boolean canAppendStaleHeader() {
    if (methodElement.getAnnotation(GET.class) == null) {
      return false;
    }
    if (methodElement.getReturnType().getKind() != TypeKind.DECLARED) {
      return false;
    }
    String rawName = ((DeclaredType) methodElement.getReturnType()).asElement().toString();
    return rawName.equals(Observable.class.getCanonicalName());

  }

  private static boolean isHeaderAnnotation(AnnotationSpec annotationSpec) {
    return annotationSpec.type.toString().equals(Headers.class.getCanonicalName());
  }

}