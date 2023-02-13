package common;

import java.io.Serializable;
import java.util.List;

public class SaleItem implements Serializable {
    public final String itemName;
    public final int category;
    public final String keywords;
    public final int isNew;
    public final float itemPrice;
    public final int sellerID;
    public final int quantity;

    public SaleItem(String itemName, int category, String keywords, int isNew, float itemPrice, int sellerID, int quantity) {
        this.itemName = itemName;
        this.category = category;
        this.keywords = keywords;
        this.isNew = isNew;
        this.itemPrice = itemPrice;
        this.sellerID = sellerID;
        this.quantity = quantity;
    }
}

