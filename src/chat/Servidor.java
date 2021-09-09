package chat;

import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor extends Thread {
	private static ArrayList<PrintStream> clients;
	private Socket connection;
	
	private String myName;
	private Integer portRemoteClient;
	private InetAddress addressClient;
	
	private PrintWriter printWriter;
	private static FileWriter fileWriter;

	public Servidor(Socket socket) {
		this.connection = socket;
		
		try {
			fileWriter = new FileWriter("logs.txt", true);
			printWriter = new PrintWriter(fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ServerSocket serverSocket;
		clients = new ArrayList<PrintStream>();
		try {
			serverSocket = new ServerSocket(2000);
			while (true) {
				System.out.print("Esperando conectar...");
				Socket connection = serverSocket.accept();
				
				System.out.println(" Conectou!");
				Thread t = new Servidor(connection);
				
				t.start();
			}
		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public void run() {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			PrintStream output = new PrintStream(connection.getOutputStream());
			
			String log = "";
			portRemoteClient = connection.getPort();
			addressClient = connection.getInetAddress();
			
			myName = input.readLine();
			if (myName == null) {
				return;
			}
			
			clients.add(output);
			String line = input.readLine();
			while ((line != null) && (!line.trim().equals(""))) {
				log = createLineLog(line);
				printWriter.println(log);
				printWriter.flush();
				
				sendToAll(output, " disse: ", line);
				line = input.readLine();
			}
			sendToAll(output, " saiu ", " do Chat!");
			clients.remove(output);
			connection.close();
		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public String createLineLog(String line) {
		String log = "";
		try {
			@SuppressWarnings("static-access")
			String hostName = "<" + addressClient.getLocalHost().getHostName() + ">";
			String hostIp = "<" + addressClient.getHostAddress() + ">";
			
			log = myName + "@" + hostIp + "@<" + portRemoteClient + ">#<" + line + ">";
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return log;
	}

	public void sendToAll(PrintStream output, String action, String line) throws IOException {
		for (PrintStream chat : clients) {
			if (chat != output) {
				chat.println("[" + myName + "]" + action + line);
			}
			
			if (action.equals(" saiu ")) {
				if (chat == output)
					chat.println("");
			}
		}
	}
}