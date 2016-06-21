import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestHistoryFeature {
	private HistoryXML xmlTest;
	private HistoryBean testBean;
	String testXML = "history.xml";
	String testDateTime, testLocation, testTitle;
	
	@Before
	public void setUp() throws Exception {
		xmlTest = new HistoryXML();
		xmlTest.history_Vector = new Vector<HistoryBean>();
		xmlTest.writeXMLFile("history.xml");
		testDateTime = "ZonedDateTime";
		testLocation = "pageLocation";
		testTitle = "pageTitle";
	}
	
	@After
	public void setDown() {
		xmlTest = null;
	}
	
	@Test
	public void testSetHistory() throws Exception {
		xmlTest.setHistory(testDateTime, testLocation, testTitle);
		testBean = (HistoryBean) xmlTest.history_Vector.get(xmlTest.history_Vector.size()-1);
		String setDateTime = testBean.getDateTime();
		String setLocation = testBean.getURL();
		String setTitle = testBean.getTitle();
		
		assertEquals(testDateTime, setDateTime);
		assertEquals(testLocation, setLocation);
		assertEquals(testTitle, setTitle);
	}
	
	@Test
	public void testWriteReadHistory() throws Exception {
		xmlTest.setHistory(testDateTime, testLocation, testTitle);
		xmlTest.writeXMLFile("history.xml");
		xmlTest.readXMLFile("history.xml");
		
		final String LS = System.getProperty("line.separator");
		String uponBody = "<html>" + LS +
						"  <body>" + LS;
		String htmlContent = "    "+testDateTime+
				" <a href=\""+testLocation+"\">"+testTitle+" &gt&gt "+testLocation+"</a><br/>" + LS;
		String belowBody = "  </body>" + LS +
							"</html>";
		assertEquals(uponBody+htmlContent+belowBody, xmlTest.printXMLFile());	
	}
}
