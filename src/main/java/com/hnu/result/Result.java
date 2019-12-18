package com.hnu.result;

public class Result<T> {

    //错误码
    private int code;
    //错误信息
    private String msg;
    //不出错的数据
    private T data;

    /**
     * 成功时候的调用
     *
     * @param data
     * @param <T>
     * @return
     */
    //对返回前台或者浏览器的json结果作封装
    public static <T> Result<T> success(T data) {
        return new Result<T>(data);
    }

    /**
     * 失败时候的调用
     *
     * @param codeMsg
     * @param <T>
     * @return
     */
    //这里code和msg是配套的，所以再次作封装
    public static <T> Result<T> error(CodeMsg codeMsg) {
        return new Result<T>(codeMsg);
    }

    public Result(T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    public Result(CodeMsg codeMsg) {
        if (codeMsg != null) {
            this.code = codeMsg.getCode();
            this.msg = codeMsg.getMsg();
        }
    }



    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
    public T getData() {
        return data;
    }

}
