package com.parse4cn1.command;

import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import com.parse4cn1.Parse;
import com.parse4cn1.ParseConstants;
import com.parse4cn1.callback.ProgressCallback;
import com.parse4cn1.http.CountingHttpEntity;

public class ParseUploadCommand extends ParseCommand {

	private String endPoint;
	private String contentType;
	private byte[] data;
	private ProgressCallback progressCallback;

	public ParseUploadCommand(String endPoint) {
		this.endPoint = endPoint;
	}

	@Override
	public HttpRequestBase getRequest() throws IOException {
		String url = Parse.getParseAPIUrl(endPoint);
		System.out.println(url);
		HttpPost httppost = new HttpPost(url);
		setupHeaders(httppost, false);
		
		if(contentType != null) {
			httppost.addHeader(ParseConstants.HEADER_CONTENT_TYPE, contentType);
		}

		System.out.println("data size: " + data.length);
		if (data != null) {
			if(progressCallback != null) {
				httppost.setEntity(new CountingHttpEntity(new ByteArrayEntity(data), progressCallback));
			}
			else {
				httppost.setEntity(new ByteArrayEntity(data));
			}
		}
		return httppost;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public void setProgressCallback(ProgressCallback progressCallback) {
		this.progressCallback = progressCallback;
	}
	
	/*
	@Override
	public ParseResponse perform() throws ParseException {
		
		try {
			HttpClient httpclient = createSingleClient();
			HttpResponse httpResponse = httpclient.execute(getRequest());
			ParseResponse response = new ParseResponse(httpResponse);
			
			return response;
		}
		catch (ClientProtocolException e) {
			throw ParseResponse.getConnectionFailedException(e.getMessage());
		} 
		catch (IOException e) {
			throw ParseResponse.getConnectionFailedException(e.getMessage());
		}
	}
	*/

}
