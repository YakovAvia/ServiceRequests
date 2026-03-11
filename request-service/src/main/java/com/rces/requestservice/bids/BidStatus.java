package com.rces.requestservice.bids;

public enum BidStatus {
    NEW("Новая"),
    WORK("В работе"),
    REJECTED("Забракована"),
    COMPLETED("Выполнена"),
    STOPPED("Приостановлена");

    BidStatus(String enumName) {
        this.enumName = enumName;
    }

    private final String enumName;

    public String getEnumName() {
        return enumName;
    }
}
