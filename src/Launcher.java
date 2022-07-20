import TinyDB.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Launcher {
    private static final int PORT = 7777;

    public static void main(String[] args) {
        System.out.println( " _____ _____ _   ___   _____________ \n" +
                "|_   _|_   _| \\ | \\ \\ / /  _  \\ ___ \\\n" +
                "  | |   | | |  \\| |\\ V /| | | | |_/ /\n" +
                "  | |   | | | . ` | \\ / | | | | ___ \\\n" +
                "  | |  _| |_| |\\  | | | | |/ /| |_/ /\n" +
                "  \\_/  \\___/\\_| \\_/ \\_/ |___/ \\____/\n" +
                "=====================================\n" +
                "TinyDB version 1.0 by mizoxes (Hamza EL Kaiche)");

        System.out.println("loading dbinfo... ");
        DBManager.load();
        System.out.println("dbinfo loaded successfuly!");
        System.out.println("Listening for new connections on port " + PORT);

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            boolean quit = false;
            while (!quit) {
                Socket clientSocket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(clientSocket);
                clientThread.start();
            }
        }
        catch (IOException ex) {
        }
    }
}