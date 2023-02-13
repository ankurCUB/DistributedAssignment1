package common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import static common.Utils.CUSTOMER_DB_PORT;

public abstract class Client {

    protected String address;
    protected int port;
    protected String sendRequest(String request) {
        try {
            ClientDelegate clientDelegate = new ClientDelegate(address, port);
            String response = clientDelegate.sendRequest(request);
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.toString();
        } catch (IOException exception) {
            exception.printStackTrace();
            return "[]";
        }
    }
}
