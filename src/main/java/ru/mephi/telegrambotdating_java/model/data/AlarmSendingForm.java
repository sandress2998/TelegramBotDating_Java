package ru.mephi.telegrambotdating_java.model.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlarmSendingForm {
    String receiverTag;
    String deactivationCode;
    String address;
    String time;
    LocalDateTime datingTime;

    public AlarmSendingForm(String receiverTag, String deactivationCode, String address, String time) {
        this.receiverTag = receiverTag;
        this.deactivationCode = deactivationCode;
        this.address = address;
        this.time = time;
    }

    public boolean isDataCorrect() {
        return isDeactivationCodeCorrect() && isAddressCorrect() && isDatingTimeCorrect();
    }

    public boolean isDeactivationCodeCorrect() {
        try {
            return deactivationCode == null || (Integer.parseInt(deactivationCode) >= 1000 && Integer.parseInt(deactivationCode) <= 9999);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isAddressCorrect() {
        return address == null || address.length() < 200;
    }

    public boolean isDatingTimeCorrect() {
        String pattern = "dd.MM.yyyy HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            if (time == null) return true;
            datingTime = LocalDateTime.parse(time, formatter);
            return !datingTime.isBefore(LocalDateTime.now());
        } catch (Exception e) {
            return false;
        }
    }
}
