package com.hnu.service;

import com.hnu.dao.UserDao;
import com.hnu.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User getById(int id){
        System.out.println(userDao);
        return userDao.getById(id);
    }

//    @Transactional
    public boolean tx(){
        User user = new User();
        user.setId(4);
        user.setName("张1");
        userDao.insert(user);

        user.setId(3);
        user.setName("张3");
        userDao.insert(user);
        return true;
    }
}
