package lk.javainstitute.automotivex.model;

import java.util.ArrayList;

public class OrderDetails {

    private String orderDocId;
    private String first_name;
    private String last_name;
    private String address_line1;
    private String address_line2;
    private String city;
    private String postal_code;
    private String mobile;
    private String orderedDateTime;
    private String status;
    private int total;
    private int processingFee;
    private ArrayList<ProductDetails> product = new ArrayList<>();


    public OrderDetails() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderDocId() {
        return orderDocId;
    }

    public void setOrderDocId(String orderDocId) {
        this.orderDocId = orderDocId;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAddress_line1() {
        return address_line1;
    }

    public void setAddress_line1(String address_line1) {
        this.address_line1 = address_line1;
    }

    public String getAddress_line2() {
        return address_line2;
    }

    public void setAddress_line2(String address_line2) {
        this.address_line2 = address_line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOrderedDateTime() {
        return orderedDateTime;
    }

    public void setOrderedDateTime(String orderedDateTime) {
        this.orderedDateTime = orderedDateTime;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(int processingFee) {
        this.processingFee = processingFee;
    }

    public ArrayList<ProductDetails> getProduct() {
        return product;
    }

    public void setProduct(ArrayList<ProductDetails> product) {
        this.product = product;
    }
}
