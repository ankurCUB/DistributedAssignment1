package buyer;

import common.ClientDelegate;
import common.Server;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static common.Utils.*;

public class ServerSideBuyersInterface extends Server implements BuyersInterface {

    public ServerSideBuyersInterface(int port) throws IOException {
        super(port);
    }

    @Override
    public String createAccount(String username, String password, String buyerName) {
        try {
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT);
            String request = "INSERT INTO Login(\"username\",\"password\",\"userType\") VALUES (\"" +
                    username + "\", \"" +
                    password + "\", \"buyer\")";
            clientDelegate.sendRequest(request);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        String userIDResponse = fetchUserID(username, password);
        JSONObject userIDJSON = new JSONObject(userIDResponse);
        int userID = userIDJSON.getInt("userID");
        try {
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT);
            String request = "INSERT INTO Buyers(\"buyerID\",\"buyerName\") VALUES (" +
                    userID + ", \"" +
                    buyerName + "\")";
            clientDelegate.sendRequest(request);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return userIDResponse;
    }

    @Override
    public String login(String username, String password) {
        return fetchUserID(username, password);
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
    public String addItemToShoppingCart(int userID, int itemID, int quantity) {
        String response = "{}";
        int currentCartValueForItem = 0;
        try {
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT);
            String request = "SELECT quantity from ShoppingCart where \"userID\" = " + userID + " and \"itemID\" = \"" + itemID + "\"";
            String adjResponse = clientDelegate.sendRequest(request);

            String addToCartRequest = "";
            JSONArray jsonArray = new JSONArray(adjResponse);
            if (!jsonArray.isEmpty()) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                currentCartValueForItem = Integer.parseInt(jsonObject.getString("quantity"));
                addToCartRequest = "UPDATE ShoppingCart SET \"quantity\" = " + (currentCartValueForItem + quantity) + " WHERE \"userID\" = " + userID + " and \"itemID\" = \"" + itemID + "\"";
            } else {
                addToCartRequest = "INSERT INTO ShoppingCart VALUES(" + userID + ", \"" + itemID + "\", " + quantity + ")";
            }

            clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT);
            clientDelegate.sendRequest(addToCartRequest);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return response;
    }

    @Override
    public String removeItemFromShoppingCart(int userID, int itemID, int quantity) {
        String response = "{}";
        try {
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT);
            String request = "SELECT quantity from ShoppingCart where \"userID\" = " + userID + " and \"itemID\" = \"" + itemID + "\"";
            String adjResponse = clientDelegate.sendRequest(request);

            String removeFromCartRequest = "";
            JSONArray jsonArray = new JSONArray(adjResponse);

            if (jsonArray.length() == 0) {
                return "{}";
            }

            JSONObject jsonObject = jsonArray.getJSONObject(0);
            int currentCartValueForItem = Integer.parseInt(jsonObject.getString("quantity"));

            if (currentCartValueForItem < quantity) {
                return "{}";
            } else if (currentCartValueForItem == quantity) {
                removeFromCartRequest = "DELETE FROM ShoppingCart WHERE \"userID\" = " + userID + " and \"itemID\" = \"" + itemID + "\"";
            } else {
                removeFromCartRequest = "UPDATE ShoppingCart SET \"quantity\" = " + (currentCartValueForItem - quantity) + " WHERE \"userID\" = " + userID + " and \"itemID\" = \"" + itemID + "\"";
            }

            clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT);
            clientDelegate.sendRequest(removeFromCartRequest);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return response;
    }

    @Override
    public String clearShoppingCart(int buyerID) {
        String response = "{}";
        try {
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT);
            String request = "DELETE from ShoppingCart where \"userID\" = " + buyerID;
            response = clientDelegate.sendRequest(request);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return response;
    }

    @Override
    public String displayShoppingCart(int buyerID) {
        try {
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT);
            String request = "SELECT itemID, quantity from ShoppingCart where \"userID\" = " + buyerID;
            String adjResponse = clientDelegate.sendRequest(request);

            JSONArray shoppingCartResponseArray = new JSONArray(adjResponse);

            if (shoppingCartResponseArray.isEmpty()) {
                return "{}";
            }

            HashMap<Integer, Integer> shoppingCartMap = new LinkedHashMap<>();
            for (int i = 0; i < shoppingCartResponseArray.length(); i++) {
                JSONObject jsonObject = shoppingCartResponseArray.getJSONObject(i);
                int itemID = jsonObject.getInt("itemID");
                shoppingCartMap.put(itemID, jsonObject.getInt("quantity"));
            }

            return getProductDetails(shoppingCartMap).toString();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return "{}";
    }

    @Override
    public String makePurchase() {
        return null;
    }

    @Override
    public String getBuyerPurchaseHistory(int buyerID) {
        try {
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT);
            String request = "SELECT sellerID, quantity from PurchaseHistory where \"userID\" = " + buyerID;
            String adjResponse = clientDelegate.sendRequest(request);

            JSONArray purchaseHistoryResponseArray = new JSONArray(adjResponse);

            if (purchaseHistoryResponseArray.isEmpty()) {
                return "{}";
            }

            HashMap<Integer, Integer> purchaseHistoryMap = new LinkedHashMap<>();
            for (int i = 0; i < purchaseHistoryResponseArray.length(); i++) {
                JSONObject jsonObject = purchaseHistoryResponseArray.getJSONObject(i);
                int itemID = jsonObject.getInt("itemID");
                purchaseHistoryMap.put(itemID, jsonObject.getInt("quantity"));
            }

            return getProductDetails(purchaseHistoryMap).toString();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return "{}";
    }

    @Override
    public String provideFeedback(int purchaseID, int feedback) {
        try {
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT);
            String request = "SELECT sellerID, feedback from PurchaseHistory where \"purchaseID\" = " + purchaseID;
            String adjResponse = clientDelegate.sendRequest(request);

            JSONArray purchaseHistoryResponseArray = new JSONArray(adjResponse);

            if (purchaseHistoryResponseArray.isEmpty()) {
                return "{}";
            }

            JSONObject purchaseHistoryObject = purchaseHistoryResponseArray.getJSONObject(0);
            if (Integer.parseInt(purchaseHistoryObject.getString("feedback")) == 0) {
                clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT);
                request = "UPDATE PurchaseHistory SET \"feedback\" = " + feedback + "WHERE \"purchaseID\" = " + purchaseID;
                clientDelegate.sendRequest(request);
            }

            int sellerID = Integer.parseInt(purchaseHistoryObject.getString("sellerID"));

            updateSellerRating(sellerID, feedback);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return "{}";
    }

    private void updateSellerRating(int sellerID, int feedback) {
        try {
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT);
            String rating = "";
            if(feedback==1){
                rating = "thumbsUp";
            } else {
                rating = "thumbsDown";
            }
            String request = "SELECT "+rating+" FROM Sellers WHERE \"sellerID\" = "+sellerID;
            String response = clientDelegate.sendRequest(request);

            JSONArray array = new JSONArray(response);
            int newRating = array.getJSONObject(0).getInt(rating) + 1;

            clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT);
            request = "UPDATE Sellers SET \""+ rating+"\" = "+newRating+"WHERE \"sellerID\" = "+sellerID;
            clientDelegate.sendRequest(request);
        } catch (IOException exception){
            exception.printStackTrace();
        }
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
            } else if (invokedFunction.equalsIgnoreCase("login")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                String username = arguments.getString("username");
                String password = arguments.getString("password");
                response = login(username, password);
            } else if (invokedFunction.equalsIgnoreCase("addItemToShoppingCart")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int buyerID = arguments.getInt("buyerID");
                int itemID = arguments.getInt("itemID");
                int quantity = arguments.getInt("quantity");
                response = addItemToShoppingCart(buyerID, itemID, quantity);
            } else if (invokedFunction.equalsIgnoreCase("removeItemFromShoppingCart")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int buyerID = arguments.getInt("buyerID");
                int itemID = arguments.getInt("itemID");
                int quantity = arguments.getInt("quantity");
                response = removeItemFromShoppingCart(buyerID, itemID, quantity);
            } else if (invokedFunction.equalsIgnoreCase("clearShoppingCart")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int buyerID = arguments.getInt("buyerID");
                response = clearShoppingCart(buyerID);
            } else if (invokedFunction.equalsIgnoreCase("getSellerRating")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int sellerID = Integer.parseInt(arguments.getString("sellerID"));
                response = getSellerRating(sellerID);
            } else if (invokedFunction.equalsIgnoreCase("displayShoppingCart")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int buyerID = Integer.parseInt(arguments.getString("buyerID"));
                response = getSellerRating(buyerID);
            } else if (invokedFunction.equalsIgnoreCase("getBuyerPurchaseHistory")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int buyerID = Integer.parseInt(arguments.getString("buyerID"));
                response = getSellerRating(buyerID);
            } else if (invokedFunction.equalsIgnoreCase("provideFeedback")) {
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int buyerID = Integer.parseInt(arguments.getString("buyerID"));
                response = getSellerRating(buyerID);
            }
        } catch (JSONException exception) {
            response = "{}";
        }
        return response;
    }

    public static void main(String[] args) {

        try {
            ServerSideBuyersInterface serverSideBuyersInterface = new ServerSideBuyersInterface(SERVER_SIDE_BUYER_INTF_PORT);
            serverSideBuyersInterface.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
