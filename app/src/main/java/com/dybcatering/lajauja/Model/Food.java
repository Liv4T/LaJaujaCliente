package com.dybcatering.lajauja.Model;

public class Food {

    private String image, food, price, discount, description, menuId;
    public Food() {
    }

    public Food(String image, String food, String price, String discount, String description, String menuId) {
        this.image = image;
        this.food = food;
        this.price = price;
        this.discount = discount;
        this.description = description;
        this.menuId = menuId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
