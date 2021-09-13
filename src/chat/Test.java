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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import java.awt.Color;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollBar;
import java.awt.TextArea;
import java.awt.event.KeyAdapter;
import java.awt.Scrollbar;
import javax.swing.JSpinner;
import javax.swing.JSlider;
import javax.swing.JFormattedTextField;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class Test extends Thread {

	private static boolean done = false;
	private static String[] nomesClientes;

	private static Test window;

	private JFrame frameChat;
	private JFrame frameGetName;
	private JFrame frameError;
	private JTextField textField;
	private JTextField textName;
	private TextArea textArea;

	private Socket connection;
	private PrintStream output;
	private BufferedReader input;
	private String myName;
	private String nomes;

	private JComboBox<String> selectUsers;

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

	public void logic(String text) {

		frameChat.setTitle(text.toUpperCase());

		myName = text;
		/**
		 * 1º Stream => Remessa do nome do Cliente
		 */
		output.println(myName);

		getUsersList();
		updateUsersList();

		window.start();

	}

	public void getUsersList() {
		try {
			input = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
			/**
			 * 2ª Stream => Coleta da lista de usuários
			 */
			nomes = input.readLine();

			nomesClientes = nomes.split(",");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateUsersList() {

		this.selectUsers.removeAllItems();
		this.selectUsers.addItem("TODOS");
		for (String nome : nomesClientes) {
			System.out.println(nome);
			if (!nome.equalsIgnoreCase(myName))
				selectUsers.addItem(nome.toUpperCase());
		}
	}

	public void getText(String text) {

		String line = text;
		/**
		 * 3º Stream => Remessa da mensagem do cliente
		 */
		this.output.println(line);

		/**
		 * 4º Stream => Se requereu lista então servidor retorna a lista de clientes
		 */

//		if (line.equals("**lista**")) {
//			getUsersList();
//			updateUsersList();
//		} else {

		/**
		 * 4º Stream => Se não requereu lista então remete o destinatário
		 */
		this.output.println(this.selectUsers.getSelectedItem());

		String oldText = this.textArea.getText();

		oldText += System.lineSeparator() + "[Você] disse para [" + this.selectUsers.getSelectedItem() + "]: " + line;
		this.textArea.setText(oldText);
//		}
	}

	public void run() {
		try {
			input = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
			String line;

			while (true) {
				/**
				 * 5ª Stream => Coleta de mensagens dos outros clientes
				 */
				line = input.readLine();

				if (line.trim().equals("") || line.equals(this.nomes)) {
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

		/**
		 * Tela de captura do nome do usuário
		 */
		frameGetName = new JFrame();
		frameGetName.setBounds(100, 100, 480, 139);
		frameGetName.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameGetName.getContentPane().setLayout(null);

		/**
		 * Componentes da tela de caputura do nome do usuário
		 */

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

		/**
		 * Tela de erro
		 */

		frameError = new JFrame();
		frameError.setBounds(100, 100, 480, 139);
		frameError.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameError.getContentPane().setLayout(null);

		JLabel labelError = new JLabel("Texto inválido. Tente novamente!");
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

		/**
		 * Tela do chat
		 */

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

		/**
		 * Listener de clique no select
		 */
		// selectUsers.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				System.out.println("clique no select");
//				// updateUsersList();
//				getText("**lista**");
//			}
//		});
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
