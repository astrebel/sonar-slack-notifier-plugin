package com.astrebel.sonarslack;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.sonar.api.config.Settings;
import org.sonar.api.notifications.Notification;
import org.sonar.api.notifications.NotificationChannel;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import com.astrebel.sonarslack.message.SlackAttachment;
import com.astrebel.sonarslack.message.SlackAttachment.SlackAttachmentType;
import com.astrebel.sonarslack.message.SlackMessage;

public class SlackNotificationChannel extends NotificationChannel {
	private static final Logger LOG = Loggers.get(SlackNotificationChannel.class);
	
	private static final String PROPERTY_PROJECT_KEY = SlackNotifierPlugin.SLACK_PROJECTS + ".{0}." + SlackNotifierPlugin.SLACK_PROJECTS_KEY;
	private static final String PROPERTY_PROJECT_CHANNEL = SlackNotifierPlugin.SLACK_PROJECTS + ".{0}." + SlackNotifierPlugin.SLACK_PROJECTS_CHANNEL;
	
	private SlackClient slackClient;
	private Settings settings;

	public SlackNotificationChannel(SlackClient slackClient, Settings settings) {
		this.slackClient = slackClient;
		this.settings = settings;
	}

	@Override
	public void deliver(Notification notification, String user) {
		String serverBaseUrl = settings.getString(SlackNotifierPlugin.SONAR_SERVER_BASE_URL);
		String hook = settings.getString(SlackNotifierPlugin.SLACK_HOOK);
		String slackUser = settings.getString(SlackNotifierPlugin.SLACK_SLACKUSER);
		String defaultChannel = settings.getString(SlackNotifierPlugin.SLACK_CHANNELS_DEFAULT);
		Severity severityThreshold = Severity.valueOrDefault(settings.getString(SlackNotifierPlugin.SLACK_SEVERITY_THRESHOLD));
		
		if(hook == null) {
			return;
		}
		
		Map<String, ProjectConfiguration> projectConfigurations = readProjectConfigurations(settings);
		final String projectKey = notification.getFieldValue("projectKey");
		String channel = getProjectChannel(projectConfigurations, projectKey, defaultChannel);
		LOG.info(MessageFormat.format("New notification (Channel {0}, User: {1}): {2}", channel, slackUser, notification));
		
		String defaultMessage = notification.getFieldValue("default_message");
		if(defaultMessage == null) {
			return;
		}
		
		SlackMessage message = new SlackMessage(defaultMessage, slackUser);
		message.setProjectKey(projectKey);
		message.setChannel(channel);
		message.setServerBaseUrl(serverBaseUrl);
		
		if ("alerts".equals(notification.getType())) {
			 String alertLevel = notification.getFieldValue("alertLevel");
			 String alertName = notification.getFieldValue("alertName");
			 String alertText = notification.getFieldValue("alertText");
			 
			 SlackAttachmentType type = SlackAttachmentType.WARNING;
			 if("ERROR".equalsIgnoreCase(alertLevel)) {
				type = SlackAttachmentType.DANGER;
			 }
			 
			 SlackAttachment attachment = new SlackAttachment(type);
			 attachment.setTitle(alertName);
			 attachment.setReasons(alertText);
			 
			 message.setAttachment(attachment);
		 }
		 
		 if ("new-issues".equals(notification.getType())){
			 int countBlockers = getOrDefaultZero(notification.getFieldValue("SEVERITY.BLOCKER.count"));
			 int countCriticals = getOrDefaultZero(notification.getFieldValue("SEVERITY.CRITICAL.count"));
			 int countMajors = getOrDefaultZero(notification.getFieldValue("SEVERITY.MAJOR.count"));
			 int countMinors = getOrDefaultZero(notification.getFieldValue("SEVERITY.MINOR.count"));
			 int countInfos = getOrDefaultZero(notification.getFieldValue("SEVERITY.INFO.count"));
			 
			 Severity messageSeverity = Severity.messageSeverity(
					 countBlockers,
					 countCriticals,
					 countMajors,
					 countMinors,
					 countInfos);
			 
			 if (messageSeverity.compareTo(severityThreshold) < 0) {
				 return; // suppress message if severity is below threshold
			 }
			 
			 SlackAttachmentType type = SlackAttachmentType.WARNING;
			 if (countCriticals + countBlockers > 0) {
				 type = SlackAttachmentType.DANGER;
			 }
			 
			 SlackAttachment attachment = new SlackAttachment(type);
			 attachment.setTitle("New Rule Violations");
			 attachment.setReasons(MessageFormat.format(
					 "Blocker: {0}, Critical: {1}, Major: {2}, Minor: {3}, Info: {4}",
					 countBlockers,
					 countCriticals,
					 countMajors,
					 countMinors,
					 countInfos));
			 
			 message.setAttachment(attachment);
		 }
		
		slackClient.send(hook, message);
	}
	
	private String getProjectChannel(Map<String, ProjectConfiguration> projectConfigurations, String projectKey, String defaultChannel) {
		ProjectConfiguration projectConfig = projectConfigurations.get(projectKey);
		if (projectConfig != null) {
			return projectConfig.getChannel();
		}
		return defaultChannel;
	}

	private static int getOrDefaultZero(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	private static Map<String, ProjectConfiguration> readProjectConfigurations(Settings settings) {
		Map<String, ProjectConfiguration> projectConfigs = new HashMap<>();
		String[] projectIndexes = settings.getStringArray(SlackNotifierPlugin.SLACK_PROJECTS);
		LOG.info(MessageFormat.format("Project configurations: [{0}]", Arrays.toString(projectIndexes)));
		
		for (String projectIndex : projectIndexes) {
			String projectKey = settings.getString(getPropertyProjectKey(projectIndex));
			String projectChannel = settings.getString(getPropertyProjectChannel(projectIndex));
			if (projectKey == null || projectChannel == null) {
				LOG.info(MessageFormat.format("Invalid project configuration. Project key or channel name missing.Project Key = {0}, Channel name = {1}", projectKey, projectChannel));
				continue;
			}
			
			ProjectConfiguration projectConfig = new ProjectConfiguration(projectKey, projectChannel);
			LOG.info(MessageFormat.format("Found project configuration [key = {0}, channel={1}]", projectConfig.getKey(), projectConfig.getChannel()));
			projectConfigs.put(projectConfig.getKey(), projectConfig);
		}
		return projectConfigs;
	}
	
	private static String getPropertyProjectKey(String projectIndex) {
		return MessageFormat.format(PROPERTY_PROJECT_KEY, projectIndex);
	}
	
	private static String getPropertyProjectChannel(String projectIndex) {
		return MessageFormat.format(PROPERTY_PROJECT_CHANNEL, projectIndex);
	}
	
	private static final class ProjectConfiguration {
		private String key;
		private String channel;
		
		public ProjectConfiguration(String key, String channel) {
			this.key = key;
			this.channel = channel;
		}
		
		public String getKey() {
			return key;
		}
		
		public String getChannel() {
			return channel;
		}
	}
}
