package Models;

import android.media.Image;

import com.google.firebase.auth.FirebaseUser;

public class FPackage {
    private Transactor tSender;
    private Transactor tReceiver;

    private FirebaseUser pReceiver;
    private FirebaseUser assignedDeliverer;

    private Image packageImage;
    private boolean delivered;

    public FPackage(Transactor tSender, Transactor tReceiver, FirebaseUser pReceiver, Image packageImage) {
        this.tSender = tSender;
        this.tReceiver = tReceiver;
        this.pReceiver = pReceiver;
        this.packageImage = packageImage;
    }

    public Transactor gettSender() {
        return tSender;
    }

    public Transactor gettReceiver() {
        return tReceiver;
    }

    public FirebaseUser getpReceiver() {
        return pReceiver;
    }

    public Image getPackageImage() {
        return packageImage;
    }

    public FirebaseUser getAssignedDeliverer() {
        return assignedDeliverer;
    }

    public void setAssignedDeliverer(FirebaseUser pDeliverer) {
        this.assignedDeliverer = pDeliverer;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }
}
