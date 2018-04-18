package Models;

import com.stathis.constantinos.felippex.CodeHelper;

public class FPackage {
    private Transactor tSender;
    private Transactor tReceiver;
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

    public FPackage(Transactor tSender, Transactor tReceiver, String pReceiverFUID, String imageRefUri) {
        this.tSender = tSender;
        this.tReceiver = tReceiver;
        this.pReceiverFUID = pReceiverFUID;
        this.assignedDelivererFUID = "";
        this.delivered = false;
        this.imageRefUri = imageRefUri;

        this.dateReceived = CodeHelper.getDateNowToString();
        this.timeReceived = CodeHelper.getTimeNowToString();

        this.syntheticReceiptValue = pReceiverFUID + "-" + this.dateReceived;
    }

    public FPackage () {}

    public Transactor getSender() {
        return tSender;
    }

    public Transactor getReceiver() {
        return tReceiver;
    }

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
        this.syntheticReceiptValue = syntheticReceiptValue;
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

    public Transactor gettSender() {
        return tSender;
    }

    public void settSender(Transactor tSender) {
        this.tSender = tSender;
    }

    public Transactor gettReceiver() {
        return tReceiver;
    }

    public void settReceiver(Transactor tReceiver) {
        this.tReceiver = tReceiver;
    }

    public String getpReceiverFUID() {
        return pReceiverFUID;
    }

    public void setpReceiverFUID(String pReceiverFUID) {
        this.pReceiverFUID = pReceiverFUID;
    }

}
