package com.github.matheosu.meta.processor;

import com.github.matheosu.meta.Meta;
import com.github.matheosu.meta.MetaModel;
import com.github.matheosu.meta.util.TypeUtils;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("org.springframework.data.elasticsearch.annotations.Document")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class DocumentProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) { // Document

            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);


            List<MetaModel> metaModels = annotatedElements.stream()
                    .map(e -> new MetaModel(e.toString(), e.getEnclosedElements().stream()
                                                                    .filter(ee -> ElementKind.FIELD.equals(ee.getKind()))
                                                                    .map(ee -> (VariableElement) ee)
                                                                    .map(TypeUtils::createFromVariable)
                                                                    .collect(Collectors.toList())))
                    .collect(Collectors.toList());

            metaModels.forEach(this::writeMetaModel);

            metaModels.stream()
                    .flatMap(mm -> mm.getAttributes().stream())
                    .filter(m -> Meta.Type.ENTITY.equals(m.getType()))
                    .map(Meta::getClassName);

        }

        return false;
    }

    private void writeMetaModel(MetaModel meta) {
        try {
            JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(meta.getQualifiedClass());
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                meta.print(out);
                out.flush();
            }
        } catch (IOException io) {
            throw new RuntimeException(io);
        }

    }
}
