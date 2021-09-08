package chat;

import chat.componentes.ConexaoServidor;
import chat.componentes.InterfaceGrafica;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextPane;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import java.awt.Color;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollBar;
import java.awt.TextArea;

public class Test extends Thread {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ConexaoServidor window = new ConexaoServidor();
					InterfaceGrafica interfaceGrafica = new InterfaceGrafica();
					interfaceGrafica.initialize(window);
					interfaceGrafica.escondeNome();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
