package seller;

import common.ItemID;
import common.SaleItem;

public interface SellersInterface {
    String createAccount(String username, String password, String sellerName);
    String login(String username, String password);
    String logout(int sellerID);
    String getSellerRating(int sellerID);
    String putItemForSale(SaleItem item);
    String changeSalePriceOfItem(ItemID itemID, float newPrice);
    String removeItemFromSale();
    String displayItemsOnSale();
}