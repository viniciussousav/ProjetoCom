package com.codebind;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class App {
    private JButton button1;
    private JPanel panel1;
    private JTextField textField1;
    private JList list;
    private JScrollPane scrollPane;
    private DefaultListModel listModel;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Socket socketCliente;
    private boolean send;


    private App() {

        send = false;
        socketCliente = new Socket();
        InetSocketAddress porta = new InetSocketAddress("172.20.4.174",12345);
        try {
            socketCliente.connect(porta);
            dataInputStream = new DataInputStream(socketCliente.getInputStream());
            dataOutputStream = new DataOutputStream(socketCliente.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        listModel = new DefaultListModel();

        button1.addActionListener(actionEvent -> {
            if(!textField1.getText().isEmpty()){
                listModel.addElement(textField1.getText());
                list.setModel(listModel);
//                try{
//                    dataOutputStream.writeUTF(textField1.getText());
//                } catch (Exception e) {
//                }
                String mensagem = textField1.getText();
                        System.out.println("Passou");

                        try {
                            dataOutputStream.writeUTF(mensagem);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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



        Thread receberMensagem = new Thread(() -> {
            while (true) {
                try {
                    String mensagem = dataInputStream.readUTF();
                    listModel.addElement(mensagem);
                    list.setModel(listModel);
                } catch (IOException e) {
                    break;
                }
                SwingUtilities.invokeLater(() -> {
                    Dimension vpSize = scrollPane.getViewport().getExtentSize();
                    Dimension logSize = list.getSize();
                    int height = logSize.height - vpSize.height;
                    scrollPane.getViewport().setViewPosition(new Point(0, height));

                });
            }
        });

        receberMensagem.start();
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

