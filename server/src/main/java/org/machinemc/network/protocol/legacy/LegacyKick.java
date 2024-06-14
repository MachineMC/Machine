/*
 * This file is part of Machine.
 *
 * Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Machine.
 * If not, see https://www.gnu.org/licenses/.
 */
package org.machinemc.network.protocol.legacy;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;
import org.machinemc.network.protocol.status.clientbound.ServerStatus;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.util.ChatUtils;

/**
 * Represents a legacy kick packet.
 * <p>
 * This packet follows the pre-netty format and is encoded using special handler
 * ({@link org.machinemc.network.protocol.handlers.LegacyPingEncoder}).
 * <p>
 * It can be used either as a kick packet or as a response to server status request in
 * multiplayer menu.
 *
 * @param data reason of being kicked out (or encoded server status)
 */
public record LegacyKick(String data) {

    private static final String LEGACY_COLOR_CODE = String.valueOf(ChatUtils.COLOR_CHAR);

    /**
     * Encodes the server status into a kick data that can be decoded
     * by the client.
     *
     * @return encoded legacy server status as a kick packet
     */
    public static LegacyKick fromStatus(final ServerStatus status, final LegacyPingType type) {
        Preconditions.checkNotNull(type, "Legacy ping type can not be null");
        final ServerStatus.Players players = status.players() != null
                ? status.players()
                : new ServerStatus.Players(0, 0);
        final String reason = switch (type) {
            case V1_3 -> String.join(
                    LEGACY_COLOR_CODE,
                    getLegacyDescription(status.description(), type),
                    String.valueOf(players.online()),
                    String.valueOf(players.max()));
            case V1_5, V1_6 -> {
                final String versionName = status.version().version() != null
                        ? status.version().version()
                        : "Unknown";
                yield String.join("\0",
                        LEGACY_COLOR_CODE + "1",
                        String.valueOf(status.version().protocolVersion()),
                        versionName,
                        getLegacyDescription(status.description(), type),
                        String.valueOf(players.online()),
                        String.valueOf(players.max())
                );
            }
        };
        return new LegacyKick(reason);
    }

    /**
     * Creates regular legacy kick with given reason.
     *
     * @param reason reason
     * @return legacy kick with given reason
     */
    public static LegacyKick withReason(final Component reason) {
        Preconditions.checkNotNull(reason, "Reason can not be null");
        return new LegacyKick(reason.toLegacyString());
    }

    /**
     * Removes color code from the string.
     *
     * @param string string
     * @return string with removed color codes
     */
    private static String stripColors(final String string) {
        return string.replaceAll(LEGACY_COLOR_CODE, "");
    }

    /**
     * Returns the first line of given string.
     *
     * @param string string
     * @return first line of the string
     */
    private static String getFirstLine(final String string) {
        final int nl = string.indexOf('\n');
        return nl == -1 ? string : string.substring(0, nl);
    }

    /**
     * Returns string from component, formatted to be used in
     * the legacy server status response.
     *
     * @param component component
     * @param type legacy ping type
     * @return formatted component for legacy server status
     */
    private static String getLegacyDescription(final @Nullable Component component, final LegacyPingType type) {
        final Component presentComponent = component != null ? component : TextComponent.empty(); // description has to be present
        String asString = type == LegacyPingType.V1_3
                ? presentComponent.getString() // 1.3 does not allow colors
                : presentComponent.toLegacyString();
        asString = getFirstLine(asString);
        if (type == LegacyPingType.V1_3) asString = stripColors(asString); // 1.3 does not allow colors
        return asString;
    }

}
