package org.teckhooi.ninesquare;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Lim, Teck Hooi
 */
public class ClickMeFrame {
    private JButton clickMe = new JButton("1");

    public ClickMeFrame() {
        JFrame app = new JFrame("Numbers Frame");
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        final Container appContent = app.getContentPane();
        clickMe.setSize(new Dimension(20, 20));
        appContent.add(clickMe);

        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        menuBar.add(file);

        AbstractAction refreshAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Refresh");
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int nextIndex = Integer.parseInt(clickMe.getText()) + 1;
                        // remove from here
                        appContent.remove(clickMe);
                        clickMe = new JButton(nextIndex + "");
                        appContent.add(clickMe);
                        // to here to experiment with button text change instead of replacing with a new JButton

                        // enable me to experiment with button text change instead
//                        clickMe.setText(nextIndex + "");
                        appContent.revalidate();
                        appContent.repaint();
                    }
                });
            }
        };

        JMenuItem refresh = new JMenuItem("Refresh");
        refresh.setAccelerator(KeyStroke.getKeyStroke("F3"));
        refresh.addActionListener(refreshAction);
        file.add(refresh);
        app.setJMenuBar(menuBar);
        app.pack();
        app.setVisible(true);
    }

    public static void main(String[] args) {
        new ClickMeFrame();
    }
}
