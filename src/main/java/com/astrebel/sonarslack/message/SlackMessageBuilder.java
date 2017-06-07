package com.astrebel.sonarslack.message;

import java.text.MessageFormat;

public class SlackMessageBuilder {

	public String build(SlackMessage message) {
		StringBuilder builder = new StringBuilder();

		builder.append("{");
		if (message.getChannel() != null) {
			builder.append("\"channel\":\"");
			builder.append(message.getChannel());
			builder.append("\",");
		}
		builder.append("\"username\":\"");
		builder.append(message.getSlackUser());
		builder.append("\",");
		builder.append("\"text\":\"");
		builder.append(message.getShortText().replace("\n", "").replace("\r", ""));
		builder.append("\"");

		builder.append(buildAttachment(message.getAttachment()));
		builder.append("}");

		return builder.toString();
	}
	
	private String buildAttachment(SlackAttachment attachment) {
		if (attachment == null) { return ""; }
		
		String title = attachment.getTitle() != null ? attachment.getTitle().trim() : "";
		String reason = attachment.getReasons() != null ? attachment.getReasons().trim() : "";
		String text = buildText(title, reason);
		if (text.isEmpty()) { return ""; }
		
		return MessageFormat.format(",\"attachments\":['{'\"text\":\"{0}\",\"color\":\"{1}\",\"mrkdwn_in\": [\"text\"]'}']", text, attachment.getType());
	}
	
	private String buildText(String title, String reason) {
		if (!title.isEmpty() && ! reason.isEmpty()) { return MessageFormat.format("*{0}*\\n*Reason:*\\n{1}", title, reason); }
		if (!title.isEmpty()) { return MessageFormat.format("*{0}*", title); }
		if (!reason.isEmpty()) { return MessageFormat.format("*Reason:*\\n{0}", reason); }
		return "";
	}
}
