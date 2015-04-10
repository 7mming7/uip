package com.sq.dashbord.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Dashbord 控制器.
 * User: shuiqing
 * Date: 2015/3/26
 * Time: 11:20
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Controller
public class DashbordController {

    /**
     * 跳转到dashbord页面
     * @param model
     * @return
     */
    @RequestMapping("dashbord/toDashBord.do")
    public String list(Model model) {
        return "dashbord/dashbord";
    }
}
