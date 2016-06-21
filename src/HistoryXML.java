
import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import java.util.Vector;

public class HistoryXML {
	Vector<HistoryBean> history_Vector;
	
	void setHistory(String zonedDateTime, String url, String title) throws Exception {
		HistoryBean historyBean = new HistoryBean();
		historyBean.setDateTime(zonedDateTime);
		historyBean.setURL(url);
		historyBean.setTitle(title);
		history_Vector.add(historyBean);
	}
	
	void readXMLFile(String inFile) throws Exception {
		history_Vector.clear();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			System.err.println(pce);
			System.exit(1);
		}
		
		Document doc = null;
		try {
			doc = db.parse(inFile);
		} catch (DOMException dom) {
			System.err.println(dom.getMessage());
			System.exit(1);
		} catch (IOException ioe) {
			System.err.println(ioe);
			System.exit(1);
		}
		
		// parse XML file
		// get root element
		Element root = doc.getDocumentElement();
		NodeList historyList = root.getElementsByTagName("History");
		for (int i = 0; i < historyList.getLength(); i++) {
			// get each history information
			Element history = (Element) historyList.item(i);
			// new a HistoryBean
			HistoryBean historyBean = new HistoryBean();
			// historyBean.setAttributeMethod(history.getAttribute("AttributeName"));
			// get elements
			NodeList zonedDateTime = history.getElementsByTagName("ZonedDateTime");
			if (zonedDateTime.getLength() == 1) {
				Element e = (Element) zonedDateTime.item(0);
				Text t = (Text) e.getFirstChild();
				historyBean.setDateTime(t.getNodeValue());
			}
			NodeList url = history.getElementsByTagName("URL");
			if (url.getLength() == 1) {
				Element e = (Element) url.item(0);
				Text t = (Text) e.getFirstChild();
				historyBean.setURL(t.getNodeValue());
			}
			NodeList title = history.getElementsByTagName("Title");
			if (title.getLength() == 1) {
				Element e = (Element) title.item(0);
				Text t = (Text) e.getFirstChild();
				if (t==null)
					historyBean.setTitle("null");
				else
					historyBean.setTitle(t.getNodeValue());
			}
			history_Vector.add(historyBean);
		}
	}
	
	void writeXMLFile(String outFile) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			System.err.println(pce);
			System.exit(1);
		}
		Document doc = null;
		doc = db.newDocument();
		
		// build XML file
		// add root element
		Element root = doc.createElement("HistoryList");
		doc.appendChild(root);
		for (int i = 0; i < history_Vector.size(); i++) {
			// get each history information
			HistoryBean historyBean = (HistoryBean) history_Vector.get(i);
			if (historyBean.getURL().equals("about:blank") || historyBean.getTitle().equals("about:blank"))
				continue;
			else if (i>0) {
				HistoryBean previousBean = (HistoryBean) history_Vector.get(i-1);
				if (historyBean.getURL().equals(previousBean.getURL()))
					continue;
			}
			// add element "history" which belongs to the root element 
			Element history = doc.createElement("History");
			// history.setAttribute("AttributeName", historyBean.getAttributeMethod());
			root.appendChild(history);
			
			// add elements which belong to the element "History"
			Element zonedDateTime = doc.createElement("ZonedDateTime");
			history.appendChild(zonedDateTime);
			Text tDateTime = doc.createTextNode(historyBean.getDateTime());
			zonedDateTime.appendChild(tDateTime);
			Element url = doc.createElement("URL");
			history.appendChild(url);
			Text tURL = doc.createTextNode(historyBean.getURL());
			url.appendChild(tURL);
			Element title = doc.createElement("Title");
			history.appendChild(title);
			Text tTitle = doc.createTextNode(historyBean.getTitle());
			title.appendChild(tTitle);
		}
		
	// write the content into xml file
	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();
	DOMSource source = new DOMSource(doc);
	StreamResult result = new StreamResult(new File(outFile));
	transformer.transform(source, result);
	}
	
	public String printXMLFile() throws Exception {
		final String LS = System.getProperty("line.separator");
		String uponBody = "<html>" + LS +
						"  <body>" + LS;
		String htmlContent = "";
		String belowBody = "  </body>" + LS +
							"</html>";
		for (int i = 0; i < history_Vector.size(); i++) {
			// get each history information
			HistoryBean historyBean = (HistoryBean) history_Vector.get(i);
			htmlContent = htmlContent +"    "+historyBean.getDateTime()+
					" <a href=\""+historyBean.getURL()+"\">"+historyBean.getTitle()+" &gt&gt "+historyBean.getURL()+"</a><br/>" + LS;
			
		}
		return uponBody+htmlContent+belowBody;
	}
	
}
