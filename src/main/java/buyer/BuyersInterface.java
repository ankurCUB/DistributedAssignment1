package buyer;

public interface BuyersInterface {
    String createAccount(String username, String password, String buyerName);
    String login(String username, String password);
    String logout(int buyerID);
    String getSellerRating(int sellerID);
    String addItemToShoppingCart(int userID, String itemID, int quantity);
    /*
    * Remove item from the shopping cart: provide item id and quantity
    * Clear the shopping cart
    * Display shopping cart*/
}