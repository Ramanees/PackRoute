package com.example.swiftdelivery.user;

import com.google.firebase.database.PropertyName;

public class UserDeliveryHistory {

    private String deliveryID;
    private String UserID;
    private String UserName;
    private String UserMobile;
    private String AssignedAgentName;
    private String AssignedAgentMobile;
    private String PickupAddress;
    private String DeliveryAddress;
    private String PackageDetails;
    private String Status;
    private String Otp;

    public UserDeliveryHistory() {}

    public String getDeliveryID() { return deliveryID; }
    public void setDeliveryID(String deliveryID) { this.deliveryID = deliveryID; }

    @PropertyName("Otp")
    public String getOtp() { return Otp; }
    @PropertyName("Otp")
    public void setOtp(String otp) { Otp = otp; }

    @PropertyName("UserID")
    public String getUserID() { return UserID; }
    @PropertyName("UserID")
    public void setUserID(String userID) { UserID = userID; }

    @PropertyName("UserName")
    public String getUserName() { return UserName; }
    @PropertyName("UserName")
    public void setUserName(String userName) { UserName = userName; }

    @PropertyName("UserMobile")
    public String getUserMobile() { return UserMobile; }
    @PropertyName("UserMobile")
    public void setUserMobile(String userMobile) { UserMobile = userMobile; }

    @PropertyName("AssignedAgentName")
    public String getAssignedAgentName() { return AssignedAgentName; }
    @PropertyName("AssignedAgentName")
    public void setAssignedAgentName(String assignedAgentName) { AssignedAgentName = assignedAgentName; }

    @PropertyName("AssignedAgentMobile")
    public String getAssignedAgentMobile() { return AssignedAgentMobile; }
    @PropertyName("AssignedAgentMobile")
    public void setAssignedAgentMobile(String assignedAgentMobile) { AssignedAgentMobile = assignedAgentMobile; }

    @PropertyName("PickupAddress")
    public String getPickupAddress() { return PickupAddress; }
    @PropertyName("PickupAddress")
    public void setPickupAddress(String pickupAddress) { PickupAddress = pickupAddress; }

    @PropertyName("DeliveryAddress")
    public String getDeliveryAddress() { return DeliveryAddress; }
    @PropertyName("DeliveryAddress")
    public void setDeliveryAddress(String deliveryAddress) { DeliveryAddress = deliveryAddress; }

    @PropertyName("PackageDetails")
    public String getPackageDetails() { return PackageDetails; }
    @PropertyName("PackageDetails")
    public void setPackageDetails(String packageDetails) { PackageDetails = packageDetails; }

    @PropertyName("Status")
    public String getStatus() { return Status; }
    @PropertyName("Status")
    public void setStatus(String status) { Status = status; }
}
