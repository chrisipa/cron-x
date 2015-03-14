package de.papke.cronx.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.papke.cronx.model.Crontab;
import de.papke.cronx.model.Job;
import de.papke.cronx.model.User;

public class CrontabParser {

	private static final Logger log = LoggerFactory.getLogger(CrontabParser.class);
	
	private static final String PASSWD_FILE_PATH = "/etc/passwd";
	
	private static final String COMMENT_SIGN = "#";
	private static final String VARIABLE_ASSIGNMENT_SIGN = "=";
	private static final String PASSWD_SEPARATOR_SIGN = ":";
	
	private static final String WHITESPACE_REGEX = "\\s+";
	private static final String LINE_BREAK_REGEX = "\\r?\\n";
	
	public static Crontab parse(File crontabFile) throws Exception {

		// init crontab variable
		Crontab crontab = new Crontab();
		
		// get the allowed users for execution
		List<String> allowedUsernameList = getAllowedUsernameList();
		
		// read crontab file to string
		StringWriter writer = new StringWriter();
		IOUtils.copy(new FileInputStream(crontabFile), writer);
		String crontabFileText = writer.toString();
		String[] crontabFileTextLines = crontabFileText.split(LINE_BREAK_REGEX);
		
		for (String line : crontabFileTextLines) {

			// trim line
			line = line.trim();

			// ignore empty lines and lines with comments
			if (StringUtils.isNotEmpty(line) && !line.startsWith(COMMENT_SIGN)) {

				String[] variableArray = line.split(VARIABLE_ASSIGNMENT_SIGN);
				
				// contains the line a variable declaration
				if (variableArray.length == 2) {
					crontab.getVariables().put(variableArray[0], variableArray[1]);
				}
				else {
					
					String[] entryArray = line.split(WHITESPACE_REGEX);

					if (entryArray.length > 1) {

						int usernameIndex = -1;

						// get array index of username
						for (int i = 0; i < entryArray.length; i++) {
							if (allowedUsernameList.contains(entryArray[i])) {
								usernameIndex = i;
								break;
							}
						}

						// username index found -> search for date and command information
						if (usernameIndex != -1) {

							String[] dateArray = Arrays.copyOfRange(entryArray, 0, usernameIndex);
							String[] commandArray = Arrays.copyOfRange(entryArray, usernameIndex + 1, entryArray.length);

							String username = entryArray[usernameIndex];
							String command = arrayToString(commandArray);

							String date = null;
							if (dateArray.length == 5) {
								dateArray[dateArray.length - 1] = "?";
								date = arrayToString(dateArray);
								date = "0 " + date;
							}
							else {
								date = arrayToString(dateArray);
							}
							
							crontab.getJobs().add(new Job(username, command, date));
						}
					}
				}
			}
		}
		
		return crontab;
	}
	
	private static List<String> getAllowedUsernameList() {

		List<String> allowedUsernameList = new ArrayList<String>();

		try {
			
			if (User.isRoot()) {
				
				StringWriter writer = new StringWriter();
				IOUtils.copy(new FileInputStream(PASSWD_FILE_PATH), writer);
				String passwdFileText = writer.toString();
				
				String[] passwdFileTextLines = passwdFileText.split(LINE_BREAK_REGEX);
				
				for (String line : passwdFileTextLines) {
					
					line = line.trim();
					
					if (StringUtils.isNotEmpty(line) && !line.startsWith(COMMENT_SIGN)) {
						allowedUsernameList.add(line.split(PASSWD_SEPARATOR_SIGN)[0]);
					}
				}
			}
			else {
				allowedUsernameList.add(User.getName());
			}
			
		} 
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return allowedUsernameList;
	}
	
	private static String arrayToString(String[] array) {

		StringBuffer arrayStringBuffer = new StringBuffer();

		for (int i = 0; i < array.length; i++) {
			arrayStringBuffer.append(array[i]);
			if (i < array.length - 1) {
				arrayStringBuffer.append(" ");
			}
		}

		return arrayStringBuffer.toString();
	}
}