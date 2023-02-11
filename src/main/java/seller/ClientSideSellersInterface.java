package seller;

import common.ItemID;
import common.SaleItem;

public class ClientSideSellersInterface implements SellersInterface {
    @Override
    public String createAccount(String username, String password, String sellerName) {

        return username;
    }

    @Override
    public String login(String username, String password) {
        return null;
    }

    @Override
    public String logout(int sellerID) {

        return null;
    }

    @Override
    public String getSellerRating(int sellerID) {

        return null;
    }

    @Override
    public String putItemForSale(SaleItem item) {

        return null;
    }

    @Override
    public String changeSalePriceOfItem(ItemID itemID, float newPrice) {

        return null;
    }

    @Override
    public String removeItemFromSale() {

        return null;
    }

    @Override
    public String displayItemsOnSale() {

        return null;
    }
}
