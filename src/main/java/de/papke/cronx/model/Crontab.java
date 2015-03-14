package de.papke.cronx.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Crontab {
	
	private Map<String, String> variables = new HashMap<String, String>();
	private List<Job> jobs = new ArrayList<Job>();
	
	public Crontab() {}
	
	public Crontab(Map<String, String> variables, List<Job> jobs) {
		super();
		this.variables = variables;
		this.jobs = jobs;
	}

	public Map<String, String> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, String> variables) {
		this.variables = variables;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}
}
