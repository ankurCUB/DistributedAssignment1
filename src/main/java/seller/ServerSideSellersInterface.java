package seller;

import common.ClientDelegate;
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
        ClientDelegate clientDelegate = null;
        try {
            clientDelegate = new ClientDelegate("127.0.0.1", PRODUCT_DB_PORT);
            String request = "INSERT INTO Products(\"itemName\",\"category\",\"keyWords\",\"isNew\",\"itemPrice\",\"sellerID\",\"quantity\") VALUES (\"" +
                    item.itemName + "\", "+item.category+", \""+item.keywords+"\", "+item.isNew+", "+item.itemPrice+", "+item.sellerID+", "+item.quantity+")";
            clientDelegate.sendRequest(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    @Override
    public String changeSalePriceOfItem(int sellerID, int itemID, float newPrice) {
        ClientDelegate clientDelegate = null;
        try {
            clientDelegate = new ClientDelegate("127.0.0.1", PRODUCT_DB_PORT);
            String request = "UPDATE Products SET \"itemPrice\" ="+newPrice+" WHERE \"itemID\" = "+itemID+" AND \"sellerID\" = "+sellerID;
            clientDelegate.sendRequest(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    @Override
    public String removeItemFromSale(int sellerID, int itemID, int quantity) {

        ClientDelegate clientDelegate = null;
        try {
            clientDelegate = new ClientDelegate("127.0.0.1", PRODUCT_DB_PORT);
            String request = "SELECT quantity FROM Products WHERE \"itemID\" = "+itemID+" AND \"sellerID\" = "+sellerID;
            String adjResponse = clientDelegate.sendRequest(request);
            JSONArray responseArray = new JSONArray(adjResponse);

            if(responseArray.length()==0){
                return "{}";
            }

            JSONObject jsonObject = responseArray.getJSONObject(0);
            int currentSaleQuantityForItem = Integer.parseInt(jsonObject.getString("quantity"));

            String removeFromSaleRequest = "";
            if(currentSaleQuantityForItem<quantity){
                return "{}";
            } else if(currentSaleQuantityForItem==quantity){
                removeFromSaleRequest = "DELETE FROM Products WHERE \"sellerID\" = "+sellerID+" and \"itemID\" = \""+itemID+"\"";
            } else{
                removeFromSaleRequest = "UPDATE ShoppingCart SET \"quantity\" = "+(currentSaleQuantityForItem-quantity) + " WHERE \"sellerID\" = "+sellerID+" and \"itemID\" = \""+itemID+"\"";
            }

            clientDelegate = new ClientDelegate("127.0.0.1", PRODUCT_DB_PORT);
            clientDelegate.sendRequest(removeFromSaleRequest);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    @Override
    public String displayItemsOnSale(int sellerID) {
        ClientDelegate clientDelegate = null;
        try {
            clientDelegate = new ClientDelegate("127.0.0.1", PRODUCT_DB_PORT);
            String request = "SELECT * FROM Products WHERE \"sellerID\" = "+sellerID;
            return clientDelegate.sendRequest(request);
        } catch (IOException e) {
            e.printStackTrace();
            return "{}";
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
            } else if(invokedFunction.equalsIgnoreCase("putItemForSale")){
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                String itemName = arguments.getString("itemName");
                int category = arguments.getInt("category");
                String keywords = arguments.getString("keywords");
                int isNew = arguments.getInt("isNew");
                float itemPrice = arguments.getFloat("itemPrice");
                int sellerID = arguments.getInt("sellerID");
                int quantity = arguments.getInt("quantity");
                response = putItemForSale(new SaleItem(itemName, category, keywords, isNew, itemPrice, sellerID, quantity ));
            } else if(invokedFunction.equalsIgnoreCase("removeItemFromSale")){
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int itemID = arguments.getInt("itemID");
                int sellerID = arguments.getInt("sellerID");
                int quantity = arguments.getInt("quantity");
                response = removeItemFromSale(sellerID, itemID, quantity);
            } else if(invokedFunction.equalsIgnoreCase("displayItemsOnSale")){
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                int sellerID = arguments.getInt("sellerID");
                response = displayItemsOnSale(sellerID);
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

                        JSONObject argumentsJSON = new JSONObject();
                        String response = "";
                        JSONObject userIDJSON;
                        int sellerID ;

//                        JSONObject createAccountRequestJSON = new JSONObject();
//                        createAccountRequestJSON.put("function", "createAccount");
//                        argumentsJSON.put("username", "Seller" + finalI);
//                        argumentsJSON.put("password", "password");
//                        argumentsJSON.put("sellerName", "sellerName");
//                        createAccountRequestJSON.put("arguments", argumentsJSON);
//                        response = serverSideSellersInterface.processClientRequest(createAccountRequestJSON.toString());
//                        userIDJSON = new JSONObject(response);
//                        int sellerID = Integer.parseInt(userIDJSON.getString("userID"));

                        JSONObject loginRequestJSON = new JSONObject();
                        loginRequestJSON.put("function", "login");
                        argumentsJSON = new JSONObject();
                        argumentsJSON.put("username", "Seller" + finalI);
                        argumentsJSON.put("password", "password");
                        loginRequestJSON.put("arguments", argumentsJSON);
                        response = serverSideSellersInterface.processClientRequest(loginRequestJSON.toString());
                        userIDJSON = new JSONObject(response);
                        sellerID = Integer.parseInt(userIDJSON.getString("userID"));


//                        JSONObject sellerRatingJSON = new JSONObject();
//                        sellerRatingJSON.put("sellerID",userIDJSON.getString("userID"));
//                        JSONObject getSellerRatingJSON = new JSONObject();
//                        getSellerRatingJSON.put("function", "getSellerRating");
//                        getSellerRatingJSON.put("arguments",sellerRatingJSON);
//                        System.out.println(serverSideSellersInterface.processClientRequest(getSellerRatingJSON.toString()));

                        JSONObject putItemForSaleRequestJSON = new JSONObject();
                        putItemForSaleRequestJSON.put("function", "putItemForSale");
                        argumentsJSON.put("itemName","itemName");
                        argumentsJSON.put("category",0);
                        argumentsJSON.put("itemID",finalI%10);
                        argumentsJSON.put("keywords","kw1, kw2, kw3");
                        argumentsJSON.put("isNew",1);
                        argumentsJSON.put("itemPrice",9.5);
                        argumentsJSON.put("sellerID",sellerID);
                        argumentsJSON.put("quantity", finalI%20);
                        putItemForSaleRequestJSON.put("arguments",argumentsJSON);
                        serverSideSellersInterface.processClientRequest(putItemForSaleRequestJSON.toString());

                        JSONObject removeItemFromSaleRequestJSON = new JSONObject();
                        removeItemFromSaleRequestJSON.put("function", "removeItemFromSale");
                        argumentsJSON.put("itemID",finalI%10);
                        argumentsJSON.put("sellerID",sellerID);
                        argumentsJSON.put("quantity", finalI%20 - 2);
                        removeItemFromSaleRequestJSON.put("arguments",argumentsJSON);
                        serverSideSellersInterface.processClientRequest(removeItemFromSaleRequestJSON.toString());

                        JSONObject displayItemsOnSaleRequestJSON = new JSONObject();
                        displayItemsOnSaleRequestJSON.put("function", "displayItemsOnSale");
                        argumentsJSON.put("sellerID",sellerID);
                        displayItemsOnSaleRequestJSON.put("arguments",argumentsJSON);
                        System.out.println(serverSideSellersInterface.processClientRequest(displayItemsOnSaleRequestJSON.toString()));



                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();

        }
    }
}
