package com.leyou.filters;

import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.config.FilterProperties;
import com.leyou.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HAILONG_WANG
 * @version 1.0
 * @description: 登录验证filter
 * @date 2021/2/28 14:49
 */
@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class AuthFilter extends ZuulFilter {

    @Autowired
    private JwtProperties prop;
    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        //过滤器类型   前置过滤
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //过滤顺序
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    //是否过滤
    @Override
    public boolean shouldFilter() {
        //1.获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //2.获取request
        HttpServletRequest request = ctx.getRequest();
        //3.根据获取的uri（ url为完整路径 ）
        String path = request.getRequestURI();
        //是否在白名单中，
        // 如果在 放行 返回false，不在拦截

        //遍历集合，是否已指定路径大头
        for (String allowPath : filterProperties.getAllowPaths()) {
            if (path.startsWith(allowPath)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public Object run() throws ZuulException {

        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = ctx.getRequest();
        //获取token
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());


        try {
            //1.解析token 是否登录的和是否被篡改
            UserInfo info = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            //TODO 校验权限
        } catch (Exception ex) {
            //解析token 失败 未登录 403
            ctx.setSendZuulResponse(false);
            //返回状态码 403
            ctx.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
        }
        return null;
    }
}
