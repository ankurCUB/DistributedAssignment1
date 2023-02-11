package buyer;

public interface BuyersInterface {
    String createAccount(String username, String password, String buyerName);
    String login(String username, String password);
    String logout(int buyerID);
    String getSellerRating(int sellerID);
}