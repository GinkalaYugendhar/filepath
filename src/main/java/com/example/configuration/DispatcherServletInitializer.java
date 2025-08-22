package com.example.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DispatcherServletInitializer.class);

    @Override
    protected Class<?>[] getRootConfigClasses() {
        logger.info("Loading Root Configuration Classes...");
        return null;
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        logger.info("Loading Servlet Configuration Classes...");
        Class<?>[] c = {FileConfig.class};
        return c;
    }

    @Override
    protected String[] getServletMappings() {
        logger.info("Registering Servlet Mappings for DispatcherServlet...");
        String[] s = {"/"};
        logger.debug("Servlet Mappings configured: {}", (Object) s);
        return s;
    }

    public DispatcherServletInitializer() {
        logger.info("DispatcherServletInitializer created and initialized.");
    }
}
