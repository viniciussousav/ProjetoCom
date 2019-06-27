package com.codebind;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.scene.input.KeyCode;
import org.w3c.dom.*;

import javax.swing.*;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.Text;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class App {
    private JButton button1;
    private JPanel panel1;
    private JTextField textField1;
    private JList list1;
    private DefaultListModel listModel;

    private App() {

        listModel = new DefaultListModel();

        button1.addActionListener(actionEvent -> {

            if(!textField1.getText().isEmpty()){
                listModel.addElement(textField1.getText());
                list1.setModel(listModel);
                textField1.setText("");
            }
        });

        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && !textField1.getText().isEmpty()){
                    listModel.addElement(textField1.getText());
                    list1.setModel(listModel);
                    textField1.setText("");
                }
            }
        });


    }

    public static void main(String[] args){
        JFrame frame = new JFrame("App");
        //frame.setSize(1000, 1000);
        frame.setContentPane(new App().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
