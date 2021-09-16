package chat;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import java.awt.TextArea;
import java.awt.event.KeyAdapter;


public class Client extends Thread {

	private static Client window;
	
	private JFrame frameChat;
	private JFrame frameError;
	private TextArea textArea;
	private JTextField textName;
	private JFrame frameGetName;
	private JTextField textField;
	private JComboBox<String> selectUsers;

	private String myName;
	private String names;
	private String receiver;
	private Socket connection;
	private PrintStream output;
	private Boolean primaryList;
	private BufferedReader input;
	private String[] clientsNames;


	public Client() {
		try {
			this.connection = new Socket("localhost", 2000);
			this.output = new PrintStream(this.connection.getOutputStream());
			this.input = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}

		this.primaryList = true;
		initialize();
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new Client();
					window.frameGetName.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void logic(String text) {
		this.frameChat.setTitle(text.toUpperCase());

		this.myName = text;

		// 1™ Stream => Remessa do nome do cliente
		this.output.println(myName);

		// 2™ Stream => Coleta da lista de usu·rios
		this.getUsersList("");
		this.updateUsersList();

		window.start();
	}

	public void getUsersList(String listNames) {
		try {
			names = listNames;
			
			if (primaryList) {
				names = input.readLine();
				primaryList = false;
			}
			
			names = names.substring(3);
			clientsNames = names.split(",");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateUsersList() {
		this.selectUsers.removeAllItems();
		this.selectUsers.addItem("TODOS");
		
		for (String name : clientsNames) {
			if (!name.equalsIgnoreCase(myName)) {
				selectUsers.addItem(name.toUpperCase());				
			}
		}
	}

	public void getText(String text) {
		// 3™ Stream => Remessa da mensagem do cliente
		this.output.println(text);

		// 4™ Stream => Remete o destin·tario
		receiver = (String) this.selectUsers.getSelectedItem();
		this.output.println(receiver);

		String oldText = this.textArea.getText();
		oldText += System.lineSeparator() + "[VocÍ] disse para [" + receiver + "]: " + text;
		this.textArea.setText(oldText);
	}

	public void run() {
		try {
//			input = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
			String line;

			while (true) {
				// 5™ Stream => Coleta de mensagens do servidor
				line = (String) input.readLine();

				if (line.startsWith("@l")) {
					getUsersList(line);
					updateUsersList();
				} else {
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
			}
		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void initialize() {

		// Tela de captura do nome do usu·rio
		frameGetName = new JFrame();
		frameGetName.setBounds(100, 100, 480, 139);
		frameGetName.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameGetName.getContentPane().setLayout(null);

		JLabel labelInformeNome = new JLabel("Informe seu nome:");
		labelInformeNome.setFont(new Font("Arial", Font.PLAIN, 14));
		labelInformeNome.setBounds(24, 12, 120, 16);
		frameGetName.getContentPane().add(labelInformeNome);

		textName = new JTextField();
		textName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (textName.getText().equals("")) {
						window.frameGetName.setVisible(false);
						window.frameError.setVisible(true);
					} else {
						window.frameGetName.setVisible(false);
						window.frameChat.setVisible(true);
						logic(textName.getText());
					}
				}
			}
		});
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
					logic(textName.getText());
				}
			}
		});
		btnIn.setFont(new Font("Arial", Font.PLAIN, 14));
		btnIn.setBounds(187, 60, 90, 22);
		frameGetName.getContentPane().add(btnIn);

		// Tela de erro
		frameError = new JFrame();
		frameError.setBounds(100, 100, 480, 139);
		frameError.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameError.getContentPane().setLayout(null);

		JLabel labelError = new JLabel("Texto inv√°lido. Tente novamente!");
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
		
		// Tela do chat
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

		selectUsers = new JComboBox<>();
		selectUsers.setBounds(109, 230, 150, 20);
		frameChat.getContentPane().add(selectUsers);

		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					getText(textField.getText());
					textField.setText("");
				}
			}
		});
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
		textArea.setBounds(10, 32, 416, 180);
		frameChat.getContentPane().add(textArea);
	}
}
