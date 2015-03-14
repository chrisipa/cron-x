package de.papke.cronx.model;

public class Job {
	
	private String username;
	private String command;
	private String date;
	
	public Job() {}
	
	public Job(String username, String command, String date) {
		this.username = username;
		this.command = command;
		this.date = date;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "[ " + date + " | " + username + " | " + command + " ]";
	}
}
