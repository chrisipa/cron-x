package de.papke.cronx;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.papke.cronx.job.UnixJob;
import de.papke.cronx.model.Crontab;
import de.papke.cronx.model.Job;
import de.papke.cronx.util.CrontabParser;

public class Main {
	
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	private static final String APP_NAME = "cron-x";
	
	/**
	 * Program starts here
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		// create the Options
		Options options = new Options();
		options.addOption("c", "crontab-file", true, "Crontab file with job information");
		options.addOption("h", "help", false, "Print this help text");
		
		// create the command line parser
		CommandLineParser parser = new PosixParser();
		
		// parse the command line arguments
		CommandLine line = parser.parse(options, args);
		
		// get the values of the command line arguments
		String crontabFilePath = line.getOptionValue("c");
		
		if (!line.hasOption("h") && StringUtils.isNotEmpty(crontabFilePath)) {
			
			// get crontab file
			File crontabFile = new File(crontabFilePath);
			
			if (crontabFile.exists() && crontabFile.canRead()) {

				// create quartz scheduler
				Scheduler scheduler = new StdSchedulerFactory().getScheduler();
				
				// parse crontab file
				Crontab crontab = CrontabParser.parse(crontabFile);
				
				// log variables from crontab file
				log.info("Using crontab variables '" + crontab.getVariables() + "'");

				// iterate over jobs from crontab file
				for (Job job : crontab.getJobs()) {
					
					// logging
					log.info("Scheduling crontab job '" + job);
					
					// create quartz job detail
					JobDetail jobDetail = JobBuilder.newJob(UnixJob.class).withIdentity(job.toString()).build();
					
					// pass data for crontab job execution
					jobDetail.getJobDataMap().put(Constants.USERNAME, job.getUsername());
					jobDetail.getJobDataMap().put(Constants.COMMAND, job.getCommand());
					jobDetail.getJobDataMap().put(Constants.VARIABLES, crontab.getVariables());
					
					// create quartz trigger with date string
					Trigger trigger = TriggerBuilder
						.newTrigger()
						.withIdentity(job.toString())
						.withSchedule(
							CronScheduleBuilder.cronSchedule(job.getDate())
						)
						.build();
					
					// schedule job entries
					scheduler.scheduleJob(jobDetail, trigger);
				}
				
				// start quartz scheduler
				scheduler.start();
			}
			else {
				log.error("crontab file '" + crontabFile.getPath() + "' is missing");
			}
		}
		else {
			printHelp(options);
		}
	}
	
	/**
	 * Prints the help text
	 * 
	 * @param options
	 */
	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(APP_NAME, options);
	} 
}
