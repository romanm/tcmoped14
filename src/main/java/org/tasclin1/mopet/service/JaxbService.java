package org.tasclin1.mopet.service;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 * @author roman
 * 
 */
@Service("jaxbService")
@Repository
public class JaxbService {

    public PasteFromRepositoryForm onStart(Integer idt, Model model) {
	PasteFromRepositoryForm pasteFromRepositoryForm = new PasteFromRepositoryForm();
	return pasteFromRepositoryForm;
    }

    public void readJaxb() {
	HttpClient httpclient = new DefaultHttpClient();
	try {
	    // HttpGet httpget = new HttpGet("http://imsel.imise.uni-leipzig.de/");
	    HttpGet httpget = new HttpGet("http://localhost:8080/tcm14/xml=x_1339827");
	    System.out.println("executing request " + httpget.getURI());
	    // Create a response handler
	    ResponseHandler<String> responseHandler = new BasicResponseHandler();
	    String responseBody = httpclient.execute(httpget, responseHandler);
	    System.out.println(responseBody.length());
	    System.out.println("----------------------------------------");
	    System.out.println(responseBody);
	    System.out.println("----------------------------------------");
	} catch (ClientProtocolException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    // When HttpClient instance is no longer needed,
	    // shut down the connection manager to ensure
	    // immediate deallocation of all system resources
	    httpclient.getConnectionManager().shutdown();
	}
    }

    public static void main(String[] args) {
	JaxbService jaxbService = new JaxbService();
	jaxbService.readJaxb();
    }

}
