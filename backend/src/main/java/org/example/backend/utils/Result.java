package org.example.backend.utils;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

public record Result<T>(int code, String message, T data) {
    public static<T> Result<T> success(T data) {
        return new Result<>(200, "Success", data);
    }

    public static <T> Result<T> success() {
        return new Result<>(200, "Success", null);
    }

    public static <T> Result<T> forbidden(T data) {
        return new Result<>(403, "forbidden", data);
    }

    public static <T> Result<T> unAuthorized(T data) {
        return new Result<>(401, "Unauthorized", data);
    }

    public static <T> Result<T> unAuthorized() {
        return new Result<>(401, "Unauthorized", null);
    }

    public String asJsonString() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
    public static <T> Result<T> failure(int code, String message) {
        return new Result<>(code, message, null);
    }
}
