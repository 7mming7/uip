package com.sq.web.controller;

import com.sq.entity.AbstractEntity;
import com.sq.inject.support.InjectBaseDependencyHelper;
import com.sq.service.BaseService;
import com.sq.util.ReflectUtils;
import com.sq.util.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serializable;

/**
 * 抽象出的Controller，作为基础的Controller存在.
 * User: shuiqing
 * Date: 2015/3/25
 * Time: 10:29
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public abstract class BaseController<M extends AbstractEntity, ID extends Serializable> implements InitializingBean {

    protected BaseService<M, ID> baseService;

    /**
     * 实体类型
     */
    protected final Class<M> entityClass;

    private String viewPrefix;


    protected BaseController() {
        this.entityClass = ReflectUtils.findParameterizedType(getClass(), 0);
        setViewPrefix(defaultViewPrefix());
    }

    /**
     * 设置基础service
     *
     * @param baseService
     */
    public void setBaseService(BaseService<M, ID> baseService) {
        this.baseService = baseService;
    }

    /**
     * bean 初始化之后需要查找该bean的依赖，并inject其依赖bean.
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if(this.entityClass != null) {
            InjectBaseDependencyHelper.findAndInjectBaseServiceDependency(this);
            Assert.notNull(baseService, "BaseService required, Class is:" + getClass());
        }
    }

    /**
     * 设置通用数据
     *
     * @param model
     */
    protected void setCommonData(Model model) {
    }


    /**
     * 当前模块 视图的前缀
     * 默认
     * 1、获取当前类头上的@RequestMapping中的value作为前缀
     * 2、如果没有就使用当前模型小写的简单类名
     */
    public void setViewPrefix(String viewPrefix) {
        if (viewPrefix.startsWith("/")) {
            viewPrefix = viewPrefix.substring(1);
        }
        this.viewPrefix = viewPrefix;
    }

    public String getViewPrefix() {
        return viewPrefix;
    }

    /**
     * 实例化一个当前Model对象
     * @return
     */
    protected M newModel() {
        try {
            return entityClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("can not instantiated model : " + this.entityClass, e);
        }
    }

    /**
     * 获取视图名称：即prefixViewName + "/" + suffixName
     * @return
     */
    public String viewName(String suffixName) {
        if (!suffixName.startsWith("/")) {
            suffixName = "/" + suffixName;
        }
        return getViewPrefix() + suffixName;
    }

    /**
     * 共享的验证规则
     * 验证失败返回true
     * @param m
     * @param result
     * @return
     */
    protected boolean hasError(M m, BindingResult result) {
        Assert.notNull(m);
        return result.hasErrors();
    }

    /**
     * 重定向到前一页面
     * @param backURL null 将重定向到默认getViewPrefix()
     * @return
     */
    protected String redirectToUrl(String backURL) {
        if (StringUtils.isEmpty(backURL)) {
            backURL = getViewPrefix();
        }
        if (!backURL.startsWith("/") && !backURL.startsWith("http")) {
            backURL = "/" + backURL;
        }
        return "redirect:" + backURL;
    }

    /**
     * 获取当前视图的路径前缀
     * @return 当前视图的路径
     */
    protected String defaultViewPrefix() {
        String currentViewPrefix = "";
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(getClass(), RequestMapping.class);
        if (requestMapping != null && requestMapping.value().length > 0) {
            currentViewPrefix = requestMapping.value()[0];
        }

        if (StringUtils.isEmpty(currentViewPrefix)) {
            currentViewPrefix = this.entityClass.getSimpleName();
        }

        return currentViewPrefix;
    }
}
