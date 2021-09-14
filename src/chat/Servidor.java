package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor extends Thread {
	private static Map<String, PrintStream> clientes = new HashMap<>();
	private static String listaNomesClientes = "@l";
	private Socket socketCliente;
	private String nomeCliente;

	public Servidor(Socket socketCliente) {
		this.socketCliente = socketCliente;
	}

	public static void main(String[] args) {
		ServerSocket servidor;
		//listaNomesClientes = "";
		try {
			servidor = new ServerSocket(2000);
			while (true) {
				System.out.print("Esperando conectar...");
				Socket cliente = servidor.accept();
				System.out.println(" Conectou!");
				Thread t = new Servidor(cliente);
				t.start();
			}
		} catch (IOException ex) {
			System.err.println("Erro de conexão do servidor!");
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public void run() {
		try {
			BufferedReader leitor = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			/**
			 * 1º Stream => Coleta do nome do Cliente
			 */
			nomeCliente = leitor.readLine();

			if (listaNomesClientes.equals(""))
				listaNomesClientes = nomeCliente;
			else
				listaNomesClientes += "," + nomeCliente;

			PrintStream escritor = new PrintStream(socketCliente.getOutputStream());
			clientes.put(nomeCliente.toUpperCase(), escritor);

			/**
			 * 2ª Stream => Remessa da lista de usuários
			 */
			sendUserList(escritor);
			escritor.println(listaNomesClientes);

			/**
			 * 3º Stream => Coleta da mensagem do cliente
			 */
			String msg = leitor.readLine();

			String destinatario;

			while ((msg != null) && (!msg.trim().equals(""))) {

				/**
				 * 4º Stream => Coleta do destinatário da mensagem
				 */
				destinatario = leitor.readLine();
				if (clientes.containsKey(destinatario.toUpperCase())) {
					sendToOne(destinatario, " disse: ", msg);
					msg = leitor.readLine();
				} else {
					sendToAll(escritor, " disse: ", msg);
					msg = leitor.readLine();

				}
			}

			sendToAll(escritor, " saiu ", "do Chat!");

			clientes.remove(nomeCliente);
			listaNomesClientes.replace(nomeCliente + ",", "");
			socketCliente.close();

		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void sendUserList(PrintStream escritor) {
		
		//escritor.println("**lista**");
		
		for (PrintStream cliente : clientes.values()) {
			cliente.println(listaNomesClientes);
		}
	}

	public void sendToOne(String destinatario, String acao, String msg) {

		/**
		 * 5ª Stream => Remessa de mensagens (privada)
		 */
		clientes.get(destinatario).println("[" + nomeCliente.toUpperCase() + " (PRIVADO)]" + acao + msg);

	}

	public void sendToAll(PrintStream escritor, String acao, String msg) throws IOException {

		for (PrintStream cliente : clientes.values()) {
			if (cliente != escritor) {
				/**
				 * 5ª Stream => Remessa de mensagens (geral)
				 */
				cliente.println("[" + nomeCliente.toUpperCase() + "]" + acao + msg);
			}
			if (acao.equals(" saiu ")) {
				if (cliente == escritor)
					/**
					 * 5ª Stream => Remessa de mensagens (saída)
					 */
					cliente.println("");
			}
		}
	}
}