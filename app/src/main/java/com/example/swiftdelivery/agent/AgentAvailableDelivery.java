package com.example.swiftdelivery.agent;

import com.google.firebase.database.PropertyName;

public class AgentAvailableDelivery {
    private String deliveryID;
    private String userName;
    private String userMobile;
    private String pickupAddress;
    private String deliveryAddress;
    private String pickupPhone;
    private String deliveryPhone;
    private String packageDetails;
    private String status;
    private String assignedAgent;
    private String otp;
    private String packageSize;

    public AgentAvailableDelivery() {}

    public String getDeliveryID() { return deliveryID; }
    public void setDeliveryID(String deliveryID) { this.deliveryID = deliveryID; }

    @PropertyName("UserName")
    public String getUserName() { return userName; }
    @PropertyName("UserName")
    public void setUserName(String userName) { this.userName = userName; }

    @PropertyName("UserMobile")
    public String getUserMobile() { return userMobile; }
    @PropertyName("UserMobile")
    public void setUserMobile(String userMobile) { this.userMobile = userMobile; }

    @PropertyName("PickupAddress")
    public String getPickupAddress() { return pickupAddress; }
    @PropertyName("PickupAddress")
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    @PropertyName("DeliveryAddress")
    public String getDeliveryAddress() { return deliveryAddress; }
    @PropertyName("DeliveryAddress")
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    @PropertyName("Status")
    public String getStatus() { return status; }
    @PropertyName("Status")
    public void setStatus(String status) { this.status = status; }

    @PropertyName("AssignedAgent")
    public String getAssignedAgent() { return assignedAgent; }
    @PropertyName("AssignedAgent")
    public void setAssignedAgent(String assignedAgent) { this.assignedAgent = assignedAgent; }

    @PropertyName("PickupPhone")
    public String getPickupPhone() { return pickupPhone; }
    @PropertyName("PickupPhone")
    public void setPickupPhone(String pickupPhone) { this.pickupPhone = pickupPhone; }

    @PropertyName("DeliveryPhone")
    public String getDeliveryPhone() { return deliveryPhone; }
    @PropertyName("DeliveryPhone")
    public void setDeliveryPhone(String deliveryPhone) { this.deliveryPhone = deliveryPhone; }

    @PropertyName("PackageDetails")
    public String getPackageDetails() { return packageDetails; }
    @PropertyName("PackageDetails")
    public void setPackageDetails(String packageDetails) { this.packageDetails = packageDetails; }

    @PropertyName("Otp")
    public String getOtp() { return otp; }
    @PropertyName("Otp")
    public void setOtp(String otp) { this.otp = otp; }

    @PropertyName("PackageSize")
    public String getPackageSize() { return packageSize; }
    @PropertyName("PackageSize")
    public void setPackageSize(String packageSize) { this.packageSize = packageSize; }
}
