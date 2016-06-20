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
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

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

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.DefaultWebBrowserDecorator;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowserWindow;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserDecorator;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowFactory;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowOpeningEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowWillOpenEvent;
import chrriis.dj.nativeswing.swtimpl.components.DefaultWebBrowserDecorator.WebBrowserButtonBar;
import chrriis.dj.nativeswing.swtimpl.components.DefaultWebBrowserDecorator.WebBrowserMenuBar;

@SuppressWarnings({ "unused", "serial" })
public class WebBrowserFrame extends JPanel{
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
        		buttonBar.add(buttonBar.getBackButton());
        		buttonBar.add(buttonBar.getForwardButton());
        		buttonBar.add(newTabButton);
        		buttonBar.add(historyButton);
        		buttonBar.add(cookieButton);
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
}
