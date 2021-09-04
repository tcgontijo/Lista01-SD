package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor extends Thread {
	private static ArrayList<PrintStream> clientes;
	private Socket conexao;
	private String meuNome;

	public Servidor(Socket s) {
		conexao = s;
	}

	public static void main(String[] args) {
		clientes = new ArrayList<PrintStream>();
		ServerSocket s;
		try {
			s = new ServerSocket(2000);
			while (true) {
				System.out.print("Esperando conectar...");
				Socket conexao = s.accept();
				System.out.println(" Conectou!");
				Thread t = new Servidor(conexao);
				t.start();
			}
		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public void run() {
		try {
			BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
			PrintStream saida = new PrintStream(conexao.getOutputStream());
			meuNome = entrada.readLine();
			if (meuNome == null) {
				return;
			}
			clientes.add(saida);
			String linha = entrada.readLine();
			while ((linha != null) && (!linha.trim().equals(""))) {
				sendToAll(saida, " disse: ", linha);
				linha = entrada.readLine();
			}
			sendToAll(saida, " saiu ", " do Chat!");
			clientes.remove(saida);
			conexao.close();

		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void sendToAll(PrintStream saida, String acao, String linha) throws IOException {
		//Enumeration<PrintStream> e = Collections.enumeration(clientes);

		for (PrintStream chat : clientes) {
			//PrintStream chat = (PrintStream) e.nextElement();
			if (chat != saida) {
				chat.println(meuNome + acao + linha);
			}
			if (acao.equals(" saiu ")) {
				if (chat == saida)
					chat.println("");
			}

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
