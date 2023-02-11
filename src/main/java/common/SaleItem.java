package common;

import java.io.Serializable;
import java.util.List;

public class SaleItem implements Serializable {
    final String itemName;
    final int category;
    final ItemID itemID;
    final List<String> keywords;
    final boolean isNew;
    final float itemPrice;

    public SaleItem(String itemName, int category, ItemID itemID, List<String> keywords, boolean isNew, float itemPrice) {
        this.itemName = itemName;
        this.category = category;
        this.itemID = itemID;
        this.keywords = keywords;
        this.isNew = isNew;
        this.itemPrice = itemPrice;
    }
}

