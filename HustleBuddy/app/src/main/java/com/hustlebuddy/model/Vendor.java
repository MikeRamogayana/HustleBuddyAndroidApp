package com.hustlebuddy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Vendor {

    private int vendorId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String contactNumber;
    private String residentialAddress;
    private String companyName;

    public Vendor(int vendorId, String firstName, String lastName, String email, String password,
                  String contactNumber, String residentialAddress, String companyName) {
        this.vendorId = vendorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.residentialAddress = residentialAddress;
        this.companyName = companyName;
    }

    public Vendor(JSONObject jsonObject) throws JSONException {
        setVendorId(jsonObject.getInt("vendorId"));
        setFirstName(jsonObject.getString("firstName"));
        setLastName(jsonObject.getString("lastName"));
        setEmail(jsonObject.getString("email"));
        setPassword(jsonObject.getString("password"));
        setContactNumber(jsonObject.getString("contactNumber"));
        setResidentialAddress(jsonObject.getString("residentialAddress"));
        setCompanyName(jsonObject.getString("companyName"));
    }

    public Vendor() {
    }

    public static JSONObject toJSONObject(Vendor vendor) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("vendorId", vendor.getVendorId());
        jsonObject.put("firstName", vendor.getFirstName());
        jsonObject.put("lastName", vendor.getLastName());
        jsonObject.put("email", vendor.getEmail());
        jsonObject.put("password", vendor.getPassword());
        jsonObject.put("contactNumber",vendor.getContactNumber());
        jsonObject.put("residentialAddress",vendor.getResidentialAddress());
        jsonObject.put("companyName",vendor.getCompanyName());
        return jsonObject;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getResidentialAddress() {
        return residentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        this.residentialAddress = residentialAddress;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
