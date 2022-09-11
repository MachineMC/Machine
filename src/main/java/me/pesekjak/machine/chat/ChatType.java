package me.pesekjak.machine.chat;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Range;

@AllArgsConstructor
public enum ChatType {

    CHAT(0),
    SYSTEM_MESSAGE(1),
    GAME_INFO(2),
    SAY_COMMAND(3),
    MSG_COMMAND(4),
    TEAM_MSG_COMMAND(5),
    EMOTE_COMMAND(6),
    TELLRAW_COMMAND(7);

    public final int id;

    public static ChatType fromId(@Range(from = 0, to = 7) int id) {
        for (ChatType value : values()) {
            if (value.id == id)
                return value;
        }
        throw new IllegalArgumentException("Unsupported chat type");
    }
}
