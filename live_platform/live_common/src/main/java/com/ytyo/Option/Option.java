package com.ytyo.Option;

import org.springframework.lang.NonNull;

import java.util.Objects;

public class Option<T> {
    private T data;
    private static final Option<?> None = new Option<>();


    private Option(T data) {
        this.data = data;
    }

    private Option() {
    }

    public static <A> Option<A> from(A data) {
        if (Objects.isNull(data))
            return None();
        else
            return Some(data);
    }

    public static <A> Option<A> Some(@NonNull A data) {
        return new Option<>(data);
    }

    public static <A> Option<A> None() {
        @SuppressWarnings("unchecked")
        Option<A> none = (Option<A>) None;
        return none;
    }

    public T unwrap() throws NoneException {
//        说明是None
        if (Objects.isNull(data)) {
            throw new NoneException();
        } else {
            return data;
        }
    }

    public T data() {
        return data;
    }

    public T expect(String info) throws NoneException {
//        说明是None
        if (Objects.isNull(data)) {
            throw new NoneException(info);
        } else {
            return data;
        }
    }


    public boolean isSome() {
        return data != null;
    }

    public boolean isNone() {
        return !isSome();
    }

}
