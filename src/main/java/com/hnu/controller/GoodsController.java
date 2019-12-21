package com.hnu.controller;

import com.hnu.domain.MiaoshaUser;
import com.hnu.domain.miaoshaGoods;
import com.hnu.redis.RedisService;
import com.hnu.service.GoodsService;
import com.hnu.service.MiaoshaUserService;
import com.hnu.vo.goodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    /**通过cookie从缓存里得到miaoshaUser，这样写不雅观，改成下面那种方式
     @RequestMapping("/to_list") public String list(Model model,HttpServletResponse response,
     @CookieValue(value =MiaoshaUserService.COOKI_TOKEN_NAME,required = false) String cookieToken,
     @RequestParam(value = MiaoshaUserService.COOKI_TOKEN_NAME,required = false) String paraToken) {

     if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paraToken)){
     //返回登陆页面
     return "login";
     }
     String Token = StringUtils.isEmpty(cookieToken) ? paraToken:cookieToken;
     MiaoshaUser user =miaoshaUserService.geByToken(response,Token);
     model.addAttribute("user",user);
     if(user!=null){
     System.out.println(user.getNickname());
     }
     return "goods_list";
     }
     **/
    /**
     * 根据springmvc怎么把Model这些参数赋实例值的原理，给user赋值
     *
     * @param model
     * @param user
     * @return
     */
    @RequestMapping("/to_list")
    public String list(Model model, MiaoshaUser user) {

        List<goodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        model.addAttribute("user", user);

        return "goods_list";
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String detail(Model model, MiaoshaUser user, @PathVariable("goodsId") long goodsId) {

        model.addAttribute("user",user);

        goodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        //这些模型在detail.html会用到
        model.addAttribute("goods",goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remianSeconds = 0;//秒
        if (now < startAt) { //秒杀还未开始
            miaoshaStatus = 0;
            remianSeconds = (int) ((startAt - now) / 100);
        } else if (now > endAt) {//秒杀已经结束
            miaoshaStatus = 2;
            remianSeconds = -1;
        } else {//秒杀进行中
            miaoshaStatus = 1;
            remianSeconds = 0;
        }

        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remianSeconds);

        return "goods_detail";
    }
}
