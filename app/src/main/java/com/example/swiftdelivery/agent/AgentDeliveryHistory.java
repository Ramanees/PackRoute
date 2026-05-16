package com.example.swiftdelivery.agent;

public class AgentDeliveryHistory {

    private String deliveryID;
    private String AssignedAgent;
    private String UserName;
    private String UserMobile;
    private String PickupAddress;
    private String DeliveryAddress;
    private String PackageDetails;
    private String Status;

    public AgentDeliveryHistory() {}

    public String getDeliveryID() {
        return deliveryID;
    }

    public void setDeliveryID(String deliveryID) {
        this.deliveryID = deliveryID;
    }

    public String getAssignedAgent() {
        return AssignedAgent;
    }

    public void setAssignedAgent(String assignedAgent) {
        AssignedAgent = assignedAgent;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserMobile() {
        return UserMobile;
    }

    public void setUserMobile(String userMobile) {
        UserMobile = userMobile;
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

    public AgentDeliveryHistory(String deliveryID, String assignedAgent, String userName, String userMobile, String pickupAddress, String deliveryAddress, String packageDetails, String status) {
        this.deliveryID = deliveryID;
        AssignedAgent = assignedAgent;
        UserName = userName;
        UserMobile = userMobile;
        PickupAddress = pickupAddress;
        DeliveryAddress = deliveryAddress;
        PackageDetails = packageDetails;
        Status = status;
    }
}
