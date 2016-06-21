/**
 * Created by Jeff-Wang on 2016/6/12.
 */

import chrriis.dj.nativeswing.swtimpl.components.*;
import sun.swing.ImageIconUIResource;

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
        
        // set the first tab
        SwingUtilities.invokeLater(new Runnable() {
			public void run() {
    	        // add a listener for new tab
    	        addWebBrowserListener(tabbedPane, webBrowser); 
				tabbedPane.addTab("NewTab", webBrowser);   
				// add close button
				for(int i=0; i<tabbedPane.getTabCount(); i++) {
    				if(tabbedPane.getComponentAt(i) == webBrowser) {
    					System.out.println(webBrowser.getPageTitle());
//    					tabbedPane.setTitleAt(index, title);
    					tabbedPane.setTabComponentAt(i, new ButtonTabComponent(tabbedPane)); 
    					break;
    				}
    			}
			}
		});
        
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
        		JMenu historyMenu = new JMenu("History");
        		
        		// add history menu item into head bar
        		JMenuItem showHistory = new JMenuItem("Show History");
        		showHistory.addActionListener(new ActionListener() {
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
            	historyMenu.add(showHistory);
            	historyMenu.add(clearHistory);
            	menuBar.add(historyMenu);
            	
            	// add bookmark menu item into head bar
            	JMenu bookmarkMenu = new JMenu("Bookmark");
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
				bookmarkMenu.add(showBookMark);
				bookmarkMenu.add(manageBookMark);
            	menuBar.add(bookmarkMenu);

			}
        	@Override
        	protected void addButtonBarComponents(WebBrowserButtonBar buttonBar) {
        		// set button icons
        		ImageIcon add, clock, star;
        		add = new ImageIcon("icon/add.gif");
        		clock = new ImageIcon("icon/clock.gif");
        		star = new ImageIcon("icon/star.png");
        		
        		// We completely override this method so we decide which buttons to add
        		final JButton newTabButton = new JButton(add);
        		final JButton historyButton = new JButton(clock);
        		final JButton bookmarkButton = new JButton(star);
        		
        		// new tab
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
        						// add close button
        						for(int i=0; i<tabbedPane.getTabCount(); i++) {
        		    				if(tabbedPane.getComponentAt(i) == webBrowser) {
        		    					tabbedPane.setTabComponentAt(i, new ButtonTabComponent(tabbedPane)); 
        		    					break;
        		    				}
        		    			}
        					}
        				});
        			}
        		});
        		
        		// show history
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

        		// add bookmark
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
				final String pageTitle = e.getWebBrowser().getPageTitle();
				final String pageLocation = e.getWebBrowser().getResourceLocation();
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
    					// add close button
    					tabbedPane.setTabComponentAt(i, new ButtonTabComponent(tabbedPane));
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
