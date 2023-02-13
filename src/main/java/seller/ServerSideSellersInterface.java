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
        try {
            ServerSideSellersInterface serverSideSellersInterface = new ServerSideSellersInterface(SERVER_SIDE_SELLER_INTF_PORT);
            serverSideSellersInterface.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
