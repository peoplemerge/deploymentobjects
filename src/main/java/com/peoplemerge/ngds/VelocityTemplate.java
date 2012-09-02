package com.peoplemerge.ngds;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class VelocityTemplate implements Template {
	VelocityEngine ve;
	public VelocityTemplate(){
		Properties props = new Properties();
		// TODO switch to URL
		props.setProperty(VelocityEngine.RESOURCE_LOADER, "classpath");
		props.setProperty("classpath." + VelocityEngine.RESOURCE_LOADER
				+ ".class", ClasspathResourceLoader.class.getName());
		ve = new VelocityEngine(props);

		ve.init();

	}
	@Override
	public String encode(String templateName, Map<String, Object> vars) {
		
		VelocityContext context = new VelocityContext();
		for (String key : vars.keySet()) {
			context.put(key, vars.get(key));
		}
		org.apache.velocity.Template template = null;

		template = ve.getTemplate(templateName);

		StringWriter sw = new StringWriter();

		template.merge(context, sw);
		return sw.toString();
	}

}
