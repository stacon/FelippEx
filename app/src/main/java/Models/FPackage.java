package Models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FPackage {
    private Transactor tSender;
    private Transactor tReceiver;

    private String pReceiverFUID;
    private String assignedDelivererFUID;
    private String imageRefUri;

    private String timestamp;

    private boolean delivered;

    public FPackage(Transactor tSender, Transactor tReceiver, String pReceiverFUID, String imageRefUri) {
        this.tSender = tSender;
        this.tReceiver = tReceiver;
        this.pReceiverFUID = pReceiverFUID;
        this.assignedDelivererFUID = "";
        this.delivered = false;
        this.imageRefUri = imageRefUri;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        this.timestamp = dateFormat.format(date);
    }

    public Transactor getSender() {
        return tSender;
    }

    public Transactor getReceiver() {
        return tReceiver;
    }

    public String getpReceiver() {
        return pReceiverFUID;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
