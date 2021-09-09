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
	
	private BufferedReader input;
	private PrintStream output;

	public Servidor(Socket socket) {
		this.connection = socket;
		
		try {
			fileWriter = new FileWriter("logs.txt", true);
			printWriter = new PrintWriter(fileWriter);
			
			input = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
			output = new PrintStream(this.connection.getOutputStream());
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
			
		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
			
			try {
				sendToAll(output, " saiu ", " do Chat!");
				clients.remove(output);
				connection.close();			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String createLineLog(String line) {
		String hostName = "<" + myName + ">";
		String hostIp = "<" + addressClient.getHostAddress() + ">";
		
		String log = hostName + "@" + hostIp + "@<" + portRemoteClient + ">#<" + line + ">";
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