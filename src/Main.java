/**
 * Created by Jeff-Wang on 2016/6/12.
 */
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

@SuppressWarnings("unused")
public class Main {
		   
    private static void createAndShowGUI() {
        //Create and set up the window.              
        JFrame frame = new JFrame("DJ Native Swing Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Create and set up the content pane.
        frame.getContentPane().add(new WebBrowserFrame(), BorderLayout.CENTER);
        frame.getContentPane().add(new DisplayPanel(), BorderLayout.SOUTH);
        //Display the window.
        frame.setSize(800, 750);
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
