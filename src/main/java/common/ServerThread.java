package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread{

    private final Socket socket;
    private final Server server;
    public ServerThread(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader inputStream = null;
        String request = null;
        try {
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            request = inputStream.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String response = server.processClientRequest(request);
        PrintWriter outputStream = null;
        try {
            outputStream = new PrintWriter(socket.getOutputStream(), true);
            outputStream.println(response);
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
