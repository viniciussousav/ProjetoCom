package com.codebind;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private boolean conectado;

    private App() {
        listModel = new DefaultListModel<>();
        list.setModel(listModel);
        conectado = false;

        Thread receberMensagem = new Thread(() -> {
            while (true) {
                try {

                    String mensagem = dataInputStream.readUTF();

                    if(mensagem.contains("COMMAND=DELETE:")){
                        mensagem = mensagem.replace("COMMAND=DELETE:", "" );
                        boolean stop = false;
                        for (int i = 0; i < listModel.size() && !stop; i++) {
                            if(mensagem.contains(listModel.elementAt(i))) {
                                listModel.setElementAt("MENSAGEM APAGADA PELO REMETENTE",i);
                                stop = true;
                            }
                        }
                    } else if(mensagem.equals("FINALIZAR()")){
                        listModel.removeAllElements();
                        listModel.addElement("Conexão finalizada...");
                        dataInputStream.close();
                        dataOutputStream.close();
                        socketCliente.close();
                    } else {
                        listModel.addElement(mensagem);
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

        socketCliente = new Socket();
        listModel.addElement("Digite o ip do servidor: ");

        //enviar mensagem clicando botão
        button1.addActionListener(actionEvent -> {
            if(!conectado){
                try {
                    InetAddress ip = InetAddress.getByName(textField1.getText());
                    InetSocketAddress porta = new InetSocketAddress(ip,12345);
                    socketCliente.connect(porta);
                    dataInputStream = new DataInputStream(socketCliente.getInputStream());
                    dataOutputStream = new DataOutputStream(socketCliente.getOutputStream());
                    conectado = true;
                    listModel.removeAllElements();
                    listModel.addElement("Conexão estabelecida com sucesso...");
                    listModel.addElement("Bem vindo, por favor insira seu nome...");
                    textField1.setText("");

                    receberMensagem.start();


                } catch (IOException e) {
                    listModel.addElement("Servidor não encontrado, insira novamente");
                    e.printStackTrace();
                }


            } else if(!textField1.getText().isEmpty()){
                try {
                    dataOutputStream.writeUTF(textField1.getText());
                    textField1.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(() -> {
                    Dimension vpSize = scrollPane.getViewport().getExtentSize();
                    Dimension logSize = list.getSize();

                    int height = logSize.height - vpSize.height;

                    scrollPane.getViewport().setViewPosition(new Point(0, height));
                });
            }
        });

        delButton.addActionListener(actionEvent -> {
            int index = list.getSelectedIndex();

            if(listModel.elementAt(index).charAt(0) == 'V' && listModel.elementAt(index).contains("Você: ")){
                try {
                    String mensagem = listModel.elementAt(index).replace("Você: ", "");
                    dataOutputStream.writeUTF("COMMAND=DELETE:" + mensagem);
                    listModel.remove(list.getSelectedIndex());
                } catch (Exception a){

                }
            } else {
                listModel.remove(list.getSelectedIndex());
            }
        });

        //enviar mensagem apertando enter
        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && !textField1.getText().isEmpty()) {
                    if(!conectado){
                        try {
                            InetAddress ip = InetAddress.getByName(textField1.getText());
                            InetSocketAddress porta = new InetSocketAddress(ip,12345);
                            socketCliente.connect(porta);
                            dataInputStream = new DataInputStream(socketCliente.getInputStream());
                            dataOutputStream = new DataOutputStream(socketCliente.getOutputStream());
                            conectado = true;
                            listModel.removeAllElements();
                            listModel.addElement("Conexão estabelecida com sucesso...");
                            listModel.addElement("Bem vindo, por favor insira seu nome...");
                            textField1.setText("");
                            receberMensagem.start();
                        } catch (IOException a) {
                            listModel.addElement("Servidor não encontrado, insira novamente");
                            //a.printStackTrace();
                        }
                    } else {
                        try {
                            dataOutputStream.writeUTF(textField1.getText());
                            textField1.setText("");
                        } catch (IOException a) {
                            a.printStackTrace();
                        }
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

