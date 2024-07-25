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
package org.machinemc.network.protocol.serializers;

import org.machinemc.chat.ChatMode;
import org.machinemc.entities.player.MainHand;
import org.machinemc.entities.player.PlayerSettings;
import org.machinemc.entities.player.SkinPart;
import org.machinemc.paklet.DataVisitor;
import org.machinemc.paklet.serialization.Serializer;
import org.machinemc.paklet.serialization.SerializerContext;
import org.machinemc.paklet.serialization.Supports;

@Supports(PlayerSettings.class)
public class PlayerSettingsSerializer implements Serializer<PlayerSettings> {

    @Override
    public void serialize(final SerializerContext context, final DataVisitor visitor, final PlayerSettings settings) {
        final Serializer<String> stringSerializer = context.serializerProvider().getFor(String.class);
        final Serializer<Byte> byteSerializer = context.serializerProvider().getFor(Byte.class);
        final Serializer<ChatMode> chatModeSerializer = context.serializerProvider().getFor(ChatMode.class);
        final Serializer<Boolean> booleanSerializer = context.serializerProvider().getFor(Boolean.class);
        final Serializer<MainHand> mainHandSerializer = context.serializerProvider().getFor(MainHand.class);
        visitor.write(context, stringSerializer, settings.locale());
        visitor.write(context, byteSerializer, settings.viewDistance());
        visitor.write(context, chatModeSerializer, settings.chatMode());
        visitor.write(context, booleanSerializer, settings.chatColors());
        visitor.write(context, byteSerializer, (byte) SkinPart.skinMask(settings.skinParts()));
        visitor.write(context, mainHandSerializer, settings.mainHand());
        visitor.write(context, booleanSerializer, settings.textFiltering());
        visitor.write(context, booleanSerializer, settings.allowServerListing());
    }

    @Override
    public PlayerSettings deserialize(final SerializerContext context, final DataVisitor visitor) {
        final Serializer<String> stringSerializer = context.serializerProvider().getFor(String.class);
        final Serializer<Byte> byteSerializer = context.serializerProvider().getFor(Byte.class);
        final Serializer<Integer> intSerializer = context.serializerProvider().getFor(Integer.class);
        final Serializer<Boolean> booleanSerializer = context.serializerProvider().getFor(Boolean.class);
        return new PlayerSettings(
                visitor.read(context, stringSerializer), // Locale
                visitor.read(context, byteSerializer), // View Distance
                ChatMode.values()[visitor.read(context, intSerializer)], // Chat Mode
                visitor.read(context, booleanSerializer), // Chat Colors
                SkinPart.fromMask(visitor.read(context, byteSerializer)), // Skin Parts
                MainHand.values()[visitor.read(context, intSerializer)], // Main Hand
                visitor.read(context, booleanSerializer), // Text Filtering
                visitor.read(context, booleanSerializer) // Allow Server Listing
        );
    }

}
