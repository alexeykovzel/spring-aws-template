package com.alexeykovzel.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer, WebApplicationInitializer {
    private static final String BASE_PACKAGES_PATH = "com.alexeykovzel.example";
    private static final String FAVICON_PATH = "web/images/favicon.ico";

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/debug").setViewName("debug");
    }

    @Override
    public void onStartup(ServletContext context) {
        // Create the 'root' Spring application context
        var root = new AnnotationConfigWebApplicationContext();
        root.scan(BASE_PACKAGES_PATH);

        // Manage lifecycle of the root application context
        context.addListener(new ContextLoaderListener(root));

        // Register and map dispatcher servlet
        var dispatcherContext = new GenericWebApplicationContext();
        var dispatcherServlet = new DispatcherServlet(dispatcherContext);
        var dispatcher = context.addServlet("appServlet", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        return templateEngine;
    }

    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        var templates = new ClassLoaderTemplateResolver();
        templates.setPrefix("web/pages/");
        templates.setSuffix(".html");
        templates.setTemplateMode(TemplateMode.HTML);
        templates.setCharacterEncoding("UTF-8");
        templates.setOrder(1);
        templates.setCheckExistence(true);
        return templates;
    }

    @Bean
    public SimpleUrlHandlerMapping customFaviconHandlerMapping() {
        var mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(Collections.singletonMap("/favicon.ico", faviconRequestHandler()));
        mapping.setOrder(Integer.MIN_VALUE);
        return mapping;
    }

    @Bean
    protected ResourceHttpRequestHandler faviconRequestHandler() {
        var requestHandler = new ResourceHttpRequestHandler();
        var classPathResource = new ClassPathResource(FAVICON_PATH);
        requestHandler.setLocations(List.of(classPathResource));
        return requestHandler;
    }
}
