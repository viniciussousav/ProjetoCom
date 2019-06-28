package com.codebind;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class App {
    private JButton button1;
    private JPanel panel1;
    private JTextField textField1;
    private JList list;
    private JScrollPane scrollPane;
    private DefaultListModel listModel;



    private App() {

        listModel = new DefaultListModel();

        button1.addActionListener(actionEvent -> {
            if(!textField1.getText().isEmpty()){
                listModel.addElement(textField1.getText());
                list.setModel(listModel);
                textField1.setText("");
                //scrollPane.getVerticalScrollBar().setValue( scrollPane.getVerticalScrollBar().getMaximum() +1);
                SwingUtilities.invokeLater(() -> {
                    Dimension vpSize = scrollPane.getViewport().getExtentSize();
                    Dimension logSize = list.getSize();

                    int height = logSize.height - vpSize.height;

                    scrollPane.getViewport().setViewPosition(new Point(0, height));

                });

            }
        });

        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && !textField1.getText().isEmpty()) {
                    listModel.addElement(textField1.getText());
                    list.setModel(listModel);
                    textField1.setText("");
                    //scrollPane.getVerticalScrollBar().setValue( scrollPane.getVerticalScrollBar().getMaximum() +1);
                    SwingUtilities.invokeLater(() -> {
                        Dimension vpSize = scrollPane.getViewport().getExtentSize();
                        Dimension logSize = list.getSize();

                        int height = logSize.height - vpSize.height;

                        scrollPane.getViewport().setViewPosition(new Point(0, height));

                    });
                }
            }
        });
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().panel1);
        frame.setSize(1000, 1000);
        frame.setMinimumSize(new Dimension(500,500));
        frame.setMaximumSize(new Dimension(500,500));
        frame.setPreferredSize(new Dimension(500,500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
