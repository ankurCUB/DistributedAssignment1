package common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public interface Utils {

    int CUSTOMER_DB_PORT = 5003;

    int PRODUCT_DB_PORT = 5004;

    int SERVER_SIDE_SELLER_INTF_PORT = 5005;

    static String fetchUserID(String username, String password) {
        try {
            ClientDelegate clientDelegate = new ClientDelegate("127.0.0.1", CUSTOMER_DB_PORT) ;
            String request = "SELECT userID FROM Login WHERE \"username\" = \""+username+"\" and \"password\" = "+"\""+password+"\"";
            String response = clientDelegate.sendRequest(request);
            JSONArray responseJSON = new JSONArray(response);
            if(!responseJSON.isEmpty()){
                JSONObject userIDJSON = (JSONObject) responseJSON.get(0);
                return userIDJSON.toString();
            } else{
                return "{}";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    static String fetchSellerRating(int sellerID) {
        ClientDelegate clientDelegate = null;
        try {
            clientDelegate = new ClientDelegate("127.0.0.1",CUSTOMER_DB_PORT);
            String request = "SELECT thumbsUp, thumbsDown from Sellers where sellerID = "+sellerID;
            String response = clientDelegate.sendRequest(request);
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            int thumbsUp = Integer.parseInt(jsonObject.getString("thumbsUp"));
            int thumbsDown = Integer.parseInt(jsonObject.getString("thumbsDown"));
            float rating = 0;
            if(thumbsUp+thumbsDown != 0){
                rating = 10*(thumbsUp-thumbsDown)/((float)(thumbsUp+thumbsDown));
            }
            JSONObject responseJSON = new JSONObject();
            responseJSON.put("rating",rating);
            return responseJSON.toString();
        } catch (IOException e) {
            return "{}";
        }
    }
}
