package org.teckhooi.ninesquare;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Lim, Teck Hooi
 */
public class NumbersFrame {
    public NumbersFrame() {
        JFrame app = new JFrame("Numbers Frame");
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final Container appContent = app.getContentPane();
        appContent.setLayout(new GridLayout(4, 4));

        final JButton[] buttons = new JButton[16];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton(Integer.toString(i));
            appContent.add(buttons[i]);
        }

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
//                        appContent.removeAll();
                        int nextIndex = Integer.parseInt(buttons[buttons.length - 1].getText()) + 1;
                        for (int i = 0; i < buttons.length; i++) {
                            appContent.remove(buttons[i]);
                            buttons[i] = new JButton(nextIndex + i + "");
                            appContent.add(buttons[i]);
//                            buttons[i].setText(nextIndex + i + "");
                        }
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
        new NumbersFrame();
    }
}
