package net.mehvahdjukaar.supplementaries.inventories;

import net.mehvahdjukaar.supplementaries.block.tiles.PresentBlockTile;
import net.mehvahdjukaar.supplementaries.setup.ModRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import java.util.Objects;


public class PresentContainer extends AbstractContainerMenu {
    public final Container inventory;

    private final BlockPos pos;

    public PresentContainer(int id, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        this(id, playerInventory, null, packetBuffer.readBlockPos());

    }

    public PresentContainer(int id, Inventory playerInventory, Container inventory, BlockPos pos) {
        super(ModRegistry.PRESENT_BLOCK_CONTAINER.get(), id);

        this.pos = pos;

        //tile inventory
        this.inventory = Objects.requireNonNullElseGet(inventory, () -> new SimpleContainer(1) {
            public void setChanged() {
                super.setChanged();
                PresentContainer.this.slotsChanged(this);
            }
        });

        checkContainerSize(this.inventory, 1);
        this.inventory.startOpen(playerInventory.player);

        this.addSlot(new Slot(this.inventory, 0, 17, 23) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return PresentBlockTile.isAcceptableItem(stack);
            }
        });

        for (int si = 0; si < 3; ++si)
            for (int sj = 0; sj < 9; ++sj)
                this.addSlot(new Slot(playerInventory, sj + (si + 1) * 9, 8 + sj * 18, 84 + si * 18));
        for (int si = 0; si < 9; ++si)
            this.addSlot(new Slot(playerInventory, si, 8 + si * 18, 142));
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return this.inventory.stillValid(playerIn);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.inventory.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.inventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
        this.inventory.stopOpen(playerIn);
    }

}