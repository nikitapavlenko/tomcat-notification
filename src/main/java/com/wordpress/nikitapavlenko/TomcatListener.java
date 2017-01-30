package com.wordpress.nikitapavlenko;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;

public class TomcatListener implements LifecycleListener {

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        if (Lifecycle.AFTER_START_EVENT.equals(event.getType())) {
            if (growlExist()) {
                notifyViaGrowl();
            }
            else {
                notifyViaDefaultSystemTray();
            }
        }
    }

    private void notifyViaGrowl() {
        try {
            String message = "\"Server successfully started. [" + Calendar.getInstance().getTime()+"]\"";
            Runtime.getRuntime().exec("growlnotify /t:Tomcat " + message);
            System.out.println("Growl notification sent");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean growlExist() {
        Map<String, String> env = System.getenv();
        String path = env.get("Path");
        return path != null && path.toLowerCase().contains("growl");
    }

    private void notifyViaDefaultSystemTray() {
        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon trayIcon = createTrayIcon();
        try {
            tray.add(trayIcon);
            trayIcon.displayMessage("Tomcat", "Server successfully started.", MessageType.INFO);
            System.out.println("System tray notification sent");
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private TrayIcon createTrayIcon() {
        URL resource = getClass().getClassLoader().getResource("icon.png");
        Image image = Toolkit.getDefaultToolkit().createImage(resource);
        TrayIcon trayIcon = new TrayIcon(image, "Tomcat");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Hybris server");
        return trayIcon;
    }

}
