package com.hnu.controller;

import com.hnu.domain.MiaoshaUser;
import com.hnu.redis.GoodsKey;
import com.hnu.redis.RedisService;
import com.hnu.service.GoodsService;
import com.hnu.service.MiaoshaUserService;
import com.hnu.vo.goodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    ThymeleafViewResolver viewResolver;

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
    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String list(Model model, MiaoshaUser user, HttpServletRequest request, HttpServletResponse response) {

        //1.取页面缓存   //因为对于任何客户只有一个list页面，所以这里的key“”。
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (html != null) {
            return html;
        }

        List<goodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        model.addAttribute("user", user);

        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        //2.手动渲染
        //第一个参数template为带渲染的视图名称
        html = viewResolver.getTemplateEngine().process("goods_list", context);
        //3.写进缓存
        redisService.set(GoodsKey.getGoodsList, "", html);
        return html;
//       return "goods_list";
    }

    @RequestMapping(value = "/to_detail/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detail(Model model, MiaoshaUser user, @PathVariable("goodsId") long goodsId,
                         HttpServletResponse response, HttpServletRequest request) {

        //1.取页面缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, goodsId + "", String.class);
        if (html != null) {
            System.out.println(html);
            return html;
        }


        model.addAttribute("user", user);

        goodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        //这些模型在detail.html会用到
        model.addAttribute("goods", goods);


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


        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        //2.手动渲染
        //第一个参数template为带渲染的视图名称
        html = viewResolver.getTemplateEngine().process("goods_detail", context);
        //3.写进缓存
        redisService.set(GoodsKey.getGoodsDetail, goodsId + "", html);
        return html;
//        return "goods_detail";
    }
}
