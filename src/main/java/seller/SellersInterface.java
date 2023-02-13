package seller;

import common.SaleItem;

public interface SellersInterface {
    String createAccount(String username, String password, String sellerName);
    String login(String username, String password);
    String logout(int sellerID);
    String getSellerRating(int sellerID);
    String putItemForSale(SaleItem item);
    String changeSalePriceOfItem(int sellerID, int itemID, float newPrice);
    String removeItemFromSale(int sellerID, int itemID, int quantity);
    String displayItemsOnSale(int sellerID);
}