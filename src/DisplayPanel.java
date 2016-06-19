import java.awt.BorderLayout;
import java.time.ZonedDateTime;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;


@SuppressWarnings("serial")
public class DisplayPanel extends JPanel{
		
	private static JTextArea displayHistoryArea = new JTextArea(5, 20);
	private static JTextArea displayCookieArea = new JTextArea(5, 20);
    private static String historyText = "";
	
    public DisplayPanel(){
        super(new BorderLayout());
        JPanel displayPanel = new JPanel(new BorderLayout());        
        displayHistoryArea.setEditable(false);
        displayCookieArea.setEditable(false);
        JScrollPane scrollHistoryArea = new JScrollPane(displayHistoryArea);
        JScrollPane scrollCookieArea = new JScrollPane(displayCookieArea);
        
        // new a default tab pane which contains a JTextArea
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // add History tab
        tabbedPane.addTab("History", scrollHistoryArea);
        displayPanel.add(tabbedPane, BorderLayout.CENTER);
        add(displayPanel, BorderLayout.CENTER);    
        // add Cookie tab
        tabbedPane.addTab("Cookie", scrollCookieArea);
        displayPanel.add(tabbedPane, BorderLayout.CENTER);
        add(displayPanel, BorderLayout.CENTER);  
        
    }
    
    public static void setHistory(String history) {
    	historyText = historyText +ZonedDateTime.now()+" "+history+"\n";
    	displayHistoryArea.setText(historyText);
    }
	    
}
