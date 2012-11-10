package org.tasclin1.mopet.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.tasclin1.mopet.domain.Drug;
import org.tasclin1.mopet.domain.Tree;
import org.tasclin1.mopet.jaxb.Drugx;

/**
 * @author Roman Mishchenko
 * 
 */
@Service("jaxbService")
@Repository
public class JaxbService {
    protected final Log log = LogFactory.getLog(getClass());

    public Tree pasteIdLoad(PasteFromRepositoryForm pasteFromRepositoryForm) {
	String pasteId = pasteFromRepositoryForm.getPasteId();
	log.debug("-------------" + pasteId);
	String pasteNodeId = pasteId.split("_")[1];
	int pasteNodeId2 = Integer.parseInt(pasteNodeId);
	// String readJaxb = readJaxb(pasteId);
	log.debug("-------------" + pasteNodeId2);
	Drugx drugx = loadDrugx(pasteNodeId2);
	Tree drugT = buildTree(drugx);
	log.debug("-------------" + drugT);
	return drugT;
    }

    public void log(String toLog) {
	log.debug(toLog);
    }

    public Tree buildTree(Drugx drugx) {
	log.debug(1);
	Tree tree = new Tree();
	log.debug(2);
	tree = new Tree();
	tree.setTabName("drug");
	tree.setMtlO(new Drug());
	tree.getDrugO().setDrug(drugx.getDrug());
	tree.getDrugO().setGeneric(new Drug());
	tree.getDrugO().getGeneric().setDrug(drugx.getGeneric());
	log.debug(3);
	return tree;
    }

    private Drugx loadDrugx(Integer pasteId) {
	Drugx drugx = null;
	JAXBContext newInstance;
	URL url;
	try {
	    newInstance = JAXBContext.newInstance(Drugx.class);
	    Unmarshaller createUnmarshaller = newInstance.createUnmarshaller();
	    url = new URL("http://localhost:8080/tcm14/xml=x_" + pasteId);
	    drugx = (Drugx) createUnmarshaller.unmarshal(url);
	    log.debug(drugx);
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	} catch (JAXBException e) {
	    e.printStackTrace();
	}
	return drugx;
    }

    public PasteFromRepositoryForm onStart(Integer idt, Model model) {
	PasteFromRepositoryForm pasteFromRepositoryForm = new PasteFromRepositoryForm();
	return pasteFromRepositoryForm;
    }

    public void readJaxb() {
	readJaxb(1347491);
    }

    private String readJaxb(Integer id) {
	String responseBody = null;
	HttpClient httpclient = new DefaultHttpClient();
	try {
	    // HttpGet httpget = new HttpGet("http://imsel.imise.uni-leipzig.de/");
	    HttpGet httpget = new HttpGet("http://localhost:8080/tcm14/xml=x_" + id);
	    System.out.println("executing request " + httpget.getURI());
	    // Create a response handler
	    ResponseHandler<String> responseHandler = new BasicResponseHandler();
	    responseBody = httpclient.execute(httpget, responseHandler);
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
	return responseBody;
    }

    public static void main(String[] args) {
	JaxbService jaxbService = new JaxbService();
	jaxbService.readJaxb();
    }

}
