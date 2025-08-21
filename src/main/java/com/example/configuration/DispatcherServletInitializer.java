package com.example.configuration;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer
{
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return null;
	}

	@Override
	protected Class[] getServletConfigClasses() {
		Class c[] = {FileConfig.class};
		return c;
	}

	@Override
	protected String[] getServletMappings() {
		String s[] = {"/"};
		return s;
	}
	
}
