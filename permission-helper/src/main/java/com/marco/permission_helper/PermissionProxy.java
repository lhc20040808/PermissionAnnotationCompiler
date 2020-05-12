package com.marco.permission_helper;

public interface PermissionProxy<T> {
    void grant(T source, String[] permissions);

    void denied(T source, String[] permissions);

    boolean rational(T source, String[] permissions);
}
