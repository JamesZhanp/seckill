package com.james.secondkill.service;

import com.james.secondkill.dao.UserDao;
import com.james.secondkill.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: JamesZhan
 * @create: 2020 - 02 - 07 15:45
 */

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User getById(int id){
        User user = userDao.getById(id);
        return user;
    }

//    @Transactional
//    public boolean tx(){
//        User user = new User();
//
//
//        return true;
//    }
}
