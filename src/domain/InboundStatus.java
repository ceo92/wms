package domain;

import lombok.Getter;

@Getter
public enum InboundStatus {

    PENDING("입고 예정"),
    APPROVED("입고 대기"),
    COMPLETED("입고 완료"),
    CANCEL("입고 취소");

    private final String value;

    InboundStatus(String value) {
        this.value = value;
    }
}
