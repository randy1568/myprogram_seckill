package com.hnu.result;

public class CodeMsg {

    private int code;
    private String msg;

    //通用错误码
    public static CodeMsg  SUCCESS = new CodeMsg(0,"success");
    public static CodeMsg  SERVER_ERROR  = new CodeMsg(500100,"服务端异常");
    public static CodeMsg  BIND_ERROR = new CodeMsg(500101,"参数校验异常：%s");

    //登陆模块 5002xx
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已经失效");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "登录密码不能为空");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "手机号不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "手机号格式错误");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "手机号不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");
    //商品模块 5003xx


    //订单模块5004xx


    //秒杀模块 5005XX
    public static CodeMsg MIAO_SHA_OVER = new CodeMsg(500500, "库存已空，秒杀结束");
    public static CodeMsg REPEAT_MIAO_SHA = new CodeMsg(500501, "重复秒杀");
   // public static CodeMsg NO_MORE_GOODS = new CodeMsg(500501, "没有库存");

    //对BIND_ERROR做参数封装


    public CodeMsg fillArgs(Object... args){
        int code =this.code;
        String message = String.format(this.msg,args);
        return new CodeMsg(code,message);
    }

    public CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
