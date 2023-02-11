package buyer;

import common.ClientDelegate;
import common.Server;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static common.Utils.*;

public class ServerSideBuyersInterface extends Server implements BuyersInterface {

    public ServerSideBuyersInterface(int port) throws IOException {
        super(port);
    }

    @Override
    public String createAccount(String username, String password, String buyerName) {
        try {
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT) ;
            String request = "INSERT INTO Login(\"username\",\"password\",\"userType\") VALUES (\"" +
                    username + "\", \"" +
                    password + "\", \"buyer\")";
            clientDelegate.sendRequest(request);
        } catch (IOException exception){
            exception.printStackTrace();
        }

        String userIDResponse = fetchUserID(username, password);
        JSONObject userIDJSON = new JSONObject(userIDResponse);
        int userID = userIDJSON.getInt("userID");
        try {
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT) ;
            String request = "INSERT INTO Buyers(\"buyerID\",\"buyerName\") VALUES (" +
                    userID + ", \"" +
                    buyerName + "\")";
            clientDelegate.sendRequest(request);

        } catch (IOException exception){
            exception.printStackTrace();
        }
        return userIDResponse;
    }

    @Override
    public String login(String username, String password) {
        return fetchUserID(username,password);
    }

    @Override
    public String logout(int buyerID) {
        return "{}";
    }

    @Override
    public String getSellerRating(int sellerID) {
        return fetchSellerRating(sellerID);
    }

    @Override
    protected String processClientRequest(String request) {
        JSONObject jsonObject = new JSONObject(request);
        String response = "{} ";
        try {
            String invokedFunction = jsonObject.getString("function");
            if (invokedFunction.equalsIgnoreCase("createAccount")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                String username = arguments.getString("username");
                String password = arguments.getString("password");
                String buyerName = arguments.getString("buyerName");
                response = createAccount(username, password, buyerName);
            } else if(invokedFunction.equalsIgnoreCase("login")){
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                String username = arguments.getString("username");
                String password = arguments.getString("password");
                response = login(username, password);
            } else if(invokedFunction.equalsIgnoreCase("getSellerRating")){
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int sellerID = Integer.parseInt(arguments.getString("sellerID"));
                response = getSellerRating(sellerID);
            }
        } catch (JSONException exception){
            response = "{}";
        }
        return response;
    }

    public static void main(String[] args) {
        for(int i=0;i<100;i++) {
            int finalI = i;
            Thread thread = new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        ServerSideBuyersInterface serverSideBuyersInterface = new ServerSideBuyersInterface(SERVER_SIDE_SELLER_INTF_PORT+200+ finalI);
                        JSONObject createAccountRequestJSON = new JSONObject();
                        createAccountRequestJSON.put("function", "createAccount");
                        JSONObject argumentsJSON = new JSONObject();
                        argumentsJSON.put("username", "Buyer" + finalI);
                        argumentsJSON.put("password", "password");
                        argumentsJSON.put("buyerName", "buyerName");
                        createAccountRequestJSON.put("arguments", argumentsJSON);
                        String response = serverSideBuyersInterface.processClientRequest(createAccountRequestJSON.toString());
                        JSONObject sellerRatingJSON = new JSONObject();
                        sellerRatingJSON.put("sellerID",""+(finalI+1));
                        JSONObject getSellerRatingJSON = new JSONObject();
                        getSellerRatingJSON.put("function", "getSellerRating");
                        getSellerRatingJSON.put("arguments",sellerRatingJSON);
                        System.out.println(serverSideBuyersInterface.processClientRequest(getSellerRatingJSON.toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();

        }
    }
}
