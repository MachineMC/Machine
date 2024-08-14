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
package org.machinemc.client;

import com.google.common.base.Preconditions;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TranslationComponent;

import java.net.URI;

/**
 * Links that the client will display in the menu available from the pause menu.
 * <p>
 * Link labels can be built-in or custom (i.e., any text).
 *
 * @param type link label type
 * @param link link
 */
public record ServerLink(Type type, URI link) {

    public ServerLink {
        Preconditions.checkNotNull(type, "Server link type can not be null");
        Preconditions.checkNotNull(link, "Link can not be null");
    }

    /**
     * Represents type of the server link.
     *
     * @see BuiltIn
     * @see Custom
     */
    public sealed interface Type {

        /**
         * The displayed name of the server link in the
         * server links menu.
         *
         * @return display name of this type
         */
        Component displayName();

    }

    /**
     * Represents a built-in server link type.
     */
    public enum BuiltIn implements Type {

        /**
         * Bug report.
         * <p>
         * Is also displayed on connection error screen;
         * included as a comment in the disconnection report.
         */
        BUG_REPORT("known_server_link.report_bug"),

        /**
         * Community guidelines.
         */
        COMMUNITY_GUIDELINES("known_server_link.community_guidelines"),

        /**
         * Support.
         */
        SUPPORT("known_server_link.support"),

        /**
         * Status.
         */
        STATUS("known_server_link.status"),

        /**
         * Feedback.
         */
        FEEDBACK("known_server_link.feedback"),

        /**
         * Community.
         */
        COMMUNITY("known_server_link.community"),

        /**
         * Website.
         */
        WEBSITE("known_server_link.website"),

        /**
         * Forums.
         */
        FORUMS("known_server_link.forums"),

        /**
         * News.
         */
        NEWS("known_server_link.news"),

        /**
         * Announcements.
         */
        ANNOUNCEMENTS("known_server_link.announcements");

        private final Component displayName;

        BuiltIn(String translation) {
            displayName = TranslationComponent.of(translation);
        }

        @Override
        public Component displayName() {
            return displayName;
        }

    }

    /**
     * Represents a custom server link.
     *
     * @param displayName displayed name of the server link
     */
    public record Custom(Component displayName) implements Type {

        public Custom {
            Preconditions.checkNotNull(displayName, "Custom display name can not be null");
        }

    }

}
