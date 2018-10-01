package com.github.matheosu.meta;

import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

public class MetaModel implements Comparable<MetaModel>{

    private final List<Meta> attributes;
    private String nameClass;
    private String packageName;
    private String referenceClass;

    public MetaModel(String qualifiedNameReferenceClass,
                     List<Meta> attributes) {
        this.attributes = attributes;
        int lastDot = qualifiedNameReferenceClass.lastIndexOf('.');
        if (lastDot > 0) {
            this.packageName = qualifiedNameReferenceClass.substring(0, lastDot);
        }

        this.referenceClass = qualifiedNameReferenceClass.substring(lastDot + 1);
        this.nameClass = referenceClass + "_";
    }

    public void print(PrintWriter out) {
        printPackage(out);
        printImports(out);
        printAssingClassName(out);
        printMetaAttributes(out);
        printEnd(out);
    }

    private void printEnd(PrintWriter out) {
        out.println();
        out.println("}");
    }

    private void printPackage(PrintWriter out) {
        if (packageName != null) {
            out.print("package ");
            out.print(packageName);
            out.println(";");
            out.println();
        }
    }

    private void printImports(PrintWriter out) {
        out.println("import " + StaticMetaModel.class.getName() + ";");
        attributes.stream()
                .map(Meta::getType)
                .distinct()
                .map(Meta.Type::getFqdn)
                .forEach(fqdn -> out.println("import " + fqdn + ";"));

        out.println();
    }

    private void printAssingClassName(PrintWriter out) {
        out.println("@StaticMetaModel(" + referenceClass + ".class)");
        out.println("public class " + nameClass + " {");
        out.println();
    }

    private void printMetaAttributes(PrintWriter out) {
        attributes.stream()
                .map(m -> "\tpublic static final " + m.getType().getName() + " " + m.getField() + " = () -> \"" + m.getValue() + "\";")
                .forEach(out::println);
    }

    public String getNameClass() {
        return nameClass;
    }

    public String getQualifiedClass() {
        return packageName + "." + nameClass;
    }

    public List<Meta> getAttributes() {
        return attributes;
    }

    @Override
    public int compareTo(MetaModel o) {
        return referenceClass.compareTo(o.referenceClass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetaModel)) return false;
        MetaModel metaModel = (MetaModel) o;
        return Objects.equals(referenceClass, metaModel.referenceClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceClass);
    }
}
