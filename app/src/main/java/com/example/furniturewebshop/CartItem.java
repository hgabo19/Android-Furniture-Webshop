package com.example.furniturewebshop;

public class CartItem {
    private String itemId;
    private String userId;
    private int cartCount;

    public CartItem() {}

    public CartItem(String itemId, String userid, int cartCount) {
        this.itemId = itemId;
        this.userId = userid;
        this.cartCount = cartCount;
    }

    public String getItemId(){
        return itemId;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(String id){
        this.itemId = id;
    }

    public int getCartCount() {
        return cartCount;
    }
}
