package com.ytyo.Option;

import org.springframework.lang.NonNull;

import java.util.Objects;

public class Result<T, E extends Throwable> {

    private T data;

    private E err;

    private Result(T data) {
        this.data = data;
    }

    private Result(E err) {
        this.err = err;
    }

    public static <A, R extends Throwable> Result<A, R> Ok(@NonNull A data) {
        return new Result<>(data);
    }

    public static <A, R extends Throwable> Result<A, R> Err(@NonNull R err) {
        return new Result<>(err);
    }


    public T unwrap() throws E {
        //说明是Err
        if (Objects.isNull(data)) {
            throw this.err;
        } else {
            return data;
        }
    }

    public boolean isOk() {
        return data != null;
    }

    public boolean isErr() {
        return !isOk();
    }


}
