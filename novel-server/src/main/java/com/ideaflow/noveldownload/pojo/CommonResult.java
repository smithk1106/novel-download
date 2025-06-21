package com.ideaflow.noveldownload.pojo;


import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回
 *
 * @param <T> 数据泛型
 */
@Data
public class CommonResult<T> implements Serializable {

    /**
     * 错误码
     *
     */
    private Integer code;
    /**
     * 返回数据
     */
    private T data;

    private String msg;

    /**
     * 将传入的 result 对象，转换成另外一个泛型结果的对象
     *
     * 因为 A 方法返回的 CommonResult 对象，不满足调用其的 B 方法的返回，所以需要进行转换。
     *
     * @param result 传入的 result 对象
     * @param <T>    返回的泛型
     * @return 新的 CommonResult 对象
     */
    public static <T> CommonResult<T> error(CommonResult<?> result) {
        return error(result.getCode(), result.getMsg());
    }

    public static <T> CommonResult<T> error(Integer code, String message) {
        CommonResult<T> result = new CommonResult<>();
        result.code = code;
        result.msg = message;
        return result;
    }

    public static <T> CommonResult<T> success() {
        CommonResult<T> result = new CommonResult<>();
        result.code = 200;
        result.msg = "";
        return result;
    }
    public static <T> CommonResult<T> error(String message) {
        CommonResult<T> result = new CommonResult<>();
        result.code = 500;
        result.msg = message;
        return result;
    }
    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = 200;
        result.data = data;
        result.msg = "";
        return result;
    }



}
