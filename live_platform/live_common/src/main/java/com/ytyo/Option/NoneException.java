package com.ytyo.Option;

public class NoneException extends Exception {
    private String info;

    public NoneException() {
    }

    public NoneException(String info) {
        this.info = info;
    }

    @Override
    public String getMessage() {
        return "called `Option.expect` on a `None` value,cause" + (info == null ? "" : info);
    }
}
