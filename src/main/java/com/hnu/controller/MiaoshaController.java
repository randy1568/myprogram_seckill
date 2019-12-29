package com.hnu.controller;

import com.hnu.access.AccessLimit;
import com.hnu.domain.MiaoshaOrder;
import com.hnu.domain.MiaoshaUser;
import com.hnu.domain.OrderInfo;
import com.hnu.rabbitMq.MQSender;
import com.hnu.rabbitMq.MiaoshaMessage;
import com.hnu.redis.AccessKey;
import com.hnu.redis.GoodsKey;
import com.hnu.redis.RedisService;
import com.hnu.result.CodeMsg;
import com.hnu.result.Result;
import com.hnu.service.GoodsService;
import com.hnu.service.MiaoshaService;
import com.hnu.service.MiaoshaUserService;
import com.hnu.service.OrderService;
import com.hnu.vo.goodsVo;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {


    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    MQSender mqSender;

    Map<Long,Boolean> map = new HashMap<>();

    /**
     * 系统初始化
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<goodsVo> goodsVo = goodsService.listGoodsVo();
        if(goodsVo == null){
            return;
        }
        for (goodsVo vos:goodsVo){
            //以id+库存 存进redis
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+vos.getId(),vos.getStockCount());
            map.put(vos.getId(),false);
        }
    }

    @AccessLimit(seconds =1,maxCount = 10,needLogin = true)
    @RequestMapping("/{path}/do_miaosha")
    public String list(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId, @PathVariable("path") String path) {

        //用户未登陆，回到登陆界面
        if (user == null) {
            return "login";
        }
        //验证path
        boolean check =  miaoshaService.checkPath(user,goodsId,path);
        if(!check){
            model.addAttribute("errmsg", CodeMsg.ILLEGAL_REQUEST.getMsg());
            return "miaosha_fail";
        }

        //内存标记，减少之后的redis访问
        if (map.get(goodsId)){
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }

        Long res = redisService.decr(GoodsKey.getMiaoshaGoodsStock, goodsId + "");
        if(res < 0){
            //没有库存
            map.put(goodsId,true);//内存标记
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //判断是否重复秒杀
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (miaoshaOrder != null) {
            model.addAttribute("errmsg", CodeMsg.REPEAT_MIAO_SHA.getMsg());
            return "miaosha_fail";
        }
        //入队
        MiaoshaMessage message = new MiaoshaMessage();
        message.setGoodsId(goodsId);
        message.setMiaoshaUser(user);
        String msg = redisService.beanToString(message);
        mqSender.send(msg);
        //请求转发
        return "forward:./getResult";

/**
        //判断库存
        goodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        Integer stockCount = goodsVo.getStockCount();
        if (stockCount <= 0) {
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }

        //判断是否重复秒杀
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (miaoshaOrder != null) {
            model.addAttribute("errmsg", CodeMsg.REPEAT_MIAO_SHA.getMsg());
            return "miaosha_fail";
        }
        //减库存、下订单、写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goodsVo);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goodsVo);
 **/
//        return "order_detail";
    }

    /**
     *
     * @param model
     * @param user
     * @param goodsId
     * @return 成功：OrderId 排队中：0 秒杀失败：-1
     */
    @RequestMapping("/getResult")
    @ResponseBody
    public Result<Long> getResult(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user",user);
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long result =  miaoshaService.getMiaoshaResult(user.getId(),goodsId);
        return new Result<Long>(result);
    }

    /**
     * 生成秒杀的接口地址的path，并且请求转发到真正的秒杀地址
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(seconds =5,maxCount = 10,needLogin = true)
    @RequestMapping("/getPath")
    public String getMiaoshaPath(HttpServletRequest request, Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user",user);
        if(user == null ){
            model.addAttribute("errmsg",CodeMsg.SERVER_ERROR.getMsg());
            return "miaosha_fail";
        }
        //查询访问接口的次数
        /**
        String uri = request.getRequestURI();
        String key = uri+"_"+user.getId();
        Integer count = redisService.get(AccessKey.access, key, Integer.class);
        if (count == null){
            redisService.set(AccessKey.access,key,1);
        }
        else if (count < 5){
            redisService.incr(AccessKey.access,key);
        }else{
            model.addAttribute("errmsg",CodeMsg.REQUEST_LIMIT.getMsg());
            return "miaosha_fail";
        }
         **/

        String path  = miaoshaService.createMiaoshaPath(user,goodsId);
        return "forwardt:./"+path+"/do_miaosha?goodsId=";
    }

}
