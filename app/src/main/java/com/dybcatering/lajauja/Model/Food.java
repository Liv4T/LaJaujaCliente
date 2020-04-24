package com.dybcatering.lajauja.Model;

public class Food {

    private String Image,Food,Price,Discount, Description, MenuId;

    public Food() {
    }

    public Food(String image, String food, String price, String discount, String description, String menuId) {
        Image = image;
        Food = food;
        Price = price;
        Discount = discount;
        Description = description;
        MenuId = menuId;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getFood() {
        return Food;
    }

    public void setFood(String food) {
        Food = food;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }
}
