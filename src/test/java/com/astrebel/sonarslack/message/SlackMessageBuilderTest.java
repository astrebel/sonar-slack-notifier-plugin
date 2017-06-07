package com.astrebel.sonarslack.message;

import org.junit.Before;
import org.junit.Test;

import com.astrebel.sonarslack.message.SlackAttachment;
import com.astrebel.sonarslack.message.SlackAttachment.SlackAttachmentType;
import com.astrebel.sonarslack.message.SlackMessage;
import com.astrebel.sonarslack.message.SlackMessageBuilder;

import static org.junit.Assert.assertEquals;

public class SlackMessageBuilderTest {
	
	private SlackMessageBuilder messageBuilder;
	
	@Before
	public void setUp() {
		messageBuilder = new SlackMessageBuilder();
	}

	@Test
	public void testBuildSimpleMessage() {
		SlackMessage message = new SlackMessage("This is a test", "Sonar");
		
		String result = messageBuilder.build(message);
		assertEquals("Wrong message result", "{\"username\":\"Sonar\",\"text\":\"This is a test\"}", result);
	}
	
	@Test
	public void testBuildAttachmentMessage() {
		SlackMessage message = new SlackMessage("This is a test", "Sonar");
		SlackAttachment attachment = new SlackAttachment(SlackAttachmentType.WARNING);
		attachment.setTitle("TestAlert");
		attachment.setReasons("This is a test alert");
		message.setAttachment(attachment);
		
		String result = messageBuilder.build(message);
		
		String expected = "{\"username\":\"Sonar\",\"text\":\"This is a test\",\"attachments\":["
				+ "{\"text\":\"*TestAlert*\\n*Reason:*\\nThis is a test alert\",\"color\":\"warning\",\"mrkdwn_in\": [\"text\"]}]}";
		assertEquals("Wrong message result", expected, result);
	}
	
	@Test
	public void testBuildAttachmentMessageMultipleReasons() {
		SlackMessage message = new SlackMessage("This is a test", "Sonar");
		SlackAttachment attachment = new SlackAttachment(SlackAttachmentType.WARNING);
		attachment.setTitle("TestAlert");
		attachment.setReasons("This is a test alert, This is another test alert");
		message.setAttachment(attachment);
		
		String result = messageBuilder.build(message);
		
		String expected = "{\"username\":\"Sonar\",\"text\":\"This is a test\",\"attachments\":["
				+ "{\"text\":\"*TestAlert*\\n*Reason:*\\n- This is a test alert\\n- This is another test alert\",\"color\":\"warning\",\"mrkdwn_in\": [\"text\"]}]}";
		assertEquals("Wrong message result", expected, result);
	}

	@Test
	public void testBuildAttachmentMessageWithoutReason() {
		SlackMessage message = new SlackMessage("This is a test", "Sonar");
		SlackAttachment attachment = new SlackAttachment(SlackAttachmentType.WARNING);
		attachment.setTitle("TestAlert");
		attachment.setReasons("   "); // no reason here
		message.setAttachment(attachment);
		
		String result = messageBuilder.build(message);
		
		String expected = "{\"username\":\"Sonar\",\"text\":\"This is a test\",\"attachments\":["
				+ "{\"text\":\"*TestAlert*\",\"color\":\"warning\",\"mrkdwn_in\": [\"text\"]}]}";
		assertEquals("Wrong message result", expected, result);
	}
	
	@Test
	public void testBuildAttachmentMessageWithoutTitle() {
		SlackMessage message = new SlackMessage("This is a test", "Sonar");
		SlackAttachment attachment = new SlackAttachment(SlackAttachmentType.WARNING);
		attachment.setTitle("   "); // no title here
		attachment.setReasons("This is a test alert, This is another test alert");
		message.setAttachment(attachment);
		
		String result = messageBuilder.build(message);
		
		String expected = "{\"username\":\"Sonar\",\"text\":\"This is a test\",\"attachments\":["
				+ "{\"text\":\"*Reason:*\\n- This is a test alert\\n- This is another test alert\",\"color\":\"warning\",\"mrkdwn_in\": [\"text\"]}]}";
		assertEquals("Wrong message result", expected, result);
	}
	
	@Test
	public void testBuildAttachmentMessageWithoutTitleAndWithoutReason() {
		SlackMessage message = new SlackMessage("This is a test", "Sonar");
		SlackAttachment attachment = new SlackAttachment(SlackAttachmentType.WARNING);
		attachment.setTitle("   "); // no title here
		attachment.setReasons("   "); // no reason here
		message.setAttachment(attachment);
		
		String result = messageBuilder.build(message);
		
		String expected = "{\"username\":\"Sonar\",\"text\":\"This is a test\"}";
		assertEquals("Wrong message result", expected, result);
	}

	@Test
	public void testBuildMessageWithChannel() {
		SlackMessage message = new SlackMessage("This is a test", "Sonar");
		message.setChannel("TestChannel");
		
		String result = messageBuilder.build(message);
		assertEquals("Wrong message result", "{\"channel\":\"TestChannel\",\"username\":\"Sonar\",\"text\":\"This is a test\"}", result);
	}
	
	@Test
	public void testBuildMessageWithUser() {
		SlackMessage message = new SlackMessage("This is a test", "Sonar");
		message.setSlackUser("TestUser");
		
		String result = messageBuilder.build(message);
		assertEquals("Wrong message result", "{\"username\":\"TestUser\",\"text\":\"This is a test\"}", result);
	}
}