package chat.componentes;

import chat.Servidor;
import chat.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexaoServidor {
    private Socket connection;
    private PrintStream output;
    public ConexaoServidor() {
        try {
            this.connection = new Socket("localhost", 2000);
            this.output = new PrintStream(this.connection.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void output(String line) throws FileNotFoundException {
        this.output = new PrintStream(line);
    }

    public Socket getConnection() {
        return this.connection;
    }
}
