package com.example.furniturewebshop;

public class FurnitureItem {
    private String name;
    private String description;
    private int price;
    private final int imageResource;

    public FurnitureItem(String name, String description, int price, int imageResource) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public int getImageResource() {
        return imageResource;
    }
}
