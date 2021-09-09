package chat.componentes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;

public class InterfaceGrafica {

    public JFrame frameChat;
    public JFrame frameGetName;
    public JFrame frameError;
    public JTextField textField;
    public JTextField textName;
    public TextArea textArea;

    public void initialize(ConexaoServidor cx) {
        frameGetName = new JFrame();
        frameGetName.setBounds(100, 100, 480, 139);
        frameGetName.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameGetName.getContentPane().setLayout(null);

        JLabel labelName = new JLabel("Informe seu nome:");
        labelName.setFont(new Font("Arial", Font.PLAIN, 14));
        labelName.setBounds(24, 12, 120, 16);
        frameGetName.getContentPane().add(labelName);

        textName = new JTextField();
        textName.setBounds(144, 12, 296, 20);
        frameGetName.getContentPane().add(textName);
        textName.setColumns(10);

        JButton btnIn = new JButton("Entrar");
        btnIn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (textName.getText().equals("")) {
                    frameGetName.setVisible(false);
                    frameError.setVisible(true);
                } else {
                    frameGetName.setVisible(false);
                    frameChat.setVisible(true);
                    try {
                        logic(textName.getText(), cx,new Run(cx,retorno()));
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    }
                }
            }
        });
        btnIn.setFont(new Font("Arial", Font.PLAIN, 14));
        btnIn.setBounds(187, 60, 90, 22);
        frameGetName.getContentPane().add(btnIn);

        frameError = new JFrame();
        frameError.setBounds(100, 100, 480, 139);
        frameError.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameError.getContentPane().setLayout(null);

        JLabel labelError = new JLabel("Texto inv�lido. Tente novamente!");
        labelError.setHorizontalAlignment(SwingConstants.CENTER);
        labelError.setFont(new Font("Arial", Font.PLAIN, 14));
        labelError.setBounds(24, 12, 416, 16);
        frameError.getContentPane().add(labelError);

        JButton btnReturnGetName = new JButton("Fechar");
        btnReturnGetName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frameGetName.setVisible(true);
                frameError.setVisible(false);
            }
        });
        btnReturnGetName.setFont(new Font("Arial", Font.PLAIN, 14));
        btnReturnGetName.setBounds(187, 60, 90, 22);
        frameError.getContentPane().add(btnReturnGetName);

        frameChat = new JFrame();
        frameChat.setBounds(100, 100, 480, 339);
        frameChat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameChat.getContentPane().setLayout(null);


        JLabel labelChat = new JLabel("Conversa:");
        labelChat.setHorizontalAlignment(SwingConstants.LEFT);
        labelChat.setFont(new Font("Arial", Font.PLAIN, 14));
        labelChat.setBounds(24, 12, 416, 14);
        frameChat.getContentPane().add(labelChat);

        JLabel labelUsers = new JLabel("Conectados:");
        labelUsers.setFont(new Font("Arial", Font.PLAIN, 14));
        labelUsers.setBounds(24, 232, 87, 14);
        frameChat.getContentPane().add(labelUsers);

        JLabel labelInput = new JLabel("Digite:");
        labelInput.setFont(new Font("Arial", Font.PLAIN, 14));
        labelInput.setBounds(24, 262, 87, 16);
        frameChat.getContentPane().add(labelInput);

        JComboBox comboBox = new JComboBox();
        comboBox.setModel(new DefaultComboBoxModel(new String[] {"TESTE 1", "TESTE 2", "TESTE 3"}));
        comboBox.setBounds(109, 230, 150, 20);
        frameChat.getContentPane().add(comboBox);

        textField = new JTextField();
        textField.setBounds(68, 261, 273, 20);
        frameChat.getContentPane().add(textField);
        textField.setColumns(10);

        JButton btnNewButton = new JButton("Enviar");
        btnNewButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    getText(textField.getText(),cx);
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
                textField.setText("");
            }
        });
        btnNewButton.setFont(new Font("Arial", Font.PLAIN, 14));
        btnNewButton.setBounds(351, 260, 89, 22);
        frameChat.getContentPane().add(btnNewButton);

        textArea = new TextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        textArea.setForeground(Color.BLACK);
        textArea.setEditable(false);
        textArea.setBounds(24, 37, 416, 180);
        frameChat.getContentPane().add(textArea);

        this.mostrarNome(frameGetName);
    }

    private InterfaceGrafica retorno() {
        return this;
    }

    public void getText(String text, ConexaoServidor cx) throws FileNotFoundException {

        String line = text;
        cx.output(line);

        String oldText = this.textArea.getText();

        oldText += System.lineSeparator() + "[Você] disse:" + line;
        this.textArea.setText(oldText);
    }

    public void mostrarNome(JFrame tela) {
        tela.setVisible(true);
    }

    public void logic(String text, ConexaoServidor cx, Run run) throws FileNotFoundException {

        this.frameChat.setTitle(text.toUpperCase());

        String myName = text;

        cx.output(myName);

        run.start();
    }
}
