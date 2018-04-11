package Models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Transactor {
    private String fullName;
    private String phoneNumber;
    private String address;

    public Transactor(String fullName, String phoneNumber, String address) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public Transactor() {}

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        if (!isValidFullName()) {
            throw new IllegalArgumentException("The full name must contain only letters and spaces");
        }
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (!isValidPhoneNumber()) {
            throw new IllegalArgumentException("The phone number must contain numbers only in phone formatted way");
        }
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    private boolean isValidFullName() {
        Pattern pattern = Pattern.compile("^[\\p{L} .'-]+$",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(this.fullName);
        return matcher.find();
    }

    private boolean isValidPhoneNumber() {
        return android.util.Patterns.PHONE.matcher(this.phoneNumber).matches();
    }
}
