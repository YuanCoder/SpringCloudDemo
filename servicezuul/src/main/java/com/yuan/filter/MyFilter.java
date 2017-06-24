package com.yuan.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Zuul服务过滤
 *
 * @author Yuanjp
 * @create 2017-06-24-09:28
 */

@Component
public class MyFilter extends ZuulFilter{

    private static Logger logger= LoggerFactory.getLogger(MyFilter.class);

    @Override //pre：路由之前    routing：路由之时  post： 路由之后  error：发送错误调用
    public String filterType() { //返回一个字符串代表过滤器的类型
        return "pre";
    }

    @Override
    public int filterOrder() { //过滤的顺序
        return 0;
    }

    @Override
    public boolean shouldFilter() { //这里可以写逻辑判断，是否要过滤，本文true,永远过滤
        return true;
    }

    @Override
    public Object run() { // 过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问。
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        logger.info(String.format("%s >>> %s", request.getMethod(), request.getRequestURL().toString()));
        Object accessToken = request.getParameter("token");
        if(accessToken == null) {
            logger.warn("token is empty");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            try {
                ctx.getResponse().getWriter().write("token is empty");
            }catch (Exception e){}

            return null;
        }
        logger.info("ok");
        return null;
    }
}
