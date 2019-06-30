package com.codebind;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class App {
    private JButton button1;
    private JPanel panel1;
    private JTextField textField1;
    private JList list;
    private JScrollPane scrollPane;
    private JButton delButton;
    private DefaultListModel<String> listModel;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Socket socketCliente;

    private App() {

        socketCliente = new Socket();
        InetSocketAddress porta = new InetSocketAddress("localhost",12345);
        try {
            socketCliente.connect(porta);
            dataInputStream = new DataInputStream(socketCliente.getInputStream());
            dataOutputStream = new DataOutputStream(socketCliente.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        listModel = new DefaultListModel<>();
        listModel.addElement("Bem vindo, por favor insira seu nome...");
        list.setModel(listModel);


        //enviar mensagem clicando botão
        button1.addActionListener(actionEvent -> {

             if(!textField1.getText().isEmpty()){
                try {
                    dataOutputStream.writeUTF(textField1.getText());
                    textField1.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //scrollPane.getVerticalScrollBar().setValue( scrollPane.getVerticalScrollBar().getMaximum() +1);
                SwingUtilities.invokeLater(() -> {
                    Dimension vpSize = scrollPane.getViewport().getExtentSize();
                    Dimension logSize = list.getSize();

                    int height = logSize.height - vpSize.height;

                    scrollPane.getViewport().setViewPosition(new Point(0, height));

                });
            }
        });

        delButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int index = list.getSelectedIndex();

                if(listModel.elementAt(index).charAt(0) == 'V' && listModel.elementAt(index).contains("Você: ")){
                    try {
                        dataOutputStream.writeUTF("COMMAND=DELETE" + listModel.elementAt(index));
                    } catch (Exception a){

                    }
                } else {
                    listModel.remove(list.getSelectedIndex());
                }
            }
        });

        //enviar mensagem apertando enter
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && !textField1.getText().isEmpty()) {
                    try {
                        dataOutputStream.writeUTF(textField1.getText());
                        textField1.setText("");
                    } catch (IOException a) {
                            a.printStackTrace();
                        }
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
                    listModel.addElement(dataInputStream.readUTF());
                    list.setModel(listModel);
                    for (int i = 0; i < listModel.size(); i++){
                        System.out.print(listModel.elementAt(i) + " ");
                        System.out.println();
                    }
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
        frame.setResizable(false);
        frame.setVisible(true);
    }
}

