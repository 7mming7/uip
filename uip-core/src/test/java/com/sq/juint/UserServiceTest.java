package com.sq.juint;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/3/25
 * Time: 13:55
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class UserServiceTest extends BaseUserIT {

    @Autowired
    UserService userService;

    @Test
    public void testSave() {
        User dbUser = userService.save(createUser());
        assertNotNull(dbUser.getId());
    }
}
