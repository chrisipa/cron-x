package de.papke.cronx.job;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.papke.cronx.Constants;
import de.papke.cronx.model.User;

@DisallowConcurrentExecution
public class UnixJob implements Job {

	private static final Logger log = LoggerFactory.getLogger(UnixJob.class);

	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext context) throws JobExecutionException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		String username = (String) context.getJobDetail().getJobDataMap().get(Constants.USERNAME);
		String command = (String) context.getJobDetail().getJobDataMap().get(Constants.COMMAND);
		Map<String, String> variables = (Map<String, String>) context.getJobDetail().getJobDataMap().get(Constants.VARIABLES);
		
		log.debug("Try " + getLogMessage(command, username));

		try {

			if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(command)) {

				CommandLine	commandLine = null;
				
				// must the user be switched for execution
				if (User.getName().equals(username)) {
					commandLine = new CommandLine("sh");
				}
				else {
					commandLine = new CommandLine("su");
					commandLine.addArgument("-");
					commandLine.addArgument(username);
				}
				
				commandLine.addArgument("-c");
				commandLine.addArgument(getCommand(command, variables), false);

				DefaultExecutor executor = new DefaultExecutor();
				PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
				executor.setStreamHandler(streamHandler);
				executor.execute(commandLine);
			}
		} 
		catch (Exception e) {
			log.error("Error while " + getLogMessage(command, username), e);
		}
		finally {
			log.debug("Output: " + outputStream.toString());
		}
	}
	
	private String getLogMessage(String command, String username) {
		return "executing crontab command '" + command + "' as user '" + username + "'";
	}
	
	private String getCommand(String command, Map<String, String> variables) {
		
		if (!variables.isEmpty()) {
			
			StringBuffer commandBuffer = new StringBuffer();
			
			for (String key : variables.keySet()) {
				String value = variables.get(key);
				commandBuffer.append("export ");
				commandBuffer.append(key);
				commandBuffer.append("=");
				commandBuffer.append(value);
				commandBuffer.append(" ; ");
			}
			
			commandBuffer.append(command);
			
			return commandBuffer.toString();
		}
		else {
			return command;
		}
	}
}
