package chat;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextPane;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import java.awt.Color;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollBar;
import java.awt.TextArea;

public class Test extends Thread {

	private static boolean done = false;
	
	private static Test window;
	private JFrame frameChat;
	private JFrame frameGetName;
	private JFrame frameError;
	private JTextField textField;
	private JTextField textName;
	private Socket connection;
	private TextArea textArea;
	
	PrintStream output;
	
	public Test() {
		try {
			this.connection = new Socket("localhost", 2000);
			this.output = new PrintStream(this.connection.getOutputStream());
		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		initialize();
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new Test();
					window.frameGetName.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void logic(String text, boolean flag) {
				frameChat.setTitle(text.toUpperCase());
		
				String myName = text;		
				output.println(myName);
				window.start();
	}
	
	public void getText(String text) {
		String line = text;
		this.output.println(line);
		
		String oldText = this.textArea.getText();
		
		oldText += System.lineSeparator() + "[Você] disse:" + line;
		this.textArea.setText(oldText);
	}
	
	public void run() {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
			String line;
			
			while (true) {
				line = input.readLine();
				
				if (line.trim().equals("")) {
					System.out.println("Conexao encerrada!!!");
					break;
				}
				
				String oldText = this.textArea.getText();
				
				oldText += System.lineSeparator() + line; 
				
				this.textArea.setText(oldText);
				System.out.println();
				System.out.println(line);
			}
			done = true;
		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void initialize() {
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
					window.frameGetName.setVisible(false);
					window.frameError.setVisible(true);
				} else {
					window.frameGetName.setVisible(false);
					window.frameChat.setVisible(true);		
					logic(textName.getText(), true);
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
		
		JLabel labelError = new JLabel("Texto invï¿½lido. Tente novamente!");
		labelError.setHorizontalAlignment(SwingConstants.CENTER);
		labelError.setFont(new Font("Arial", Font.PLAIN, 14));
		labelError.setBounds(24, 12, 416, 16);
		frameError.getContentPane().add(labelError);
		
		JButton btnReturnGetName = new JButton("Fechar");
		btnReturnGetName.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				window.frameGetName.setVisible(true);
				window.frameError.setVisible(false);		
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
				getText(textField.getText());
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
	}
}
