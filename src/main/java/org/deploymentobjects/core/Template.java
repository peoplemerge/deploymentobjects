package org.deploymentobjects.core;

import java.util.Map;

public interface Template {
	String encode(String template, Map<String,Object> vars);
}
