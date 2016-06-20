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

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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
	static JTabbedPane tabbedPane;

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
        tabbedPane = new JTabbedPane();
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
				JMenuItem manageBookMark = new JMenuItem("Delete Bookmark");
				manageBookMark.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JFrame frame = new JFrame();
						ArrayList<String> addressList = getXMLValue();
						int dialogButton = JOptionPane.YES_NO_OPTION;
						String selectLink = JOptionPane.showInputDialog(frame, "Pick a link to delete", "Delete a Bookmark", dialogButton,
								null, addressList.toArray(), "Titan").toString();
						if(dialogButton == JOptionPane.YES_OPTION){
							deleteXMLElement(selectLink);
						}
					}
				});
				bookmarkMenu.add(manageBookMark);
				menuBar.add(bookmarkMenu);

				//set action listener of bookmark



			}
        	@Override
        	protected void addButtonBarComponents(WebBrowserButtonBar buttonBar) {
        		// We completely override this method so we decide which buttons to add
        		final JButton newTabButton = new JButton("[[NewTab]]");
        		final JButton historyButton = new JButton("[[History]]");
        		final JButton cookieButton = new JButton("[[Cookie]]");

        		newTabButton.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent e) {
        				SwingUtilities.invokeLater(new Runnable() {
        					public void run() {
        						JWebBrowser webBrowser = new JWebBrowser() {
        		    	        	// set customized decorator
        		    	        	@Override
        		    	        	protected WebBrowserDecorator createWebBrowserDecorator(Component renderingComponent) {
        		    	            	return createCustomWebBrowserDecorator(this, renderingComponent);
        		    	    		}
        		    	        };
        		    	        // add a listener for new tab
        		    	        addWebBrowserListener(tabbedPane, webBrowser); 
        						tabbedPane.addTab("NewTab", webBrowser);   
        						for(int i=0; i<tabbedPane.getTabCount(); i++) {
        		    				if(tabbedPane.getComponentAt(i) == webBrowser) {
        		    					tabbedPane.setTabComponentAt(i, new ButtonTabComponent(tabbedPane)); // add close button
        		    					break;
        		    				}
        		    			}
        					}
        				});
        			}
        		});

				final JButton bookmarkButton = new JButton("[[BookMark]]");

        		final String LS = System.getProperty("line.separator");

        		historyButton.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent e) {
        				SwingUtilities.invokeLater(new Runnable() {
        					public void run() {
        						JWebBrowser webBrowser = new JWebBrowser() {
        		    	        	// set customized decorator
        		    	        	@Override
        		    	        	protected WebBrowserDecorator createWebBrowserDecorator(Component renderingComponent) {
        		    	            	return createCustomWebBrowserDecorator(this, renderingComponent);
        		    	    		}
        		    	        };
        						webBrowser.setHTMLContent(
                				        "<html>" + LS +
                				        "  <body>" + LS +
                				        "    <a href=\"http://java.sun.com\">http://java.sun.com</a><br/>" + LS +
                				        "    <a href=\"http://www.google.com\">http://www.google.com</a><br/>" + LS +
                				        "    <a href=\"http://www.eclipse.org\">http://www.eclipse.org</a><br/>" + LS +
                				        "    <a href=\"http://www.yahoo.com\">http://www.yahoo.com</a><br/>" + LS +
                				        "    <a href=\"http://www.microsoft.com\">http://www.microsoft.com</a><br/>" + LS +
                				        "  </body>" + LS +
                				        "</html>");
        						
        						 // add a listener for new tab
        				        addWebBrowserListener(tabbedPane, webBrowser); 
        						tabbedPane.addTab("History", webBrowser);      		            
        					}
        				});
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
        		buttonBar.add(newTabButton);
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
			public void locationChanging(WebBrowserNavigationEvent e) { // just for new resource location
				final String newResourceLocation = e.getNewResourceLocation();
				try {
					DisplayPanel.setHistory(newResourceLocation);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
    			JWebBrowser newWebBrowser = new JWebBrowser() {
    	        	// set customized decorator
    	        	@Override
    	        	protected WebBrowserDecorator createWebBrowserDecorator(Component renderingComponent) {
    	            	return createCustomWebBrowserDecorator(this, renderingComponent);
    	    		}
    	        };
    			addWebBrowserListener(tabbedPane, newWebBrowser);
    			tabbedPane.addTab("NewTab", newWebBrowser);
    			e.setNewWebBrowser(newWebBrowser);
    		}
    		@Override
    		public void windowOpening(WebBrowserWindowOpeningEvent e) {
//    			e.getWebBrowser().setMenuBarVisible(true);
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
		//need to find someway to reload the JPanel
		return getXMLElement;
	}

	private static void deleteXMLElement(String deleteLink){
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse("C:\\Users\\Jeff-Wang\\Documents\\bookmark.xml");
			NodeList nList = doc.getElementsByTagName("Data");
			for (int i = 0; i < nList.getLength(); i++)
			{
				Node nNode = nList.item(i);
				Element eElement = (Element) nNode;
				String address = eElement.getElementsByTagName("Address").item(0).getTextContent().toString();
				if(address.equals(deleteLink)){
					nNode.getParentNode().removeChild(eElement);
					break;
				}
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("C:\\Users\\Jeff-Wang\\Documents\\bookmark.xml"));
			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}
