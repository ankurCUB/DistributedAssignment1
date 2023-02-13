package buyer;

public interface BuyersInterface {
    String createAccount(String username, String password, String buyerName);
    String login(String username, String password);
    String logout(int buyerID);
    String getSellerRating(int sellerID);
    String addItemToShoppingCart(int userID, String itemID, int quantity);
    String removeItemFromShoppingCart(int userID, String itemID, int quantity);
    String clearShoppingCart(int userID);
    String displayShoppingCart(int userID);
    String makePurchase();
    String getBuyerPurchaseHistory(int userID);
    String provideFeedback(int purchaseID, int feedback);
    /*
    * Search items for sale: provide an item category and up to five keywords*/
}