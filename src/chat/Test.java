package chat;

import chat.componentes.ConexaoServidor;
import chat.componentes.InterfaceGrafica;
import chat.componentes.Run;

import java.awt.EventQueue;

public class Test {

	public static void main(String[] args) {
		ConexaoServidor window = new ConexaoServidor();
		InterfaceGrafica interfaceGrafica = new InterfaceGrafica();
		interfaceGrafica.initialize(window);
	}
}


