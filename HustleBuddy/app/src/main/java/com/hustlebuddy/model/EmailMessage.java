package com.hustlebuddy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class EmailMessage {

    private int emailId;
    private String from;
    private String to;
    private String subject;
    private String message;

    public EmailMessage(String to, String subject, String message)
    {
        this.emailId = 0;
        this.from = "autoreply.hustlebuddy@gmail.com";
        this.to = to;
        this.subject = subject;
        this.message = message;
    }

    public EmailMessage() {
    }

    public static JSONObject toJSONObject(EmailMessage emailMessage) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("emailId", emailMessage.getEmailId());
        jsonObject.put("from", emailMessage.getFrom());
        jsonObject.put("to", emailMessage.getTo());
        jsonObject.put("subject", emailMessage.getSubject());
        jsonObject.put("message", emailMessage.getMessage());
        return  jsonObject;
    }

    public int getEmailId() {
        return emailId;
    }

    public void setEmailId(int emailId) {
        this.emailId = emailId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
