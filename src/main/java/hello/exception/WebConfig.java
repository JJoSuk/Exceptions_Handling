package hello.exception;

import hello.exception.filter.LogFilter;
import hello.exception.interceptor.LogInterceptor;
import hello.exception.resolver.MyHandleExceptionResolver;
import hello.exception.resolver.UserHandlerExceptionResolver;
import org.springframework.asm.Handle;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/css/**",
                        "/*.ico",
                        "/error",
                        // 오류 페이지 경로
                        // error-page/500 같은 내부 호출의 경우에도 인터셉터가 호출된다.
                        "/error-page/**"
                );
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new MyHandleExceptionResolver());
        resolvers.add(new UserHandlerExceptionResolver());
    }

    // 인터셉터와 중복으로 처리를 막기 위해 @Bean 주석 처리
//    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LogFilter());
        filterFilterRegistrationBean.setOrder(1);
        filterFilterRegistrationBean.addUrlPatterns("/*");
        // 클라이언트 요청과 오류 페이지 요청을 필터를 통해 호출
        // 아무것도 입력 안하면 DispatcherType.REQUEST 가 기본
        // 오류 페이지 요청 전용 필터로 만들고 싶으면 DispatcherType.ERROR 만 입력
        filterFilterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
        return filterFilterRegistrationBean;
    }
}
