package com.speech;

import java.sql.Timestamp;
import java.util.Date;

public class Response {
	private String body;
	private Timestamp timestamp;

	public Response(String body) {
		Date date = new Date();
		long time = date.getTime();
		this.timestamp = new Timestamp(time);
		this.body = body;

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Response [body= ");
		builder.append(body);
		builder.append(", timestamp= ");
		builder.append(timestamp);
		builder.append("]");
		return builder.toString();
	}

	public String getBody() {
		return body;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}
	
	

}
