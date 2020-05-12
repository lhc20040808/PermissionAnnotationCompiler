package com.marco.permission_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface PermissionDenied {
    int value();
}
