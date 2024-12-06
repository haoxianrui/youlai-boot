package com.youlai.boot.common.util;

import com.youlai.boot.common.annotation.AnonymousAccess;
import com.youlai.boot.common.enums.RequestMethodEnum;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;
import java.util.stream.Collectors;

public class AnonymousUtils {
    /**
     * 获取所有匿名标记URL，不区分请求方式
     */
    public static Set<String> getAnonymousUrls(ApplicationContext applicationContext) {
        return getAllAnonymousUrls(applicationContext).values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    /**
     * 获取所有被标记的匿名类集合
     *
     * @return /
     */
    public static Map<String, Set<String>> getAllAnonymousUrls(ApplicationContext applicationContext) {
        // 搜索匿名标记
        RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping) applicationContext.getBean("requestMappingHandlerMapping");
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();

        // 获取所以被标记的匿名类集合
        return getAnonymousUrl(handlerMethodMap);
    }

    /**
     * 获取所有被标记的匿名类集合
     *
     * @param handlerMethodMap 请求映射信息集合
     * @return /
     */
    public static Map<String, Set<String>> getAnonymousUrl(Map<RequestMappingInfo, HandlerMethod> handlerMethodMap) {
        Set<String> get = new HashSet<>();
        Set<String> post = new HashSet<>();
        Set<String> put = new HashSet<>();
        Set<String> patch = new HashSet<>();
        Set<String> delete = new HashSet<>();
        Set<String> all = new HashSet<>();

        handlerMethodMap.forEach((key, value) -> {
            AnonymousAccess anonymousAccess = value.getMethodAnnotation(AnonymousAccess.class);
            if (anonymousAccess != null) {
                ArrayList<RequestMethod> requestMethods = new ArrayList<>(key.getMethodsCondition().getMethods());
                RequestMethodEnum request = RequestMethodEnum.find(requestMethods.isEmpty() ? RequestMethodEnum.ALL.getType() : requestMethods.get(0).name());
                switch (Objects.requireNonNull(request)) {
                    case GET:
                        get.addAll(key.getDirectPaths());
                        break;
                    case POST:
                        post.addAll(key.getDirectPaths());
                        break;
                    case PUT:
                        put.addAll(key.getDirectPaths());
                        break;
                    case PATCH:
                        patch.addAll(key.getDirectPaths());
                        break;
                    case DELETE:
                        delete.addAll(key.getDirectPaths());
                        break;
                    default:
                        all.addAll(key.getDirectPaths());
                }
            }
        });

        return Map.ofEntries(
                entry(RequestMethodEnum.GET.getType(), get),
                entry(RequestMethodEnum.POST.getType(), post),
                entry(RequestMethodEnum.PUT.getType(), put),
                entry(RequestMethodEnum.PATCH.getType(), patch),
                entry(RequestMethodEnum.DELETE.getType(), delete),
                entry(RequestMethodEnum.ALL.getType(), all)
        );
    }

    public static Map.Entry<String, Set<String>> entry(String key, Collection<String> collection) {
        return Map.entry(key, collection.stream().filter(it -> !it.isEmpty()).collect(Collectors.toUnmodifiableSet()));
    }

}
