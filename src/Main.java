/**
 * Created by Jeff-Wang on 2016/6/12.
 */

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

public class Main {
		   
    private static void createAndShowGUI() {
        //Create and set up the window.              
        JFrame frame = new JFrame("Java Web Browser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Create and set up the content pane.
        frame.getContentPane().add(new WebBrowserFrame(), BorderLayout.CENTER);
        //Display the window.
        frame.setSize(800, 600);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void main(String[] args){
    	
        UIUtils.setPreferredLookAndFeel();
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	createAndShowGUI();
            }
        });
        NativeInterface.runEventPump();
    }
   
}
