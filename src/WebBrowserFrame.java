/**
 * Created by Jeff-Wang on 2016/6/12.
 */
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowOpeningEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowWillOpenEvent;

@SuppressWarnings({ "unused", "serial" })
public class WebBrowserFrame extends JPanel{
		
    public WebBrowserFrame(){
        super(new BorderLayout());
        JPanel webBrowserPanel = new JPanel(new BorderLayout());
        webBrowserPanel.setBorder(BorderFactory.createTitledBorder("Native Web Browser component"));
        final JWebBrowser webBrowser = new JWebBrowser();
        
        // new a default tab pane which contains a web browser
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        webBrowser.setBarsVisible(true);
        webBrowser.setStatusBarVisible(true);
        webBrowser.navigate("http://www.google.com");
        addWebBrowserListener(tabbedPane, webBrowser); // add a listener for new tab
        tabbedPane.addTab("Startup page", webBrowser); // first page is a tab
        webBrowserPanel.add(tabbedPane, BorderLayout.CENTER);
//        webBrowserPanel.add(webBrowser, BorderLayout.CENTER);
        add(webBrowserPanel, BorderLayout.CENTER);
        // Create an additional bar allowing to show/hide the menu bar of the web browser.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        JCheckBox menuBarCheckBox = new JCheckBox("Menu Bar", webBrowser.isMenuBarVisible());
        menuBarCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                webBrowser.setMenuBarVisible(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
        buttonPanel.add(menuBarCheckBox);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    // for multiple Tab
    private static void addWebBrowserListener(final JTabbedPane tabbedPane, final JWebBrowser webBrowser) {
        webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
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
            e.getWebBrowser().setMenuBarVisible(false);
          }
        });
      }
}
