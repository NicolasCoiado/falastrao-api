package br.com.falastrao.falastrao.config;

import br.com.falastrao.falastrao.security.interceptor.RateLimitInterceptor;
import br.com.falastrao.falastrao.security.resolver.CurrentUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CurrentUserArgumentResolver resolver;
    private final RateLimitInterceptor rateLimitInterceptor;


    public WebConfig(CurrentUserArgumentResolver resolver, RateLimitInterceptor rateLimitInterceptor) {
        this.resolver = resolver;
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(resolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/**");
    }
}