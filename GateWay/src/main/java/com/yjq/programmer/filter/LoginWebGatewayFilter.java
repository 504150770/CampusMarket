package com.yjq.programmer.filter;

import com.yjq.programmer.constant.WhiteList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-05-12 17:44
 */

@Component
public class LoginWebGatewayFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoginWebGatewayFilter.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        // 判断该路径是否需要验证
        for(String str : WhiteList.userNotNeedConfirmUrl){
            if(path.equals(str)){
                return chain.filter(exchange);
            }
        }
        //获取header的token参数
        String token = exchange.getRequest().getHeaders().getFirst("token");
        logger.info("前台登录验证开始，token：{}", token);
        if (token == null || token.isEmpty()) {
            logger.info( "token为空，请求被拦截" );
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        Object object = stringRedisTemplate.opsForValue().get("USER_" + token);
        if (object == null) {
            logger.warn( "token无效，请求被拦截" );
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder()
    {
        // 过滤器执行优先级 值越小优先级越高
        return 1;
    }
}
