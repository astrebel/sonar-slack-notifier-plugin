package com.astrebel.sonarslack.message;

public class SlackMessage {

	private String channel;
	private String slackUser;
	private String serverBaseUrl;
	private String projectKey;
	private String shortText;
	private SlackAttachment attachment;
	
	public SlackMessage(String shortText, String slackUser) {
		this.shortText = shortText;
		this.slackUser = slackUser;
	}
	
	public SlackMessage(String channel, String slackUser, String shortText, SlackAttachment attachment) {
		super();
		this.channel = channel;
		this.slackUser = slackUser;
		this.shortText = shortText;
		this.attachment = attachment;
	}
	
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public void setServerBaseUrl(String serverBaseUrl) {
		this.serverBaseUrl = serverBaseUrl;
	}
	public String getServerBaseUrl() {
		return serverBaseUrl;
	}
	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}
	public String getProjectKey() {
		return projectKey;
	}
	public String getSlackUser() {
		return slackUser;
	}
	public void setSlackUser(String slackUser) {
		this.slackUser = slackUser;
	}
	public String getShortText() {
		return shortText;
	}
	public void setShortText(String shortText) {
		this.shortText = shortText;
	}
	public SlackAttachment getAttachment() {
		return attachment;
	}
	public void setAttachment(SlackAttachment attachment) {
		this.attachment = attachment;
	}
}
