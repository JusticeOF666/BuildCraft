/* Copyright (c) 2016 SpaceToad and the BuildCraft team
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package buildcraft.factory;

import buildcraft.factory.tile.*;
import net.minecraft.block.material.Material;

import buildcraft.lib.BCLib;
import buildcraft.lib.block.BlockBCBase_Neptune;
import buildcraft.lib.tile.TileBC_Neptune;

import buildcraft.factory.block.BlockAutoWorkbenchItems;
import buildcraft.factory.block.BlockChute;
import buildcraft.factory.block.BlockDistiller;
import buildcraft.factory.block.BlockFloodGate;
import buildcraft.factory.block.BlockHeatExchange;
import buildcraft.factory.block.BlockMiningWell;
import buildcraft.factory.block.BlockPump;
import buildcraft.factory.block.BlockTank;
import buildcraft.factory.block.BlockTube;
import buildcraft.factory.block.BlockWaterGel;

public class BCFactoryBlocks {
    public static BlockAutoWorkbenchItems autoWorkbenchItems;
    public static BlockMiningWell miningWell;
    public static BlockPump pump;
    public static BlockTube tube;
    public static BlockFloodGate floodGate;
    public static BlockTank tank;
    public static BlockChute chute;
    public static BlockDistiller distiller;
    public static BlockHeatExchange heatExchangeStart, heatExchangeMiddle, heatExchangeEnd;

    // public static BlockAutoWorkbenchFluids autoWorkbenchFluids;
    // public static BlockPlastic plastic;
    public static BlockWaterGel waterGel;

    public static void preInit() {
        // plastic = BlockBuildCraftBase_BC8.register(new BlockPlastic("block.plastic"), ItemPlastic::new);
        autoWorkbenchItems = BlockBCBase_Neptune.register(new BlockAutoWorkbenchItems(Material.ROCK, "block.autoworkbench.item"));
        miningWell = BlockBCBase_Neptune.register(new BlockMiningWell(Material.ROCK, "block.mining_well"));
        pump = BlockBCBase_Neptune.register(new BlockPump(Material.ROCK, "block.pump"));
        tube = BlockBCBase_Neptune.register(new BlockTube(Material.IRON, "block.tube"), null);
        floodGate = BlockBCBase_Neptune.register(new BlockFloodGate(Material.ROCK, "block.flood_gate"));
        tank = BlockBCBase_Neptune.register(new BlockTank(Material.ROCK, "block.tank"));
        chute = BlockBCBase_Neptune.register(new BlockChute(Material.ROCK, "block.chute"));
        distiller = BlockBCBase_Neptune.register(new BlockDistiller(Material.IRON, "block.distiller"));
        heatExchangeStart = BlockBCBase_Neptune.register(new BlockHeatExchange(Material.IRON, "block.heat_exchange.start", BlockHeatExchange.Part.START));
        heatExchangeMiddle = BlockBCBase_Neptune.register(new BlockHeatExchange(Material.IRON, "block.heat_exchange.middle", BlockHeatExchange.Part.MIDDLE));
        heatExchangeEnd = BlockBCBase_Neptune.register(new BlockHeatExchange(Material.IRON, "block.heat_exchange.end", BlockHeatExchange.Part.END));
        if (BCLib.DEV) {
            waterGel = BlockBCBase_Neptune.register(new BlockWaterGel(Material.CLAY, "block.water_gel"), null);
        }

        TileBC_Neptune.registerTile(TileAutoWorkbenchItems.class, "tile.autoworkbench.item");
        TileBC_Neptune.registerTile(TileMiningWell.class, "tile.mining_well");
        TileBC_Neptune.registerTile(TilePump.class, "tile.pump");
        TileBC_Neptune.registerTile(TilePump2.class, "tile.pump2");
        TileBC_Neptune.registerTile(TileFloodGate.class, "tile.flood_gate");
        TileBC_Neptune.registerTile(TileTank.class, "tile.tank");
        TileBC_Neptune.registerTile(TileChute.class, "tile.chute");
        TileBC_Neptune.registerTile(TileDistiller_BC8.class, "tile.distiller");
        TileBC_Neptune.registerTile(TileHeatExchangeStart.class, "tile.heat_exchange.start");
        TileBC_Neptune.registerTile(TileHeatExchangeEnd.class, "tile.heat_exchange.end");
    }
}
