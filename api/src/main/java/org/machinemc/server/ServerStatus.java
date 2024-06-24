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
package org.machinemc.server;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import lombok.With;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.machinemc.barebones.profile.GameProfile;
import org.machinemc.scriptive.components.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Server status sent to the player in the multiplayer menu after
 * pinging the server.
 *
 * @param version server version
 * @param players online server players, if omitted, {@code ???} will be displayed in dark grey in place of the player count
 * @param description message of the day
 * @param favicon server icon image
 * @param enforcesSecureChat whether the server enforces secure chat messages
 */
@With
public record ServerStatus(Version version,
                           @Nullable Players players,
                           @Nullable Component description,
                           @Nullable Favicon favicon,
                           boolean enforcesSecureChat) {

    public ServerStatus(final Version version, final boolean enforcesSecureChat) {
        this(version, null, enforcesSecureChat);
    }

    public ServerStatus(final Version version, final Players players, final boolean enforcesSecureChat) {
        this(version, players, null, enforcesSecureChat);
    }

    public ServerStatus(final Version version, final Players players, final Component description, final boolean enforcesSecureChat) {
        this(version, players, description, null, enforcesSecureChat);
    }

    public ServerStatus {
        Preconditions.checkNotNull(version, "Version can not be null");
    }

    /**
     * Version displayed in server status.
     * <p>
     * If the protocol version match the client's protocol, player count will be displayed,
     * otherwise the version string will be displayed in red.
     * <p>
     * Version name should be considered mandatory. On newer Notchian client versions, it may
     * be omitted and will be treated as though it were the string {@code Old} if so.
     *
     * @param version version name
     * @param protocolVersion protocol version
     */
    @With
    public record Version(@Nullable String version, int protocolVersion) {
    }

    /**
     * Server players shown in the server status.
     *
     * @param max maximum number of allowed online players
     * @param online number of online players
     * @param sample the player names (but not their UUIDs) will be shown in order in the tooltip
     *               when hovering over the player count, or no tooltip will be shown if null/empty.
     */
    @With
    public record Players(int max, int online, @Nullable List<GameProfile> sample) {

        public Players(final int max, final int online) {
            this(max, online, null);
        }

        public Players {
            if (sample != null) {
                sample = Collections.unmodifiableList(sample);
                Preconditions.checkArgument(Iterables.all(sample, Predicates.notNull()), "Sample can not contain null values");
            }
        }

        /**
         * Adds new game profiles to the sample list.
         *
         * @param profiles profiles to add
         * @return new players instance
         */
        @Contract("_ -> new")
        public Players addSample(final GameProfile... profiles) {
            Preconditions.checkNotNull(profiles);
            return addSample(List.of(profiles));
        }

        /**
         * Adds new game profiles to the sample list.
         *
         * @param profiles profiles to add
         * @return new players instance
         */
        @Contract("_ -> new")
        public Players addSample(final Collection<GameProfile> profiles) {
            Preconditions.checkNotNull(profiles);
            final List<GameProfile> sample = new ArrayList<>();
            if (this.sample != null) sample.addAll(this.sample);
            sample.addAll(profiles);
            return withSample(sample);
        }

        /**
         * Returns whether there are sample game profiles (sample players) present.
         *
         * @return whether there is a players sample
         */
        public boolean hasSample() {
            return sample != null && !sample.isEmpty();
        }

    }

    /**
     * A Minecraft server favicon is a 64x64, Base64 encoded PNG image
     * that is displayed as server icon in multiplayer server list.
     *
     * @param data icon data
     */
    public record Favicon(byte[] data) {

        private static final String PREFIX = "data:image/png;base64,";

        /**
         * Creates a new {@link Favicon} from given image.
         *
         * @param image the image to use for the favicon
         * @return new favicon
         */
        public static Favicon create(final BufferedImage image) {
            Preconditions.checkNotNull(image);
            Preconditions.checkArgument(image.getWidth() == 64 && image.getHeight() == 64, "Image is not 64x64");
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "PNG", os);
            } catch (IOException exception) {
                throw new AssertionError(exception);
            }
            return new Favicon(Base64.getEncoder().encode(os.toByteArray()));
        }

        /**
         * Creates a new {@link Favicon} by reading the image from the path.
         *
         * @param path the path to the image
         * @return new favicon
         * @throws IOException if the file could not be read from the path
         */
        public static Favicon create(final Path path) throws IOException {
            try (InputStream stream = Files.newInputStream(path)) {
                final BufferedImage image = ImageIO.read(stream);
                if (image == null) throw new IOException("Failed to create image");
                return create(image);
            }
        }

        /**
         * Creates a new {@link Favicon} from its string representation.
         *
         * @param string string
         * @return new favicon
         * @see Favicon#asString()
         */
        public static Favicon fromString(final String string) {
            Preconditions.checkArgument(string.startsWith(PREFIX), "Unsupported format");
            final String data = string.replaceFirst(PREFIX, "");
            return new Favicon(data.getBytes(StandardCharsets.UTF_8));
        }

        public Favicon {
            Preconditions.checkNotNull(data, "Data can not be null");
        }

        /**
         * Returns the favicon as encoded String that can be processed by the
         * client.
         *
         * @return this favicon as string
         */
        public String asString() {
            return PREFIX + new String(data, StandardCharsets.UTF_8);
        }

    }

}
