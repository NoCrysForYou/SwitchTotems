package io.github.nocrysforyou.switchtotems.mixins;

import io.github.nocrysforyou.switchtotems.SwitchTotems;
import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.TimeUnit;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow public LocalPlayer player;

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"))
    public void freedom(ClientPacketListener instance, Packet<?> packet) {
        if (!SwitchTotems.enabled) {
            instance.send(packet);
            return;
        }
        if (SwitchTotems.swapping || player == null || player.getMainHandItem().is(Items.TOTEM_OF_UNDYING) || player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) {
            return;
        }
        SwitchTotems.swapping = true;
        // If you think that this hell crap af code was written by me, you're right.
        // I could implement it in more "normal way" like adding 36 for hotbar or something like that.
        // There are four points in this approach
        // 1. It's not a hack.
        // 2. I don't care about code quality here. (actually anywhere)
        // 3. Cry about it.
        // 4. Nested code is unoptimized af, so your GT 630 will give out StackOverflowError while JIT-compiling this.
        Channel channel = ((ConnectionAccessor) instance.getConnection()).channel();
        NonNullList<ItemStack> items = player.getInventory().items;
        for (int[] slots : SwitchTotems.SLOTS) {
            SwitchTotems.shuffleArray(slots);
            for (int slot : slots) {
                ItemStack stack = items.get(slot);
                if (!stack.is(Items.TOTEM_OF_UNDYING)) continue;
                if (Inventory.isHotbarSlot(slot)) {
                    channel.eventLoop().schedule(() -> {
                        SwitchTotems.ensureYourFreedomIsRespectedByFilthyServerAntiCheats = true;
                        if (slot != player.getInventory().selected) {
                            instance.send(new ServerboundSetCarriedItemPacket(slot));
                        }
                        channel.eventLoop().schedule(() -> {
                            instance.send(packet);
                            channel.eventLoop().schedule(() -> {
                                int selected = player.getInventory().selected;
                                if (slot != selected) {
                                    instance.send(new ServerboundSetCarriedItemPacket(selected));
                                }
                                SwitchTotems.ensureYourFreedomIsRespectedByFilthyServerAntiCheats = false;
                                channel.eventLoop().schedule(() -> {
                                    instance.send(new ServerboundContainerClosePacket(0));
                                    instance.send(new ServerboundContainerClosePacket(0));
                                    SwitchTotems.swapping = false;
                                }, SwitchTotems.closeDelay, TimeUnit.MILLISECONDS);
                            }, SwitchTotems.scrollEndDelay, TimeUnit.MILLISECONDS);
                        }, SwitchTotems.swapDelay, TimeUnit.MILLISECONDS);
                    }, SwitchTotems.scrollStartDelay, TimeUnit.MILLISECONDS);
                    return;
                }
                ServerboundContainerClickPacket clickPacket = new ServerboundContainerClickPacket(0, 0, slot, 40, ClickType.SWAP, ItemStack.EMPTY, Int2ObjectMaps.emptyMap());
                SwitchTotems.ensureYourFreedomIsSecuredFromNastyServerAdmins = true;
                channel.eventLoop().schedule(() -> {
                    instance.send(clickPacket);
                    channel.eventLoop().schedule(() -> {
                        instance.send(new ServerboundContainerClosePacket(0));
                        instance.send(new ServerboundContainerClosePacket(0));
                        SwitchTotems.swapping = false;
                        SwitchTotems.ensureYourFreedomIsSecuredFromNastyServerAdmins = false;
                    }, SwitchTotems.closeDelay, TimeUnit.MILLISECONDS);
                }, SwitchTotems.swapDelay, TimeUnit.MILLISECONDS);
                return;
            }
        }
        instance.send(packet);
        SwitchTotems.swapping = false;
    }
}
