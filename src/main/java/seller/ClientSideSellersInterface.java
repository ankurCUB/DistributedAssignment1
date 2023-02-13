package seller;

import common.ClientDelegate;
import common.SaleItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import static common.Utils.CUSTOMER_DB_PORT;

public class ClientSideSellersInterface implements SellersInterface {
    @Override
    public String createAccount(String username, String password, String sellerName) {
        JSONObject createAccountRequestJSON = new JSONObject();
        JSONObject argumentsJSON = new JSONObject();
        createAccountRequestJSON.put("function", "createAccount");
        argumentsJSON.put("username", username);
        argumentsJSON.put("password", password);
        argumentsJSON.put("sellerName", sellerName);
        createAccountRequestJSON.put("arguments", argumentsJSON);
        return sendRequest(createAccountRequestJSON.toString());
    }

    @Override
    public String login(String username, String password) {
        JSONObject loginRequestJSON = new JSONObject();
        loginRequestJSON.put("function", "login");
        JSONObject argumentsJSON = new JSONObject();
        argumentsJSON.put("username", username);
        argumentsJSON.put("password", password);
        loginRequestJSON.put("arguments", argumentsJSON);
        return sendRequest(loginRequestJSON.toString());
    }

    @Override
    public String logout(int sellerID) {
        return null;
    }

    @Override
    public String getSellerRating(int sellerID) {
        JSONObject argumentsJSON = new JSONObject();
        argumentsJSON.put("sellerID", sellerID);
        JSONObject getSellerRatingJSON = new JSONObject();
        getSellerRatingJSON.put("function", "getSellerRating");
        getSellerRatingJSON.put("arguments", argumentsJSON);
        return sendRequest(getSellerRatingJSON.toString());
    }

    @Override
    public String putItemForSale(SaleItem item) {
        JSONObject argumentsJSON = new JSONObject();
        JSONObject putItemForSaleRequestJSON = new JSONObject();
        putItemForSaleRequestJSON.put("function", "putItemForSale");
        argumentsJSON.put("itemName",item.itemName);
        argumentsJSON.put("category",item.category);
        argumentsJSON.put("keywords",item.keywords);
        argumentsJSON.put("isNew",item.isNew);
        argumentsJSON.put("itemPrice",item.itemPrice);
        argumentsJSON.put("sellerID",item.sellerID);
        argumentsJSON.put("quantity", item.quantity);
        putItemForSaleRequestJSON.put("arguments",argumentsJSON);
        return sendRequest(putItemForSaleRequestJSON.toString());
    }

    @Override
    public String changeSalePriceOfItem(int sellerID, int itemID, float newPrice) {
        return null;
    }

    @Override
    public String removeItemFromSale(int sellerID, int itemID, int quantity) {
        JSONObject argumentsJSON = new JSONObject();
        JSONObject removeItemFromSaleRequestJSON = new JSONObject();
        removeItemFromSaleRequestJSON.put("function", "removeItemFromSale");
        argumentsJSON.put("itemID",itemID);
        argumentsJSON.put("sellerID",sellerID);
        argumentsJSON.put("quantity", quantity);
        removeItemFromSaleRequestJSON.put("arguments",argumentsJSON);
        return sendRequest(removeItemFromSaleRequestJSON.toString());
    }

    @Override
    public String displayItemsOnSale(int sellerID) {
        JSONObject argumentsJSON = new JSONObject();
        JSONObject displayItemsOnSaleRequestJSON = new JSONObject();
        displayItemsOnSaleRequestJSON.put("function", "displayItemsOnSale");
        argumentsJSON.put("sellerID",sellerID);
        displayItemsOnSaleRequestJSON.put("arguments",argumentsJSON);
        return sendRequest(displayItemsOnSaleRequestJSON.toString());
    }

    private String sendRequest(String request) {
        try {
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT);
            String response = clientDelegate.sendRequest(request);
            JSONArray jsonArray = new JSONArray(response);
            return jsonArray.toString();
        } catch (IOException exception) {
            exception.printStackTrace();
            return "{}";
        }
    }
}
