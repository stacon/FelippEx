package Models;

import com.stathis.constantinos.felippex.CodeHelper;

public class FPackage {
    private Transactor sender;
    private Transactor receiver;
    private String transactionId;

    private String pReceiverFUID;
    private String assignedDelivererFUID;
    private String imageRefUri;

    private String dateReceived;
    private String timeReceived;

    // Firebase query synthetics
    private String syntheticReceiptValue; // Synthetic Value pReceiverFUID-date
    private String syntheticDeliveryValue; // Synthetic Value pReceiverFUID-date-assignedDelivererFUID

    private boolean delivered;

    public FPackage(Transactor sender, Transactor receiver, String pReceiverFUID, String imageRefUri) {
        this.sender = sender;
        this.receiver = receiver;
        this.pReceiverFUID = pReceiverFUID;
        this.assignedDelivererFUID = "";
        this.delivered = false;
        this.imageRefUri = imageRefUri;

        this.dateReceived = CodeHelper.getDateNowToString();
        this.timeReceived = CodeHelper.getTimeNowToString();

        if (this.syntheticReceiptValue == null) {
            this.syntheticReceiptValue = pReceiverFUID + "-" + this.dateReceived;
        }

    }

    public FPackage () {}

    public String getAssignedDelivererFUID() {
        return assignedDelivererFUID;
    }

    public void setAssignedDelivererFUID(String assignedDelivererFUID) {
        this.assignedDelivererFUID = assignedDelivererFUID;
    }

    public String getImageRefUri() { return imageRefUri; }

    public void setImageRefUri(String imageRefUri) {
        this.imageRefUri = imageRefUri;
    }

    public boolean getDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public String getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(String dateReceived) {
        this.dateReceived = dateReceived;
    }

    public String getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(String timeReceived) {
        this.timeReceived = timeReceived;
    }

    public String getSyntheticReceiptValue() {
        return syntheticReceiptValue;
    }

    public void setSyntheticReceiptValue(String syntheticReceiptValue) {
        if (this.syntheticReceiptValue == null) {
            this.syntheticReceiptValue = pReceiverFUID + "-" + this.dateReceived;
        }
    }

    public String getSyntheticDeliveryValue() {
        return syntheticDeliveryValue;
    }

    public void setSyntheticDeliveryValue(String syntheticDeliveryValue) {
        this.syntheticDeliveryValue = syntheticDeliveryValue;
    }

    public void setSyntheticDeliveryValue() {
        this.syntheticDeliveryValue = pReceiverFUID + CodeHelper.getDateNowToString() + assignedDelivererFUID;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getpReceiverFUID() {
        return pReceiverFUID;
    }

    public void setpReceiverFUID(String pReceiverFUID) {
        this.pReceiverFUID = pReceiverFUID;
    }

    public Transactor getSender() {
        return sender;
    }

    public void setSender(Transactor sender) {
        this.sender = sender;
    }

    public Transactor getReceiver() {
        return receiver;
    }

    public void setReceiver(Transactor receiver) {
        this.receiver = receiver;
    }

}
