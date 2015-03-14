package de.papke.cronx.model;

public class User {
	
	private static final String ROOT = "root";
	private static final String CURRENT_USERNAME = System.getProperty("user.name");

	public static String getName() {
		return CURRENT_USERNAME;
	}
	
	public static boolean isRoot() {
		return getName().equals(ROOT);
	}
}
