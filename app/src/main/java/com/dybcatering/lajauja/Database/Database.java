package com.dybcatering.lajauja.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.dybcatering.lajauja.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME= "LaJaujaDB.db";
    private static final int DB_VER = 5;

    public Database(Context context) {
        super(context, DB_NAME,null , DB_VER);
    }

    public List<Order> getCarts(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ID","ProductName", "ProductId", "Quantity", "Price", "Discount", "Image", "Accomp", "Acomp2", "Acomp3"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect,null, null, null, null, null);

        final List<Order> result = new ArrayList<>();
        if (c.moveToFirst()){
            do{
                result.add(new Order(
                        c.getInt(c.getColumnIndex("ID")),
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                        c.getString(c.getColumnIndex("Image")),
                        c.getString(c.getColumnIndex("Accomp")),
                        c.getString(c.getColumnIndex("Acomp2")),
                        c.getString(c.getColumnIndex("Acomp3"))
                ));
            }while (c.moveToNext());
        }
        db.close();
        c.close();
        return result;
    }

    public void addToCart(Order order){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO OrderDetail(ProductId, ProductName, Quantity, Price, Discount, Image, Accomp, Acomp2, Acomp3) VALUES ('%s','%s','%s','%s','%s', '%s', '%s','%s','%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage(),
                order.getAccomp(),
                order.getAccomp2(),
                order.getAccomp3());
        db.execSQL(query);
        db.close();
    }

    public void cleanCart(){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);
        db.close();

    }

    public void addToFavorites(String foodId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites(FoodId) VALUES ('%s');", foodId);
        db.execSQL(query);

    }

    public void removeFromFavorites(String foodId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE FoodId='%s';", foodId);
        db.execSQL(query);

    }

    public boolean isFavorite(String foodId){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorites WHERE FoodId='%s';", foodId);
        db.execSQL(query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <=0 ){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;

    }


    public int getCountCart() {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail");
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                count = cursor.getInt(0);
            }while (cursor.moveToNext());

        }
        return count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= %s WHERE ID = %d", order.getQuantity(), order.getID());
        db.execSQL(query);

    }
}
