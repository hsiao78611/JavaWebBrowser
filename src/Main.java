/**
 * Created by Jeff-Wang on 2016/6/12.
 */
import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args){
        UIUtils.setPreferredLookAndFeel();
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("DJ Native Swing Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new WebBrowserFrame(), BorderLayout.CENTER);
                frame.setSize(800, 600);
                frame.setLocationByPlatform(true);
                frame.setVisible(true);
            }
        });
        NativeInterface.runEventPump();
    }
}
