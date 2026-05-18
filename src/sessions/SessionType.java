package sessions;

import exception.InvalidSessionException;

public enum SessionType {

    PLAYER_VS_PLAYER(1, "Player Vs Player"),
    PLAYER_VS_BOT(2, "Player Vs Bot"),
    BOT_VS_BOT(3, "Bot Vs Bot");

    private final int value;
    private final String displayName;

    SessionType(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    @SuppressWarnings("unused")
    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static SessionType fromIntValue(int value) {
        for (SessionType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new InvalidSessionException(value);
    }
}