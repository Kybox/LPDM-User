package com.lpdm.msuser.msproduct;

import com.lpdm.msuser.msauthentication.AppUserBean;

public class ProductBean {

    private Integer id;

    private String name;

    private CategoryBean category;

    private String label;

    private double price;

    private Integer stockId;

    private String picture;

    private Integer productorID;

    private AppUserBean producer;

    private int tva;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CategoryBean getCategory() {
        return category;
    }

    public void setCategory(CategoryBean category) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getStockId() {
        return stockId;
    }

    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getProductorID() {
        return productorID;
    }

    public void setProductorID(Integer productorID) {
        this.productorID = productorID;
    }

    public int getTva() {
        return tva;
    }

    public void setTva(int tva) {
        this.tva = tva;
    }

    public AppUserBean getProducer() {
        return producer;
    }

    public void setProducer(AppUserBean producer) {
        this.producer = producer;
    }

    @Override
    public String toString() {
        return "ProductBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", label='" + label + '\'' +
                ", price=" + price +
                ", stockId=" + stockId +
                ", picture='" + picture + '\'' +
                ", productorID=" + productorID +
                ", producer=" + producer +
                ", tva=" + tva +
                '}';
    }
}
