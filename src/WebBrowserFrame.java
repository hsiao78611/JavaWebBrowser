/**
 * Created by Jeff-Wang on 2016/6/12.
 */

import chrriis.dj.nativeswing.swtimpl.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Vector;

@SuppressWarnings({ "unused", "serial" })
public class WebBrowserFrame extends JPanel{

	private static HistoryXML historyXML = new HistoryXML();
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
            	JMenuItem clearHistory = new JMenuItem("Clear History");
            	clearHistory.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JFrame frame = new JFrame();
						ArrayList<String> addressList = Bookmark.getXMLValue();
						int dialogButton = JOptionPane.showConfirmDialog(null, "This action cannot be recovered! "
								+ "\nDo you want to clear the browsing history?", 
								"Note!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						if(dialogButton == JOptionPane.YES_OPTION){
							historyXML.history_Vector = new Vector<HistoryBean>();
							try {
								historyXML.writeXMLFile("history.xml");
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}	
					}
				});
            	myMenu.add(clearHistory);
            	menuBar.add(myMenu);

				//add bookmark to head bar
            	JMenu bookmarkMenu = new JMenu("[[Bookmark]]");
				JMenuItem showBookMark = new JMenuItem("Show Bookmark");
				showBookMark.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JFrame frame = new JFrame();
						ArrayList<String> addressList = Bookmark.getXMLValue();
						int dialogButton = JOptionPane.YES_NO_OPTION;
						String selectLink = JOptionPane.showInputDialog(frame,
								"Choose one", "Bookmark",
								JOptionPane.INFORMATION_MESSAGE, null,
								addressList.toArray(), addressList.get(0)).toString();
						if(dialogButton == JOptionPane.YES_OPTION){
							webBrowser.navigate(selectLink);
						}
					}
				});
				bookmarkMenu.add(showBookMark);
//				for(int i=0;i<bookmarkList.size();i++){
//					JMenuItem test = new JMenuItem(bookmarkList.get(i));
//					test.addActionListener(new ActionListener() {
//						@Override
//						public void actionPerformed(ActionEvent e) {
//							webBrowser.navigate(test.getText());
//						}
//					});
//					bookmarkMenu.add(test);
//				}
				JMenuItem manageBookMark = new JMenuItem("Delete Bookmark");
				manageBookMark.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						JFrame frame = new JFrame();
						ArrayList<String> addressList = Bookmark.getXMLValue();
						int dialogButton = JOptionPane.YES_NO_OPTION;
						String selectLink = JOptionPane.showInputDialog(null,
								"Pick a link to delete", "Delete",
								JOptionPane.WARNING_MESSAGE, null,
								addressList.toArray(), addressList.get(0)).toString();
						if(dialogButton == JOptionPane.YES_OPTION){
							Bookmark.deleteXMLElement(selectLink);
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
//        		final JButton cookieButton = new JButton("[[Cookie]]");
        		final JButton bookmarkButton = new JButton("[[BookMark]]");
        		
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
        		    	        historyXML.history_Vector = new Vector<HistoryBean>();
        						try {
        							historyXML.readXMLFile("history.xml");
            						webBrowser.setHTMLContent(historyXML.printXMLFile());
        						} catch (Exception e1) {
        							// TODO Auto-generated catch block
        							e1.printStackTrace();
        						}
        						
        						 // add a listener for new tab
        				        addWebBrowserListener(tabbedPane, webBrowser); 
        						tabbedPane.addTab("History", webBrowser); 
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
//        		cookieButton.addActionListener(new ActionListener() {
//        			public void actionPerformed(ActionEvent e) {
//        				JOptionPane.showMessageDialog(cookieButton, "Cookie Button was pressed!");
//        			}
//        		});
				bookmarkButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int dialogButton = JOptionPane.YES_NO_OPTION;
						int dialogResult = JOptionPane.showConfirmDialog(null, "Add this website to bookmark?", "Bookmark", dialogButton);
						if(dialogResult==JOptionPane.YES_OPTION){
							String address = webBrowser.getResourceLocation();
							Bookmark.addBookMark(address);
						}
					}
				});
        		buttonBar.add(buttonBar.getBackButton());
        		buttonBar.add(buttonBar.getForwardButton());
        		buttonBar.add(newTabButton);
        		buttonBar.add(historyButton);
//        		buttonBar.add(cookieButton);
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
				final String pageTitle = e.getWebBrowser().getPageTitle();
				final String pageLocation = e.getWebBrowser().getResourceLocation();
				DisplayPanel.setHistory(pageTitle); // only for test
				// record browsing history
				historyXML.history_Vector = new Vector<HistoryBean>();
				try {
					historyXML.readXMLFile("history.xml");
					historyXML.setHistory(ZonedDateTime.now().toString(), pageLocation, pageTitle);
					historyXML.writeXMLFile("history.xml");
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
}
