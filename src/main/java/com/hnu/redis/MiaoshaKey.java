package com.hnu.redis;

public class MiaoshaKey extends BasePrefix {

    public MiaoshaKey(String prefix) {
        super(0, prefix);
    }

   public static MiaoshaKey goodsOver = new MiaoshaKey("go");
   public static MiaoshaKey getMiaoshaPath = new MiaoshaKey("gMP");
}
