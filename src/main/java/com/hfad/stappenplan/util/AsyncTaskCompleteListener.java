package com.hfad.stappenplan.util;



public interface AsyncTaskCompleteListener<T> {
    void onTaskComplete(T result);
}
