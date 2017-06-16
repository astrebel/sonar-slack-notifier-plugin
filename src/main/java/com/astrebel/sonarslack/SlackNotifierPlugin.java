package com.astrebel.sonarslack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.config.PropertyFieldDefinition;

public class SlackNotifierPlugin extends SonarPlugin {
	
	public static final String SONAR_SERVER_BASE_URL = "sonar.core.serverBaseURL";
	
	public static final String SLACK_HOOK = "slack.hook";
	public static final String SLACK_SLACKUSER = "slack.slackuser";
	public static final String SLACK_CHANNELS_DEFAULT = "slack.channel";
	public static final String SLACK_PROJECTS = "slack.projects";
	public static final String SLACK_PROJECTS_KEY = "key";
	public static final String SLACK_PROJECTS_CHANNEL = "channel";
	
	public static final String SLACK_CATEGORY = "Slack";

	
	public SlackNotifierPlugin() {
		super();
	}
	
	public static List<PropertyDefinition> getSlackProperties() {
		return Arrays.asList(
				PropertyDefinition.builder(SLACK_SLACKUSER)
					.name("User")
					.description("User name shown in slack.")
					.category(SLACK_CATEGORY)
					.index(0)
					.type(PropertyType.STRING)
					.defaultValue("Sonar")
					.build(),
				PropertyDefinition.builder(SLACK_HOOK)
					.name("Slack Web Hook")
					.description("Slack web hook used to send notifications.")
					.category(SLACK_CATEGORY)
					.index(1)
					.type(PropertyType.STRING)
					.build(),
				PropertyDefinition.builder(SLACK_CHANNELS_DEFAULT)
					.name("Default Channel")
					.description("Default channel (#channel_name) where the notification should be sent to if no project specific channel applies.")
					.category(SLACK_CATEGORY)
					.index(2)
					.type(PropertyType.STRING)
					.build(),
				PropertyDefinition.builder(SLACK_PROJECTS)
					.name("Project Channels")
					.description("Project specific channels. If no project specific channel is configured for a project, the notification will be sent to the default channel.")
					.category(SLACK_CATEGORY)
					.index(3)
					.fields(
						PropertyFieldDefinition.build(SLACK_PROJECTS_KEY)
							.name("Project Key")
							.description("Ex: com.astrebel.sonarslack:sonar-slack-notifier-plugin")
							.type(PropertyType.STRING)
							.build(),
						PropertyFieldDefinition.build(SLACK_PROJECTS_CHANNEL)
							.name("Project Channel")
							.description("Channel to send project specific messages to")
							.type(PropertyType.STRING)
							.build())
					.build());
	}

	@Override
	public List<Object> getExtensions() {
		List<Object> extensions = new ArrayList<>();
		extensions.add(SlackNotificationChannel.class);
		extensions.add(SlackClient.class);
		extensions.addAll(getSlackProperties());
		return extensions;
	}
}
