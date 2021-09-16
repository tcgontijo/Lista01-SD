package chat;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor extends Thread {
	private static Map<String, PrintStream> clients = new HashMap<>();
	private static String listClientsName = "@l";
	private Socket clientSocket;

	private String clientName;
	private Integer portRemoteClient;
	private InetAddress addressClient;

	private PrintWriter printWriter;
	private static FileWriter fileWriter;

	private BufferedReader reader;
	private PrintStream writer;

	public Servidor(Socket socketCliente) {
		this.clientSocket = socketCliente;

		try {
			fileWriter = new FileWriter("logs.txt", true);
			printWriter = new PrintWriter(fileWriter);

			reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			writer = new PrintStream(this.clientSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

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
			System.err.println("Erro de conexão do servidor!");
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void run() {
		try {			
			// 1ª Stream => Coleta do nome do Cliente
			clientName = reader.readLine();

			portRemoteClient = clientSocket.getPort();
			addressClient = clientSocket.getInetAddress();

			listClientsName += "," + clientName;
			
			clients.put(clientName.toUpperCase(), writer);

			// 2ª Stream => Remessa da lista de usuários
			sendUserList();

			// 3ª Stream => Coleta da mensagem do cliente
			String message = reader.readLine();

			String receiver;

			while ((message != null) && (!message.trim().equals(""))) {
				String log = createLineLog(message);
				printWriter.println(log);
				printWriter.flush();

				// 4ª Stream => Coleta do destinário da mensagem
				receiver = reader.readLine();
				if (clients.containsKey(receiver.toUpperCase())) {
					sendToOne(receiver, " disse: ", message);
					message = reader.readLine();
				} else {
					sendToAll(writer, " disse: ", message);
					message = reader.readLine();
				}
			}

			sendToAll(writer, " saiu ", "do Chat!");
			clients.remove(clientName);
			listClientsName.replace(clientName + ",", "");
			clientSocket.close();
		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
			
			try {
				sendToAll(writer, " saiu ", "do Chat!");
				clients.remove(clientName);
				listClientsName.replace(clientName + ",", "");
				clientSocket.close();
			} catch (IOException error) {
				Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, error);
			}
		}
	}

	public void sendUserList() {		
		for (PrintStream cliente : clients.values()) {
			cliente.println(listClientsName);
		}
	}

	public void sendToOne(String receiver, String action, String message) {
		// 5ª Stream => Remessa de mensagens (privada)
		clients.get(receiver).println("[" + clientName.toUpperCase() + " (PRIVADO)]" + action + message);
	}

	public void sendToAll(PrintStream writer, String action, String message) throws IOException {
		for (PrintStream client : clients.values()) {
			if (client != writer) {
				// 5ª Stream => Remessa de mensgens (geral)
				client.println("[" + clientName.toUpperCase() + "]" + action + message);
			}
			if (action.equals(" saiu ")) {
				if (client == writer)
					// 5ª Stream => Remessa de mensagens (saída)
					client.println("");
			}
		}
	}
	
	public String createLineLog(String line) {
		String hostName = "<" + clientName + ">";
		String hostIp = "<" + addressClient.getHostAddress() + ">";

		String log = hostName + "@" + hostIp + "@<" + portRemoteClient + ">#<" + line + ">";
		return log;
	}
}