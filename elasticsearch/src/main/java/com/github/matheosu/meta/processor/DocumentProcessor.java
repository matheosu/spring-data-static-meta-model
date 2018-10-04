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
import java.util.LinkedHashSet;
import java.util.Objects;
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


            Set<MetaModel> models = annotatedElements.stream()
                    .map(this::getMetaModel).sorted()
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            // Extra Classes
            discoveryExtraClasses(models, roundEnv);

            models.forEach(this::writeMetaModel);


            // Arguments in Collections
            Set<MetaModel> collectionsArgument = models.stream().flatMap(mm -> mm.getAttributes().stream())
                    .filter(m -> Meta.Type.COLLECTION.equals(m.getType()))
                    .map(Meta::getTypeArguments)
                    .map(s -> roundEnv.getRootElements().stream()
                            .filter(e -> e.toString().equals(s))
                            .findFirst().orElse(null)
                    ).filter(Objects::nonNull)
                    .map(this::getMetaModel)
                    .sorted()
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            discoveryExtraClasses(collectionsArgument, roundEnv);
            collectionsArgument.forEach(this::writeMetaModel);

        }

        return false;
    }

    private void discoveryExtraClasses(Set<MetaModel> models, RoundEnvironment roundEnv) {
        // Extra Classes
        Set<MetaModel> extraModels = models;
        do {
            extraModels = extraModels.stream()
                    .flatMap(mm -> mm.getAttributes().stream())
                    .filter(m -> Meta.Type.ENTITY.equals(m.getType()))
                    .map(Meta::getClassName)
                    .map(s -> roundEnv.getRootElements().stream()
                            .filter(e -> e.toString().equals(s))
                            .findFirst().orElse(null))
                    .filter(Objects::nonNull)
                    .map(this::getMetaModel)
                    .sorted().collect(Collectors.toCollection(LinkedHashSet::new));
            models.addAll(extraModels);
        } while (!extraModels.isEmpty());
    }

    private MetaModel getMetaModel(Element e) {
        return new MetaModel(e.toString(), e.getEnclosedElements().stream()
                .filter(ee -> ElementKind.FIELD.equals(ee.getKind()))
                .map(ee -> (VariableElement) ee)
                .map(TypeUtils::createFromVariable)
                .collect(Collectors.toList()));
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
