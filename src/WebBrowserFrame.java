/**
 * Created by Jeff-Wang on 2016/6/12.
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.WebBrowserObject;
import chrriis.dj.nativeswing.swtimpl.components.*;
import chrriis.dj.nativeswing.swtimpl.components.DefaultWebBrowserDecorator.WebBrowserButtonBar;
import chrriis.dj.nativeswing.swtimpl.components.DefaultWebBrowserDecorator.WebBrowserMenuBar;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

@SuppressWarnings({ "unused", "serial" })
public class WebBrowserFrame extends JPanel{

	private static JMenu bookmarkMenu;

    public WebBrowserFrame(){
        super(new BorderLayout());
        JPanel webBrowserPanel = new JPanel(new BorderLayout());        
        final JWebBrowser webBrowser = new JWebBrowser(){
        	// set customized decorator
        	@Override
        	protected WebBrowserDecorator createWebBrowserDecorator(Component renderingComponent) {
            	return createCustomWebBrowserDecorator(this, renderingComponent);
    		}
        };
        
        // new a default tab pane which contains a web browser
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // set bars
        webBrowser.setBarsVisible(true); // feature bar
        webBrowser.setMenuBarVisible(true);
        webBrowser.setStatusBarVisible(true);
        
        // add a listener for new tab
        addWebBrowserListener(tabbedPane, webBrowser); 

        // add a default tab
        webBrowser.navigate("http://www.google.com");
        tabbedPane.addTab("Startup page", webBrowser); // first page is a tab
        webBrowserPanel.add(tabbedPane, BorderLayout.CENTER);
        add(webBrowserPanel, BorderLayout.CENTER);
    }
    
    // for customer button and menu item
    private static WebBrowserDecorator createCustomWebBrowserDecorator(JWebBrowser webBrowser, Component renderingComponent) {
        // Let's extend the default decorator.
        // We could rewrite our own decorator, but this is far more complex and we generally do not need this.
        return new DefaultWebBrowserDecorator(webBrowser, renderingComponent) {
        	@Override
        	protected void addMenuBarComponents(WebBrowserMenuBar menuBar) {
        		// We let the default menus to be added and then we add ours.
        		super.addMenuBarComponents(menuBar);
        		JMenu myMenu = new JMenu("[[Features]]");
            	myMenu.add(new JMenuItem("History"));
            	myMenu.add(new JMenuItem("Cookie"));
            	menuBar.add(myMenu);

				//add bookmark to head bar
				bookmarkMenu = new JMenu("[[bookMark]]");
				ArrayList<String> bookmarkList = getXMLValue();
				for(int i=0;i<bookmarkList.size();i++){
					JMenuItem test = new JMenuItem(bookmarkList.get(i));
					test.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							webBrowser.navigate(test.getText());
						}
					});
					bookmarkMenu.add(test);
				}
				bookmarkMenu.add(new JMenuItem("Manage Bookmark"));
				menuBar.add(bookmarkMenu);

				//set action listener of bookmark



			}
        	@Override
        	protected void addButtonBarComponents(WebBrowserButtonBar buttonBar) {
        		// We completely override this method so we decide which buttons to add
        		final JButton historyButton = new JButton("[[History]]");
        		final JButton cookieButton = new JButton("[[Cookie]]");
				final JButton bookmarkButton = new JButton("[[BookMark]]");
        		historyButton.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent e) {
//        				JOptionPane.showMessageDialog(historyButton, "History Button was pressed!");
        				webBrowser.navigate("http://www.google.com");
        			}
        		});
        		cookieButton.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent e) {
        				JOptionPane.showMessageDialog(cookieButton, "Cookie Button was pressed!");
        			}
        		});
				bookmarkButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int dialogButton = JOptionPane.YES_NO_OPTION;
						int dialogResult = JOptionPane.showConfirmDialog(null, "Add this website to bookmark?", "Bookmark", dialogButton);
						if(dialogResult==JOptionPane.YES_OPTION){
							String address = webBrowser.getResourceLocation();
							addBookMark(address);
						}
					}
				});
        		buttonBar.add(buttonBar.getBackButton());
        		buttonBar.add(buttonBar.getForwardButton());
        		buttonBar.add(historyButton);
        		buttonBar.add(cookieButton);
				buttonBar.add(bookmarkButton);
        		buttonBar.add(buttonBar.getReloadButton());
        		buttonBar.add(buttonBar.getStopButton());
        	}
        };
    }
    
    // for multiple Tab
    private static void addWebBrowserListener(final JTabbedPane tabbedPane, final JWebBrowser webBrowser) {
    	webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
    		@Override
			public void locationChanging(WebBrowserNavigationEvent e) {
				final String newResourceLocation = e.getNewResourceLocation();
				DisplayPanel.setHistory(newResourceLocation);
    		}
    		@Override
    		public void titleChanged(WebBrowserEvent e) {
    			for(int i=0; i<tabbedPane.getTabCount(); i++) {
    				if(tabbedPane.getComponentAt(i) == webBrowser) {
    					if(i == 0) {
    						return;
    					}
    					tabbedPane.setTitleAt(i, webBrowser.getPageTitle());
    					tabbedPane.setTabComponentAt(i, new ButtonTabComponent(tabbedPane)); // add close button
    					break;
    				}
    			}
    		}
    		@Override
    		public void windowWillOpen(WebBrowserWindowWillOpenEvent e) {
    			JWebBrowser newWebBrowser = new JWebBrowser();
    			addWebBrowserListener(tabbedPane, newWebBrowser);
    			tabbedPane.addTab("New Tab", newWebBrowser);
    			e.setNewWebBrowser(newWebBrowser);
    		}
    		@Override
    		public void windowOpening(WebBrowserWindowOpeningEvent e) {
    			e.getWebBrowser().setMenuBarVisible(true);
    		}
    	});
    }

	private static void addBookMark(String address){
		try {

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			File f = new File("C:\\Users\\Jeff-Wang\\Documents\\bookmark.xml");
			if(f.exists() && !f.isDirectory()) {
				Document document = documentBuilder.parse("C:\\Users\\Jeff-Wang\\Documents\\bookmark.xml");

				Element root = document.getDocumentElement();

				// add elements
				Element newData = document.createElement("Data");

				Element name = document.createElement("Address");
				name.appendChild(document.createTextNode(address));
				newData.appendChild(name);

				root.appendChild(newData);

				DOMSource source = new DOMSource(document);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				StreamResult result = new StreamResult("C:\\Users\\Jeff-Wang\\Documents\\bookmark.xml");
				transformer.transform(source, result);
			}else{
				createXMLForBookmark(address);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createXMLForBookmark(String address){
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// Root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Bookmark");
			doc.appendChild(rootElement);

			// bookmark element
			Element data = doc.createElement("Data");
			rootElement.appendChild(data);

			// Address elements into data Element
			Element link = doc.createElement("Address");
			link.appendChild(doc.createTextNode(address));
			data.appendChild(link);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("C:\\Users\\Jeff-Wang\\Documents\\bookmark.xml"));
			transformer.transform(source, result);
			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();

		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	private static ArrayList<String> getXMLValue(){
		ArrayList<String> getXMLElement = new ArrayList<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse("C:\\Users\\Jeff-Wang\\Documents\\bookmark.xml");
			NodeList nList = document.getElementsByTagName("Data");
			for (int i = 0; i < nList.getLength(); i++)
			{
				Node nNode = nList.item(i);
				Element eElement = (Element) nNode;
				String address = eElement.getElementsByTagName("Address").item(0).getTextContent().toString();
				getXMLElement.add(address);
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		return getXMLElement;
	}
}
