package com.sq.juint;

import com.sq.inject.annotation.BaseComponent;
import com.sq.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/3/25
 * Time: 13:54
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Service()
public class UserService extends BaseService<User, Long> {

    @Autowired
    @BaseComponent
    UserRepository userRepository;
}
