package com.marco.permission_compiler;

import com.google.auto.service.AutoService;
import com.marco.permission_annotation.PermissionDenied;
import com.marco.permission_annotation.PermissionGrant;
import com.marco.permission_annotation.PermissionRational;
import com.marco.permission_compiler.model.MethodInfo;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class PermissionProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Messager messager;
    private HashMap<String, MethodInfo> methodMap = new HashMap<>();
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();//日志输出工具，这里不能用Log
        filer = processingEnvironment.getFiler();
    }

    /**
     * @return 如果自己处理不了该注解，则返回false交由系统处理
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        methodMap.clear();
        messager.printMessage(Diagnostic.Kind.NOTE, "permission annotation process start");
        if (!handleAnnotation(roundEnvironment, PermissionGrant.class)) {
            return false;
        }
        if (!handleAnnotation(roundEnvironment, PermissionDenied.class)) {
            return false;
        }
        if (!handleAnnotation(roundEnvironment, PermissionRational.class)) {
            return false;
        }

        for (String className : methodMap.keySet()) {
            MethodInfo methodInfo = methodMap.get(className);
            try {
                JavaFileObject sourceFile = filer.createSourceFile(methodInfo.packageName + "." + methodInfo.fileName);
                Writer writer = sourceFile.openWriter();
                writer.write(methodInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                messager.printMessage(Diagnostic.Kind.NOTE, "write file failed:" + e.getMessage());
            }
        }

        return false;
    }

    private boolean handleAnnotation(RoundEnvironment roundEnvironment, Class<? extends Annotation> annotation) {
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(annotation);
        for (Element e : elementsAnnotatedWith) {
            if (!checkMethodValidator(e, annotation)) {
                return false;
            }
            ExecutableElement methodElement = (ExecutableElement) e;
            TypeElement enclosingElement = (TypeElement) methodElement.getEnclosingElement();
            String className = enclosingElement.getQualifiedName().toString();
            MethodInfo methodInfo = methodMap.get(className);
            if (methodInfo == null) {
                methodInfo = new MethodInfo(elementUtils, enclosingElement);
                methodMap.put(className, methodInfo);
            }
            Annotation annotationClz = methodElement.getAnnotation(annotation);
            String methodName = methodElement.getSimpleName().toString();
            List<? extends VariableElement> parameters = methodElement.getParameters();

            if (parameters == null || parameters.size() < 1) {
                String msg = "the method %s marked by annotation %s must have an unique parameter [String[] permissions]";
                throw new IllegalArgumentException(String.format(msg, methodName, annotationClz.getClass().getSimpleName()));
            }

            if (annotationClz instanceof PermissionGrant) {
                int requestCode = ((PermissionGrant) annotationClz).value();
                methodInfo.grantMethodMap.put(requestCode, methodName);
            } else if (annotationClz instanceof PermissionDenied) {
                int requestCode = ((PermissionDenied) annotationClz).value();
                methodInfo.deniedMethodMap.put(requestCode, methodName);
            } else if (annotationClz instanceof PermissionRational) {
                int requestCode = ((PermissionRational) annotationClz).value();
                methodInfo.rationalMethodMap.put(requestCode, methodName);
            }
        }
        return true;
    }

    private boolean checkMethodValidator(Element element, Class<? extends Annotation> annotation) {
        if (element.getKind() != ElementKind.METHOD) {
            //不是方法，则不处理
            return false;
        }
        if (element.getModifiers().contains(Modifier.PRIVATE)) {
            //Private修饰的方法，不处理
            return false;
        }
        if (element.getModifiers().contains(Modifier.ABSTRACT)) {
            //抽象方法，不处理
            return false;
        }
        return true;
    }

    //返回该注解处理器能处理的注解
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportList = new HashSet<>();
        //getCanonicalName()返回该类java语言规范定义的格式输出
        supportList.add(PermissionGrant.class.getCanonicalName());
        supportList.add(PermissionDenied.class.getCanonicalName());
        supportList.add(PermissionRational.class.getCanonicalName());
        return supportList;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
