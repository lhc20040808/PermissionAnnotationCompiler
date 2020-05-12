package com.marco.permission_compiler.model;

import java.util.HashMap;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class MethodInfo {

    public HashMap<Integer, String> grantMethodMap = new HashMap<>();
    public HashMap<Integer, String> deniedMethodMap = new HashMap<>();
    public HashMap<Integer, String> rationalMethodMap = new HashMap<>();

    public String packageName;
    public String className;
    public String fileName;

    private static final String SUFFIX = "PermissionProxy";

    public MethodInfo(Elements elementUtils, TypeElement typeElement) {
        PackageElement packageElement = elementUtils.getPackageOf(typeElement);
        packageName = packageElement.getQualifiedName().toString();
        int packageLen = packageName.length();
        //getQualifiedName拿到的是全路径，需要把包命截掉才能拿到类名
        className = typeElement.getQualifiedName().toString().substring(packageLen).replace(".", "$");
        fileName = className + "$" + SUFFIX;
    }

    public String generateJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("//generate code.do not modify\n");
        builder.append("package ").append(packageName).append(";\n\n");
        builder.append("import com.marco.permission_helper.*;");
        builder.append("\n");

        builder.append("public class ").append(fileName)
                .append(" implements ").append(SUFFIX).append("<").append(SUFFIX).append(">");
        builder.append("{\n");
        generateMethod(builder);
        builder.append("\n}");
        return builder.toString();
    }

    private void generateMethod(StringBuilder builder) {
        generateGrantMethod(builder);
        generateDeniedMethod(builder);
        generateRationalMethod(builder);
    }

    private void generateGrantMethod(StringBuilder builder) {
        builder.append("@Override\n");
        builder.append("public void grant(").append(className).append(" source, String[] permissions){\n");
        builder.append("switch(requestCode){\n");
        for (int requestCode : grantMethodMap.keySet()) {
            builder.append("case ").append(requestCode).append(":\n");
            builder.append("source.").append(grantMethodMap.get(requestCode)).append("(permissions);\n");
            builder.append("break;\n");
        }
        builder.append("}\n");
        builder.append("}");
    }

    private void generateDeniedMethod(StringBuilder builder) {
        builder.append("@Override\n");
        builder.append("public void denied(").append(className).append(" source, String[] permissions){\n");
        builder.append("switch(requestCode){");
        for (int requestCode : deniedMethodMap.keySet()) {
            builder.append("case ").append(requestCode).append(":\n");
            builder.append("source.").append(deniedMethodMap.get(requestCode)).append("(permissions);\n");
            builder.append("break;\n");
        }
        builder.append("\n}");
        builder.append("}");
    }

    private void generateRationalMethod(StringBuilder builder) {
        builder.append("@Override\n");
        builder.append("public void rational(").append(className).append(" source, String[] permissions){\n");
        builder.append("switch(requestCode){");
        for (int requestCode : rationalMethodMap.keySet()) {
            builder.append("case ").append(requestCode).append(":\n");
            builder.append("source.").append(rationalMethodMap.get(requestCode)).append("(permissions);\n");
            builder.append("return true;\n");
        }
        builder.append("}\n");
        builder.append("return false;\n");
        builder.append("}");
    }
}
