package com.hnu.service;

import com.hnu.dao.MiaoshaUserDao;
import com.hnu.domain.MiaoshaUser;
import com.hnu.result.CodeMsg;
import com.hnu.util.MD5Util;
import com.hnu.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoshaUserService {

    @Autowired
    MiaoshaUserDao miaoshaUserDao;


    public MiaoshaUser getById(long id){
        return miaoshaUserDao.getById(id);
    }


    public CodeMsg login(LoginVo loginVo) {
        if (loginVo == null) {
            return CodeMsg.SERVER_ERROR;
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();

        //判断手机号码是否存在
        MiaoshaUser miaoshaUser = miaoshaUserDao.getById(Long.parseLong(mobile));
        if (miaoshaUser == null) {
            return CodeMsg.MOBILE_NOT_EXIST;
        }
        //验证密码
        String dbPass = miaoshaUser.getPassword();
        String saltDB = miaoshaUser.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if(!calcPass.equals(dbPass)){
            return CodeMsg.PASSWORD_ERROR;
        }
        return CodeMsg.SUCCESS;
    }

}
