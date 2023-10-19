package com.example.server.imgen.vo;

public class Result<T> {
    public int code;
    public String msg;
    public T data;

    public static <T> Result<T> success()
    {
        return new Result<T>(0, "succ");
    }

    public static <T> Result<T> success(T data)
    {
        return new Result<T>(0, "succ", data);
    }

    public static <T> Result<T> error()
    {
        return new Result<T>(1, "error");
    }

    public static <T> Result<T> error(T data)
    {
        return new Result<T>(1, "error", data);
    }

    private Result(int code, String msg)
    {
        this.code = code;
        this.msg  = msg;
    }

    private Result(int code, String msg, T data)
    {
        this.code = code;
        this.msg  = msg;
        this.data = data;
    }
}
