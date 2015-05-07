package io.kaif.mobile.retrofit.processor;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleAnnotationValueVisitor7;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;

/**
 * copy from https://github.com/square/javapoet/pull/268
 * delete this file after pull request merge into https://github.com/square/javapoet
 */
public class AnnotationSpectUtil {

  public static AnnotationSpec generate(AnnotationMirror annotation) {
    TypeElement element = (TypeElement) annotation.getAnnotationType().asElement();
    AnnotationSpec.Builder builder = AnnotationSpec.builder(ClassName.get(element));
    Visitor visitor = new Visitor(builder);
    for (ExecutableElement executableElement : annotation.getElementValues().keySet()) {
      String name = executableElement.getSimpleName().toString();
      AnnotationValue value = annotation.getElementValues().get(executableElement);
      value.accept(visitor, new Entry(name, value));
    }
    return builder.build();
  }

  private static class Entry {
    final String name;
    final AnnotationValue value;

    Entry(String name, AnnotationValue value) {
      this.name = name;
      this.value = value;
    }
  }

  private static class Visitor
      extends SimpleAnnotationValueVisitor7<AnnotationSpec.Builder, Entry> {
    final AnnotationSpec.Builder builder;

    Visitor(AnnotationSpec.Builder builder) {
      super(builder);
      this.builder = builder;
    }

    @Override
    public AnnotationSpec.Builder visitArray(List<? extends AnnotationValue> vals, Entry entry) {
      Visitor visitor = new Visitor(builder);
      vals.forEach(val -> val.accept(visitor, new Entry(entry.name, val)));
      return builder;
    }

    @Override
    protected AnnotationSpec.Builder defaultAction(Object o, Entry entry) {
      return builder.addMember(entry.name, "$L", entry.value);
    }

    @Override
    public AnnotationSpec.Builder visitAnnotation(AnnotationMirror a, Entry entry) {
      return builder.addMember(entry.name, "$L", generate(a));
    }

    @Override
    public AnnotationSpec.Builder visitEnumConstant(VariableElement c, Entry entry) {
      return builder.addMember(entry.name, "$T.$L", c.asType(), c.getSimpleName());
    }

    @Override
    public AnnotationSpec.Builder visitType(TypeMirror t, Entry entry) {
      return builder.addMember(entry.name, "$T.class", t);
    }
  }
}
