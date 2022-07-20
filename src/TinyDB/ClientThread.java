package TinyDB;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientThread extends Thread {
    private TinyDBConnection connection;
    public ClientThread(Socket clientSocket) {
        this.connection = new TinyDBConnection();
        this.connection.setClientSocket(clientSocket);
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(this.connection.getClientSocket().getOutputStream(), true);
                Scanner in = new Scanner(this.connection.getClientSocket().getInputStream());
                ) {

            System.out.println("Received a new connection attempt fromt " + this.connection.getClientSocket().getRemoteSocketAddress().toString());

            // First thing we expect from the client is the connection string
            String connectionString = in.nextLine();
            String[] data = connectionString.split(":"); // connectionString = "username:password:dbName"
            if (data.length != 3) {
                out.println("Bad connection string");
                this.connection.getClientSocket().close();
                System.out.println("Bad connection string from " + this.connection.getClientSocket().getRemoteSocketAddress());
                return;
            }

            String username = data[0];
            String password = data[1];
            String dbName   = data[2];

            this.connection.setDbName(dbName);

            TinyDBConnection fakeConnection = new TinyDBConnection();
            fakeConnection.setDbName("_tinyDB_accounts_");
            TinyDBResultSet rs = SQLExecutor.instance.execute(fakeConnection,
                                                        "SELECT * FROM accounts WHERE username='"+username+"' AND password='" + password + "'");
            if (rs.getSuccess() && rs.getData() != null && rs.getData().isEmpty()) {
                out.println("Access refused: Wrong username or password");
                this.connection.getClientSocket().close();
                System.out.println("Wrong username of password from " + this.connection.getClientSocket().getRemoteSocketAddress());
                return;
            }

            System.out.println("Authorized access to " + this.connection.getClientSocket().getRemoteSocketAddress());

            while (in.hasNextLine()) {
                String query = in.nextLine();
                System.out.println("Received query '" + query + "' from " + this.connection.getClientSocket().getRemoteSocketAddress());
                rs = SQLExecutor.instance.execute(this.connection, query);
                out.println(rs.toString());
            }

        } catch (IOException ex) {

        }
    }
}
