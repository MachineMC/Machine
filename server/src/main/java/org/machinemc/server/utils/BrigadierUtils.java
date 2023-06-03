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
package org.machinemc.server.utils;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

/**
 * Utils for Brigadier library.
 */
public final class BrigadierUtils {

    private BrigadierUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a literal node that redirects its execution to
     * the given destination node.
     *
     * @param alias the command alias
     * @param destination the destination node
     * @param <T> executor type
     * @return the built node
     */
    public static <T> LiteralCommandNode<T> buildRedirect(final String alias,
                                                          final LiteralCommandNode<T> destination) {
        final LiteralArgumentBuilder<T> builder = LiteralArgumentBuilder
                .<T>literal(alias.toLowerCase())
                .requires(destination.getRequirement())
                .forward(
                        destination.getRedirect(), destination.getRedirectModifier(), destination.isFork())
                .executes(destination.getCommand());
        for (final CommandNode<T> child : destination.getChildren())
            builder.then(child);
        return builder.build();
    }

}
