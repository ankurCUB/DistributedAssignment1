package common;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    private final Socket socket;
    private DataOutputStream dataOutputStream;

    private final DataInputStream dataInputStream;

    Client(String address, int port) throws IOException {
        socket = new Socket(address, port);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void sendMessage() throws IOException {
        dataOutputStream.writeUTF("Sent message to server");
    }

    private void receiveMessage() throws IOException {
        String data = dataInputStream.readUTF();
        System.out.println(data);
    }

    public void disconnectClient(){
        dataOutputStream = null;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
//        for (int i = 0; i < 5; i++) {
//            marketPlace.Client client = new marketPlace.Client("localhost", 5003);
//            client.sendMessage();
//            client.receiveMessage();
//            client.disconnectClient();
//        }
        Client client = new Client("localhost", 5003);
//        client.sendMessage();
//        client.receiveMessage();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("function","login");
        jsonObject.put("username","test2");
        jsonObject.put("password","123");
        client.dataOutputStream.writeUTF(jsonObject.toString());
        client.disconnectClient();
    }
}
