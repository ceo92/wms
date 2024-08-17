package domain;

import lombok.Getter;

@Getter
public enum InboundStatus {

    PENDING("PENDING"),
    APPROVED("APPROVED"),
    COMPLETED("COMPLETED"),
    CANCEL("CANCEL");

    private final String value;

    InboundStatus(String value) {
        this.value = value;
    }

}
