package com.dybcatering.lajauja.Model;

public class Order {
    private int ID;
    private String ProductId;
    private String ProductName;
    private String Quantity;
    private String Price;
    private String Discount;
    private String Image;
    private String Accomp;
    private String Accomp2;
    private String Accomp3;

    public Order() {
    }


    public Order( String productId, String productName, String quantity, String price, String discount, String image, String accomp, String accomp2, String accomp3) {

        ProductId = productId;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        Image = image;
        Accomp = accomp;
        Accomp2 = accomp2;
        Accomp3 = accomp3;
    }

    public Order(int ID, String productId, String productName, String quantity, String price, String discount, String image, String accomp, String accomp2, String accomp3) {
        this.ID = ID;
        ProductId = productId;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        Image = image;
        Accomp = accomp;
        Accomp2 = accomp2;
        Accomp3 = accomp3;

    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
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

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getAccomp() {
        return Accomp;
    }

    public void setAccomp(String accomp) {
        Accomp = accomp;
    }

    public String getAccomp2() {
        return Accomp2;
    }

    public void setAccomp2(String accomp2) {
        Accomp2 = accomp2;
    }

    public String getAccomp3() {
        return Accomp3;
    }

    public void setAccomp3(String accomp3) {
        Accomp3 = accomp3;
    }
}
