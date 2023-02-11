package buyer;

public interface BuyersInterface {
    String createAccount(String username, String password, String buyerName);
    String login(String username, String password);
    String logout(int buyerID);
    String getSellerRating(int sellerID);
    String addItemToShoppingCart(int userID, String itemID, int quantity);
    String removeItemFromShoppingCart(int userID, String itemID, int quantity);

    String clearShoppingCart(int userID);
    /*
    * Clear the shopping cart
    * Display shopping cart*/
}