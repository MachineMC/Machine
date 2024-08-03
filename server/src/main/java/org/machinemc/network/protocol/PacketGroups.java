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
package org.machinemc.network.protocol;

/**
 * Packet group identifiers.
 */
public interface PacketGroups {

    /**
     * Returns string representation of a packet group from
     * connection state and packet flow.
     *
     * @param state state
     * @param flow flow
     * @return packet group
     */
    static String getGroup(ConnectionState state, PacketFlow flow) {
        return switch (state) {
            case HANDSHAKING -> flow == PacketFlow.CLIENTBOUND
                    ? Handshaking.ClientBound.NAME
                    : Handshaking.ServerBound.NAME;
            case STATUS -> flow == PacketFlow.CLIENTBOUND
                    ? Status.ClientBound.NAME
                    : Status.ServerBound.NAME;
            case LOGIN -> flow == PacketFlow.CLIENTBOUND
                    ? Login.ClientBound.NAME
                    : Login.ServerBound.NAME;
            case CONFIGURATION -> flow == PacketFlow.CLIENTBOUND
                    ? Configuration.ClientBound.NAME
                    : Configuration.ServerBound.NAME;
            case PLAY -> flow == PacketFlow.CLIENTBOUND
                    ? Play.ClientBound.NAME
                    : Play.ServerBound.NAME;
        };
    }

    /**
     * Handshaking packet groups.
     */
    interface Handshaking {

        /**
         * Handshaking client-bound packet group.
         */
        interface ClientBound {
            String NAME = "HandshakingClientBound";
        }

        /**
         * Handshaking server-bound packet group.
         */
        interface ServerBound {
            String NAME = "HandshakingServerBound";

            int CLIENT_INTENTION = 0x00;
        }

    }

    /**
     * Status packet groups.
     */
    interface Status {

        /**
         * Status client-bound packet group.
         */
        interface ClientBound {
            String NAME = "StatusClientBound";

            int STATUS_RESPONSE = 0x00;
            int PONG = 0x01;
        }

        /**
         * Status server-bound packet group.
         */
        interface ServerBound {
            String NAME = "StatusServerBound";

            int STATUS_REQUEST = 0x00;
            int PING = 0x01;
        }

    }

    /**
     * Login packet groups.
     */
    interface Login {

        /**
         * Login client-bound packet group.
         */
        interface ClientBound {
            String NAME = "LoginClientBound";

            int ENCRYPTION_REQUEST = 0x01;
            int LOGIN_SUCCESS = 0x02;
            int SET_COMPRESSION = 0x03;
        }

        /**
         * Login server-bound packet group.
         */
        interface ServerBound {
            String NAME = "LoginServerBound";

            int HELLO = 0x00;
            int ENCRYPTION_RESPONSE = 0x01;
            int LOGIN_ACKNOWLEDGED = 0x03;
        }

    }

    /**
     * Configuration packet groups.
     */
    interface Configuration {

        /**
         * Configuration client-bound packet group.
         */
        interface ClientBound {
            String NAME = "ConfigurationClientBound";

            int PLUGIN_MESSAGE = 0x01;
            int FINISH_CONFIGURATION = 0x03;
            int PONG = 0x05;
        }

        /**
         * Configuration server-bound packet group.
         */
        interface ServerBound {
            String NAME = "ConfigurationServerBound";

            int CLIENT_INFORMATION = 0x00;
            int COOKIE_RESPONSE = 0x01;
            int PLUGIN_MESSAGE = 0x02;
            int ACKNOWLEDGE_FINISH_CONFIGURATION = 0x03;
            int PING = 0x05;
        }

    }

    /**
     * Play packet groups.
     */
    interface Play {

        /**
         * Play client-bound packet group.
         */
        interface ClientBound {
            String NAME = "PlayClientBound";

            int PLUGIN_MESSAGE = 0x19;
            int DISCONNECT = 0x1D;
            int PONG = 0x36;
        }

        /**
         * Play server-bound packet group.
         */
        interface ServerBound {
            String NAME = "PlayServerBound";

            int CLIENT_INFORMATION = 0x0A;
            int PLUGIN_MESSAGE = 0x12;
            int PING = 0x21;
        }
    }

}
