package seller;

import common.ClientDelegate;
import common.ItemID;
import common.SaleItem;
import common.Server;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static common.Utils.*;

public class ServerSideSellersInterface extends Server implements SellersInterface {

    public ServerSideSellersInterface(int port) throws IOException {
        super(port);
    }

    @Override
    public String createAccount(String username, String password, String sellerName) {
        if(!accountExists(username)){
            return "{}";
        } else {
            return createNewAccount(username, password, sellerName);
        }
    }

    private boolean accountExists(String username) {
        try{
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1",CUSTOMER_DB_PORT);
            String request = "SELECT * from Login where username = \""+username+"\"";
            String response = clientDelegate.sendRequest(request);
            JSONArray jsonArray = new JSONArray(response);
            return jsonArray.isEmpty();
        } catch (IOException exception){
            return false;
        }
    }

    private static String createNewAccount(String username, String password, String sellerName) {
        try {
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT) ;
            String request = "INSERT INTO Login(\"username\",\"password\",\"userType\") VALUES (\"" +
                    username + "\", \"" +
                    password + "\", \"seller\")";
            clientDelegate.sendRequest(request);
        } catch (IOException exception){
            exception.printStackTrace();
        }

        String userIDResponse = fetchUserID(username, password);
        JSONObject userIDJSON = new JSONObject(userIDResponse);
        int userID = userIDJSON.getInt("userID");
        try {
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT) ;
            String request = "INSERT INTO Sellers(\"sellerID\",\"sellerName\") VALUES (" +
                    userID + ", \"" +
                    sellerName + "\")";
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
    public String logout(int sellerID) {
        return "{}";
    }

    @Override
    public String getSellerRating(int sellerID) {
        return fetchSellerRating(sellerID);
    }

    @Override
    public String putItemForSale(SaleItem item) {

        return null;
    }

    @Override
    public String changeSalePriceOfItem(ItemID itemID, float newPrice) {

        return null;
    }

    @Override
    public String removeItemFromSale() {

        return null;
    }

    @Override
    public String displayItemsOnSale() {

        return null;
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
                String sellerName = arguments.getString("sellerName");
                response = createAccount(username, password, sellerName);
            } else if(invokedFunction.equalsIgnoreCase("login")){
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                String username = arguments.getString("username");
                String password = arguments.getString("password");
                response = login(username, password);
            } else if(invokedFunction.equalsIgnoreCase("getSellerRating")){
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int userID = Integer.parseInt(arguments.getString("sellerID"));
                response = getSellerRating(userID);
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
                        ServerSideSellersInterface serverSideSellersInterface = new ServerSideSellersInterface(SERVER_SIDE_SELLER_INTF_PORT+100+ finalI);
                        JSONObject createAccountRequestJSON = new JSONObject();
                        createAccountRequestJSON.put("function", "createAccount");
                        JSONObject argumentsJSON = new JSONObject();
                        argumentsJSON.put("username", "Seller" + finalI);
                        argumentsJSON.put("password", "password");
                        argumentsJSON.put("sellerName", "sellerName");
                        createAccountRequestJSON.put("arguments", argumentsJSON);
                        String response = serverSideSellersInterface.processClientRequest(createAccountRequestJSON.toString());
                        JSONObject userIDJSON = new JSONObject(response);
                        JSONObject sellerRatingJSON = new JSONObject();
                        sellerRatingJSON.put("sellerID",userIDJSON.getString("userID"));
                        JSONObject getSellerRatingJSON = new JSONObject();
                        getSellerRatingJSON.put("function", "getSellerRating");
                        getSellerRatingJSON.put("arguments",sellerRatingJSON);
                        System.out.println(serverSideSellersInterface.processClientRequest(getSellerRatingJSON.toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();

        }
    }
}
