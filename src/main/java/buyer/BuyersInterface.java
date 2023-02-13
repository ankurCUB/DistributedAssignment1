package buyer;

public interface BuyersInterface {
    String createAccount(String username, String password, String buyerName);
    String login(String username, String password);
    String logout(int buyerID);
    String getSellerRating(int sellerID);
    String addItemToShoppingCart(int userID, int itemID, int quantity);
    String removeItemFromShoppingCart(int userID, int itemID, int quantity);
    String clearShoppingCart(int buyerID);
    String displayShoppingCart(int buyerID);
    String makePurchase();
    String getBuyerPurchaseHistory(int buyerID);
    String provideFeedback(int purchaseID, int feedback);
    /*
    * Search items for sale: provide an item category and up to five keywords*/
}