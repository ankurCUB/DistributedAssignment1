import common.SaleItem;
import org.json.JSONObject;
import seller.ClientSideSellersInterface;
import seller.SellersInterface;

public class MarketPlace {
    public static void main(String[] args) {
        for(int i=0;i<100;i++) {
            int finalI = i;
            Thread sellerThread = new Thread(){
                @Override
                public void run() {
                    super.run();
                    SellersInterface clientSideSellersInterface = new ClientSideSellersInterface();

                    String response = "";
                    JSONObject userIDJSON;
                    int sellerID ;

                    response = clientSideSellersInterface.createAccount("Seller" + finalI, "password", "sellerName");
                    userIDJSON = new JSONObject(response);
                    sellerID = Integer.parseInt(userIDJSON.getString("userID"));

                    response = clientSideSellersInterface.login("Seller" + finalI,"password");
                    userIDJSON = new JSONObject(response);
                    sellerID = Integer.parseInt(userIDJSON.getString("userID"));

                    response = clientSideSellersInterface.getSellerRating(sellerID);

                    response = clientSideSellersInterface.putItemForSale(new SaleItem("itemName", 0 , "kw1, kw2, kw3", 1, 9.5f, sellerID, finalI%20));

                    response = clientSideSellersInterface.removeItemFromSale(sellerID, finalI%10, finalI%20 - 2);

                    response = clientSideSellersInterface.displayItemsOnSale(sellerID);

                }
            };
            sellerThread.start();

        }
    }
}
