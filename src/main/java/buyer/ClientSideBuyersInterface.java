package buyer;

import common.Client;
import org.json.JSONObject;

import static common.Utils.SERVER_SIDE_BUYER_INTF_PORT;

public class ClientSideBuyersInterface extends Client implements BuyersInterface {

    public ClientSideBuyersInterface() {
        address = "127.0.0.1";
        port = SERVER_SIDE_BUYER_INTF_PORT;
    }

    @Override
    public String createAccount(String username, String password, String buyerName) {
        JSONObject argumentsJSON = new JSONObject();
        JSONObject createAccountRequestJSON = new JSONObject();
        createAccountRequestJSON.put("function", "createAccount");
        argumentsJSON.put("username", username);
        argumentsJSON.put("password", password);
        argumentsJSON.put("buyerName", buyerName);
        createAccountRequestJSON.put("arguments", argumentsJSON);
        return sendRequest(createAccountRequestJSON.toString());
    }

    @Override
    public String login(String username, String password) {
        JSONObject argumentsJSON = new JSONObject();
        JSONObject loginRequestJSON = new JSONObject();
        loginRequestJSON.put("function", "login");
        argumentsJSON.put("username", username);
        argumentsJSON.put("password", password);
        loginRequestJSON.put("arguments", argumentsJSON);
        return sendRequest(loginRequestJSON.toString());
    }

    @Override
    public String logout(int buyerID) {
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
    public String addItemToShoppingCart(int userID, int itemID, int quantity) {
        JSONObject addItemToShoppingCartJSON = new JSONObject();
        addItemToShoppingCartJSON.put("function", "addItemToShoppingCart");
        JSONObject argumentsJSON = new JSONObject();
        argumentsJSON.put("buyerID", userID);
        argumentsJSON.put("itemID", itemID);
        argumentsJSON.put("quantity", quantity);
        addItemToShoppingCartJSON.put("arguments", argumentsJSON);
        return sendRequest(addItemToShoppingCartJSON.toString());
    }

    @Override
    public String removeItemFromShoppingCart(int userID, int itemID, int quantity) {
        JSONObject argumentsJSON = new JSONObject();
        JSONObject removeItemFromShoppingCartJSON = new JSONObject();
        removeItemFromShoppingCartJSON.put("function", "removeItemFromShoppingCart");
        argumentsJSON.put("buyerID", userID);
        argumentsJSON.put("itemID", itemID);
        argumentsJSON.put("quantity", quantity);
        removeItemFromShoppingCartJSON.put("arguments", argumentsJSON);
        return sendRequest(removeItemFromShoppingCartJSON.toString());
    }

    @Override
    public String clearShoppingCart(int buyerID) {
        JSONObject argumentsJSON = new JSONObject();
        JSONObject clearShoppingCartJSON = new JSONObject();
        clearShoppingCartJSON.put("function", "clearShoppingCart");
        argumentsJSON.put("buyerID", buyerID);
        clearShoppingCartJSON.put("arguments", argumentsJSON);
        return sendRequest(clearShoppingCartJSON.toString());
    }

    @Override
    public String displayShoppingCart(int buyerID) {
        JSONObject argumentsJSON = new JSONObject();
        JSONObject displayShoppingCartJSON = new JSONObject();
        displayShoppingCartJSON.put("function", "displayShoppingCart");
        argumentsJSON.put("buyerID", buyerID);
        displayShoppingCartJSON.put("arguments", argumentsJSON);
        return sendRequest(displayShoppingCartJSON.toString());
    }

    @Override
    public String makePurchase() {
        return null;
    }

    @Override
    public String getBuyerPurchaseHistory(int buyerID) {
        JSONObject argumentsJSON = new JSONObject();
        JSONObject getBuyerPurchaseHistoryJSON = new JSONObject();
        getBuyerPurchaseHistoryJSON.put("function", "getBuyerPurchaseHistory");
        argumentsJSON.put("buyerID", buyerID);
        getBuyerPurchaseHistoryJSON.put("arguments", argumentsJSON);
        return sendRequest(getBuyerPurchaseHistoryJSON.toString());
    }

    @Override
    public String provideFeedback(int purchaseID, int feedback) {
        JSONObject argumentsJSON = new JSONObject();
        JSONObject provideFeedbackJSON = new JSONObject();
        provideFeedbackJSON.put("function", "provideFeedback");
        argumentsJSON.put("purchaseID", purchaseID);
        argumentsJSON.put("feedback", feedback);
        provideFeedbackJSON.put("arguments", argumentsJSON);
        return sendRequest(provideFeedbackJSON.toString());
    }

    @Override
    public String searchItemsForSale(int category, String keywords) {
        JSONObject argumentsJSON = new JSONObject();
        JSONObject searchItemsForSaleJSON = new JSONObject();
        searchItemsForSaleJSON.put("function", "searchItemsForSale");
        argumentsJSON.put("category", category);
        argumentsJSON.put("keywords", keywords);
        searchItemsForSaleJSON.put("arguments", argumentsJSON);
        return sendRequest(searchItemsForSaleJSON.toString());
    }
}
