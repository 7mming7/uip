package com.sq.protocol.opc.controller;

import com.sq.inject.annotation.BaseComponent;
import com.sq.protocol.opc.service.MesuringPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * OPC通讯控制器
 * User: shuiqing
 * Date: 2015/9/18
 * Time: 14:57
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Controller
@RequestMapping("opc")
public class OpcConnController {

    @BaseComponent
    @Autowired
    private MesuringPointService mesuringPointService;

    /**
     * 列表opc连接
     * @return
     */
    @RequestMapping(value = "listOpcConnection.do",method = RequestMethod.POST)
    public String list (@ModelAttribute Page page,
                        @RequestParam Map<String, Object> parameterMap,
                        Model model) {
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        return "protocal/opc/opc-client-list";
    }

    /**
     * 新增OPC通信连接客户端
     * @return
     */
    @RequestMapping(value = "addOpcClient.do",method = RequestMethod.POST)
    public String addOpcConnClient (@RequestParam("connName") String connName,
                                    @RequestParam("host") String host,
                                    @RequestParam("username") String username,
                                    @RequestParam("password") String password,
                                    @RequestParam("clsid") String clsid,
                                    @RequestParam("pointFile") MultipartFile pointFile) {
        System.out.println(connName + host + username + password
                + clsid + pointFile.getOriginalFilename() + " ,size:" + pointFile.getSize());

        //保存opc配置对象
        /*opcConfigService.saveOpcConfig(connName,host,username,password,clsid);*/
        return null;
    }

    @RequestMapping(value = "deleteMp.do",method = RequestMethod.GET)
    public void deleteMesuringPoint(@RequestParam("pointId") String pointId) {
        mesuringPointService.delete(Long.parseLong(pointId));
    }
}
