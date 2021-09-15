package chat;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor extends Thread {
	private static Map<String, PrintStream> clientes = new HashMap<>();
	private static String listClientsName = "@l";
	private Socket socketCliente;

	//TODO Inicio Codigo Log
	private String clientName;
	private Integer portRemoteClient;
	private InetAddress addressClient;

	private PrintWriter printWriter;
	private static FileWriter fileWriter;

	private BufferedReader reader;
	private PrintStream writer;
	//TODO FIM Codigo Log

	public Servidor(Socket socketCliente) {
		this.socketCliente = socketCliente;

		//TODO Inicio Codigo Log
		try {
//			fileWriter = new FileWriter("logs.txt", true);
//			printWriter = new PrintWriter(fileWriter);

			reader = new BufferedReader(new InputStreamReader(this.socketCliente.getInputStream()));
			writer = new PrintStream(this.socketCliente.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//TODO FIM Codigo Log

	}

	public static void main(String[] args) {		
		ServerSocket server;
		try {
			server = new ServerSocket(2000);
			
			while (true) {
				System.out.print("Esperando conectar...");
				Socket connection = server.accept();

				System.out.println(" Conectou!");
				Thread thread = new Servidor(connection);
				thread.start();
			}
		} catch (IOException ex) {
			System.err.println("Erro de conex�o do servidor!");
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void run() {
		try {
//			BufferedReader leitor = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			/**
			 * 1º Stream => Coleta do nome do Cliente
			 */
			clientName = reader.readLine();

			//TODO Inicio Codigo Log
//			String log = "";
//			portRemoteClient = socketCliente.getPort();
//			addressClient = socketCliente.getInetAddress();
//
//			myName = nomeCliente;
//			if (myName == null) {
//				return;
//			}
			//TODO FIM Codigo Log

			listClientsName += "," + clientName;
			
//			PrintStream escritor = new PrintStream(socketCliente.getOutputStream());
			clientes.put(clientName.toUpperCase(), writer);

			/**
			 * 2ª Stream => Remessa da lista de usuários
			 */
			sendUserList();

			/**
			 * 3º Stream => Coleta da mensagem do cliente
			 */
			String msg = reader.readLine();

			String destinatario;


				while ((msg != null) && (!msg.trim().equals(""))) {
				//TODO Inicio Codigo Log
//					String	log = createLineLog(msg);
//					printWriter.println(log);
//					printWriter.flush();
				//TODO FIM Codigo Log
					/**
					 * 4º Stream => Coleta do destinatário da mensagem
					 */
					destinatario = reader.readLine();
					if (clientes.containsKey(destinatario.toUpperCase())) {
						sendToOne(destinatario, " disse: ", msg);
						msg = reader.readLine();
					} else {
						sendToAll(writer, " disse: ", msg);
						msg = reader.readLine();
					}
				}

				sendToAll(writer, " saiu ", "do Chat!");

				clientes.remove(clientName);
				listClientsName.replace(clientName + ",", "");
				socketCliente.close();

		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void sendUserList() {		
		for (PrintStream cliente : clientes.values()) {
			cliente.println(listClientsName);
		}
	}

	public void sendToOne(String destinatario, String acao, String msg) {

		/**
		 * 5ª Stream => Remessa de mensagens (privada)
		 */
		clientes.get(destinatario).println("[" + clientName.toUpperCase() + " (PRIVADO)]" + acao + msg);

	}

	public void sendToAll(PrintStream escritor, String acao, String msg) throws IOException {

		for (PrintStream cliente : clientes.values()) {
			if (cliente != escritor) {
				/**
				 * 5ª Stream => Remessa de mensagens (geral)
				 */
				cliente.println("[" + clientName.toUpperCase() + "]" + acao + msg);
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
	//TODO Inicio Codigo Log
	public String createLineLog(String line) {
		String hostName = "<" + clientName + ">";
		String hostIp = "<" + addressClient.getHostAddress() + ">";

		String log = hostName + "@" + hostIp + "@<" + portRemoteClient + ">#<" + line + ">";
		return log;
	}
	//TODO FIM Codigo Log
}