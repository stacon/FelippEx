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
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {

        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    private boolean isValidFullName(String fullName) {
        Pattern pattern = Pattern.compile("^[\\p{L} .'-]+$",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(fullName);
        return matcher.find();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return android.util.Patterns.PHONE.matcher(phoneNumber).matches();
    }
}
