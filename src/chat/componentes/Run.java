package chat.componentes;

import chat.Servidor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Run  extends Thread{


    public Run(ConexaoServidor conexaoServidor, InterfaceGrafica interfaceGrafica) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(conexaoServidor.getConnection().getInputStream()));
            String line;

            while (true) {
                line = input.readLine();

                if (line.trim().equals("")) {
                    System.out.println("Conexao encerrada!!!");
                    break;
                }

                String oldText = interfaceGrafica.textArea.getText();

                oldText += System.lineSeparator() + line;

                interfaceGrafica.textArea.setText(oldText);
                System.out.println();
                System.out.println(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
