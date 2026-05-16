package com.example.swiftdelivery.admin;

public class AdminManageDelivery {

    private String UserName;
    private String UserID;
    private String UserMobile;
    private String AssignedAgentName;
    private String AssignedAgent;
    private String AssignedAgentMobile;
    private String PickupAddress;
    private String DeliveryAddress;
    private String PackageDetails;
    private String Status;
    private String DeliveryRequestDate;
    AdminManageDelivery() {}

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserMobile() {
        return UserMobile;
    }

    public void setUserMobile(String userMobile) {
        UserMobile = userMobile;
    }

    public String getAssignedAgentName() {
        return AssignedAgentName;
    }

    public void setAssignedAgentName(String assignedAgentName) {
        AssignedAgentName = assignedAgentName;
    }

    public String getAssignedAgent() {
        return AssignedAgent;
    }

    public void setAssignedAgent(String assignedAgent) {
        AssignedAgent = assignedAgent;
    }

    public String getAssignedAgentMobile() {
        return AssignedAgentMobile;
    }

    public void setAssignedAgentMobile(String assignedAgentMobile) {
        AssignedAgentMobile = assignedAgentMobile;
    }

    public String getPickupAddress() {
        return PickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        PickupAddress = pickupAddress;
    }

    public String getDeliveryAddress() {
        return DeliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        DeliveryAddress = deliveryAddress;
    }

    public String getPackageDetails() {
        return PackageDetails;
    }

    public void setPackageDetails(String packageDetails) {
        PackageDetails = packageDetails;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDeliveryRequestDate() {
        return DeliveryRequestDate;
    }

    public void setDeliveryRequestDate(String deliveryRequestDate) {
        DeliveryRequestDate = deliveryRequestDate;
    }

    public AdminManageDelivery(String userName, String userID, String userMobile, String assignedAgentName, String assignedAgent, String assignedAgentMobile, String pickupAddress, String deliveryAddress, String packageDetails, String status, String deliveryRequestDate) {
        UserName = userName;
        UserID = userID;
        UserMobile = userMobile;
        AssignedAgentName = assignedAgentName;
        AssignedAgent = assignedAgent;
        AssignedAgentMobile = assignedAgentMobile;
        PickupAddress = pickupAddress;
        DeliveryAddress = deliveryAddress;
        PackageDetails = packageDetails;
        Status = status;
        DeliveryRequestDate = deliveryRequestDate;
    }
}
