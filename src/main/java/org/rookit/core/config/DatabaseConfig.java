package org.rookit.core.config;

import static org.rookit.core.config.ConfigUtils.*;

import java.util.Map;

import com.google.common.collect.Maps;

@SuppressWarnings("javadoc")
public class DatabaseConfig {
	
	public static final String DEFAULT_DRIVER = "rookit-mongodb";
	public static final Map<String, String> DEFAULT_OPTIONS = Maps.newLinkedHashMap();
	
	static {
		DEFAULT_OPTIONS.put("host", "localhost");
		DEFAULT_OPTIONS.put("port", "27039");
		DEFAULT_OPTIONS.put("db_name", "rookit");
	}
	
	private String driverName;
	private Map<String, String> options;
	
	public String getDriverName() {
		return getOrDefault(driverName, "");
	}
	
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	public Map<String, String> getOptions() {
		return getOrDefault(options, DEFAULT_OPTIONS);
	}
	
	public void setOptions(Map<String, String> options) {
		this.options = options;
	}
	
}