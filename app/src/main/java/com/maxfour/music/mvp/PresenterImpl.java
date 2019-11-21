package com.maxfour.music.mvp;

public abstract class PresenterImpl<T> {
    protected T view;

    public void attachView(T view) {
        this.view = view;
    }

    public void detachView() {
        view = null;
    }
}
