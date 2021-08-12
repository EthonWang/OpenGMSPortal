package njgis.opengms.portal.config;

import njgis.opengms.portal.component.interceptor.AuthorityInterceptor;
import njgis.opengms.portal.component.interceptor.ModelAndViewInterceptor;
import njgis.opengms.portal.component.interceptor.TestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class IntercpetorConfig implements WebMvcConfigurer {

    @Autowired
    private ModelAndViewInterceptor modelAndViewInterceptor;

    @Autowired
    private AuthorityInterceptor authorityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(modelAndViewInterceptor).addPathPatterns("/**").excludePathPatterns("/static/**");
        registry.addInterceptor(authorityInterceptor).addPathPatterns("/**").excludePathPatterns("/static/**");
    }
}

