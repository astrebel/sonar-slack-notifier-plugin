package com.astrebel.sonarslack;

import java.text.MessageFormat;

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
		String channel = settings.getString(SlackNotifierPlugin.SLACK_CHANNEL);
		String slackUser = settings.getString(SlackNotifierPlugin.SLACK_SLACKUSER);
		
		if(hook == null) {
			return;
		}
		
		LOG.info("New notification: " + notification.toString());
		
		String defaultMessage = notification.getFieldValue("default_message");
		if(defaultMessage != null) {
		
			SlackMessage message = new SlackMessage(defaultMessage, slackUser);
			message.setChannel(channel);
			message.setServerBaseUrl(serverBaseUrl);
			message.setProjectKey(notification.getFieldValue("projectKey"));
			
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
				 String countBlockers = getOrDefaultZero(notification.getFieldValue("SEVERITY.BLOCKER.count"));
				 String countCriticals = getOrDefaultZero(notification.getFieldValue("SEVERITY.CRITICAL.count"));
				 String countMajors = getOrDefaultZero(notification.getFieldValue("SEVERITY.MAJOR.count"));
				 String countMinors = getOrDefaultZero(notification.getFieldValue("SEVERITY.MINOR.count"));
				 String countInfos = getOrDefaultZero(notification.getFieldValue("SEVERITY.INFO.count"));
				 
				 SlackAttachmentType type = SlackAttachmentType.WARNING;
				 if (!"00".equals(countCriticals + countBlockers)) {
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
	}
	
	private static String getOrDefaultZero(String value) {
		if (value == null || value.isEmpty()) { return "0"; }
		return value;
	}

}
