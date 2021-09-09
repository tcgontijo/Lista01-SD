package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor extends Thread {
	private static Map<String, PrintStream> clientes = new HashMap<>();
	private Socket socketCliente;
	private String nomeCliente;

	public Servidor(Socket socketCliente) {
		this.socketCliente = socketCliente;
	}

	public static String[] getClientes() {
		String[] nomesClientes = new String[clientes.size()];

		int i = 0;
		for (String nome : clientes.keySet()) {
			nomesClientes[i] = nome;
			i++;
		}

		return nomesClientes;
	}

	public static void main(String[] args) {
		ServerSocket servidor;
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
			nomeCliente = leitor.readLine();

			PrintStream escritor = new PrintStream(socketCliente.getOutputStream());
			clientes.put(nomeCliente.toUpperCase(), escritor);
			
			ObjectOutputStream escritorArray = new ObjectOutputStream(socketCliente.getOutputStream());
			
			escritorArray.writeObject(getClientes());

			
			String msg = leitor.readLine();
			String destinatario;
			
			
			while ((msg != null) && (!msg.trim().equals(""))) {
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
			socketCliente.close();

		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void sendToOne(String destinatario, String acao, String msg) {

		clientes.get(destinatario).println("[" + nomeCliente.toUpperCase() + " (PRIVADO)]" + acao + msg);

	}

	public void sendToAll(PrintStream escritor, String acao, String msg) throws IOException {

		for (PrintStream chat : clientes.values()) {
			if (chat != escritor) {
				chat.println("[" + nomeCliente.toUpperCase() + "]" + acao + msg);
			}
			if (acao.equals(" saiu ")) {
				if (chat == escritor)
					chat.println("");
			}

//		for (PrintStream chat : saidasClientes) {
//			if (chat != escritor) {
//				chat.println("[" + nomeCliente + "]" + acao + msg);
//			}
//			if (acao.equals(" saiu ")) {
//				if (chat == escritor)
//					chat.println("");
//			}

//			while (e.hasMoreElements()) {
//				PrintStream chat = (PrintStream) e.nextElement();
//				if (chat != saida) {
//					chat.println(meuNome + acao + linha);
//				}
//				if (acao.equals(" saiu ")) {
//					if (chat == saida)
//						chat.println("");
//				}
//			}
		}
	}
}