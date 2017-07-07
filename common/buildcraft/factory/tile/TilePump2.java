/*
 * Copyright (c) 2017 SpaceToad and the BuildCraft team
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not
 * distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/
 */

package buildcraft.factory.tile;

import java.io.IOException;
import java.util.*;

import javax.annotation.Nullable;

import buildcraft.api.core.BCLog;
import it.unimi.dsi.fastutil.ints.IntArrays;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import buildcraft.api.core.EnumPipePart;
import buildcraft.api.mj.IMjReceiver;

import buildcraft.lib.fluid.Tank;
import buildcraft.lib.misc.BlockUtil;
import buildcraft.lib.misc.CapUtil;
import buildcraft.lib.misc.FluidUtilBC;
import buildcraft.lib.misc.NBTUtilBC;
import buildcraft.lib.mj.MjRedstoneBatteryReceiver;
import buildcraft.lib.net.PacketBufferBC;

public class TilePump2 extends TileMiner {
    private final Tank tank = new Tank("tank", 16 * Fluid.BUCKET_VOLUME, this);
    private final int updateEvery = 25;  // ticks
    private boolean infiniteSource = false;
    private final ArrayDeque<BlockPos> LocationDeque = new ArrayDeque<>();

    @Nullable
    private BlockPos oilSpringPos;

    public TilePump2() {
        tank.setCanFill(false);
        tankManager.add(tank);
        caps.addCapabilityInstance(CapUtil.CAP_FLUIDS, tank, EnumPipePart.VALUES);
    }

    @Override
    protected IMjReceiver createMjReceiver() {
        return new MjRedstoneBatteryReceiver(battery);
    }


    private boolean canDrain(BlockPos blockPos) {
        Fluid fluid = BlockUtil.getFluid(world, blockPos);
        return tank.isEmpty() ? fluid != null : fluid == tank.getFluidType();
    }


    @Override
    protected void initCurrentPos() {;}

    private void floodFill(BlockPos start) {
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(start);

        int count = 0;
        while (!queue.isEmpty()) {
            if (count >= 3)
                if (BlockUtil.getFluid(world, LocationDeque.peek()) == FluidRegistry.WATER) {   // no infinite uu-matter
                    LocationDeque.clear();  // don't need this if it's infinite
                    LocationDeque.add(start);  // add the original source block to (fake) drain from
                    this.infiniteSource = true;
                    System.out.println("Infinite water source");
                    return;
                }

            BlockPos p = queue.remove();

            if (BlockUtil.getFluidWithFlowing(world, p) != null && (!LocationDeque.contains(p) || p == start)) {
                if (canDrain(p))
                    LocationDeque.addLast(p);
                queue.add(new BlockPos(p.down()));
                queue.add(new BlockPos(p.north()));
                queue.add(new BlockPos(p.east()));
                queue.add(new BlockPos(p.south()));
                queue.add(new BlockPos(p.west()));
                count++;
            }
        }

        this.infiniteSource = false;
        System.out.println("Found " + count + " fluid blocks");
    }

    private BlockPos touch() {
        BlockPos searchPos = this.pos.down();

        while (searchPos.getY() >= 0) {
            if (!world.isAirBlock(searchPos)) {
                if (canDrain(searchPos))
                    return searchPos;
                else
                    return null;
            }

            searchPos = searchPos.down();
        }
        BCLog.logger.error("You're trying to pump up void");
        return null;
    }


    @Override
    public void update() {
        if (!world.isRemote && world.getTotalWorldTime() % this.updateEvery == 0) {
            BlockPos t = touch();
            if (t != null)
                floodFill(t);
            }

        super.update();

        FluidUtilBC.pushFluidAround(world, pos, tank);
    }

    @Override
    public void mine() {
        // currentPos = pos;
        if (LocationDeque.isEmpty())
            return;

        BlockPos toBeDrained = LocationDeque.peek();
        System.out.println(toBeDrained);
        FluidStack drain = BlockUtil.drainBlock(world, toBeDrained, false);
        System.out.println(infiniteSource);


        if (infiniteSource)
            tank.fillInternal(drain, true);
        //else {
        //    tank.fillInternal(drain, true);
        //    BlockUtil.drainBlock(world, toBeDrained, true);
        //    LocationDeque.remove();
        //}
    }

    // NBT

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        oilSpringPos = NBTUtilBC.readBlockPos(nbt.getTag("oilSpringPos"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (oilSpringPos != null) {
            nbt.setTag("oilSpringPos", NBTUtilBC.writeBlockPos(oilSpringPos));
        }
        return nbt;
    }

    // Networking

    @Override
    public void writePayload(int id, PacketBufferBC buffer, Side side) {
        super.writePayload(id, buffer, side);
        if (side == Side.SERVER) {
            if (id == NET_RENDER_DATA) {
                writePayload(NET_LED_STATUS, buffer, side);
            } else if (id == NET_LED_STATUS) {
                tank.writeToBuffer(buffer);
            }
        }
    }

    @Override
    public void readPayload(int id, PacketBufferBC buffer, Side side, MessageContext ctx) throws IOException {
        super.readPayload(id, buffer, side, ctx);
        if (side == Side.CLIENT) {
            if (id == NET_RENDER_DATA) {
                readPayload(NET_LED_STATUS, buffer, side, ctx);
            } else if (id == NET_LED_STATUS) {
                tank.readFromBuffer(buffer);
            }
        }
    }

    @Override
    public void getDebugInfo(List<String> left, List<String> right, EnumFacing side) {
        super.getDebugInfo(left, right, side);
        left.add("fluid = " + tank.getDebugString());
        //left.add("queue size = " + queue.size());
    }
}
