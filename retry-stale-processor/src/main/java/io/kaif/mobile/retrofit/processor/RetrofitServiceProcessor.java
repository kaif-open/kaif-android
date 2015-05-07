package io.kaif.mobile.retrofit.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.HEAD;
import retrofit.http.POST;
import retrofit.http.PUT;

@AutoService(Processor.class)
public class RetrofitServiceProcessor extends AbstractProcessor {


  private Elements elementUtils;
  private Filer filer;
  private Messager messager;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    elementUtils = processingEnv.getElementUtils();
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    annotations.stream()
        .flatMap(typeElement -> roundEnv.getElementsAnnotatedWith(typeElement).stream())
        .filter(element -> element.getKind() == ElementKind.METHOD)
        .map(Element::getEnclosingElement)
        .filter(element -> element.getKind() == ElementKind.INTERFACE)
        .filter(element -> !RetrofitAnnotatedInterface.isGenerated(element))
        .distinct()
        .map(element -> new RetrofitAnnotatedInterface((TypeElement) element))
        .forEach(this::generateCode);
    return true;
  }

  private void generateCode(RetrofitAnnotatedInterface retrofitAnnotatedInterface) {

    TypeSpec typeSpec = retrofitAnnotatedInterface.createRetryStaleInterface();

    TypeElement annotatedClassElement = retrofitAnnotatedInterface.getAnnotatedClassElement();
    JavaFile javaFile = JavaFile.builder(elementUtils.getPackageOf(annotatedClassElement)
        .getQualifiedName()
        .toString(), typeSpec).build();
    try {
      JavaFileObject jfo = filer.createSourceFile(retrofitAnnotatedInterface.getQualifiedName());
      try (Writer writer = jfo.openWriter()) {
        javaFile.writeTo(writer);
      }
    } catch (IOException e) {
      error(annotatedClassElement, e.getMessage());
    }
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> annotations = new LinkedHashSet<String>();
    annotations.add(GET.class.getCanonicalName());
    annotations.add(PUT.class.getCanonicalName());
    annotations.add(POST.class.getCanonicalName());
    annotations.add(DELETE.class.getCanonicalName());
    annotations.add(HEAD.class.getCanonicalName());
    return annotations;
  }

  private void error(Element e, String msg, Object... args) {
    messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
}
