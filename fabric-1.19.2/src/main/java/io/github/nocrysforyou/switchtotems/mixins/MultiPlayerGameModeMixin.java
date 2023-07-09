package io.github.nocrysforyou.switchtotems.mixins;

import io.github.nocrysforyou.switchtotems.SwitchTotems;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    // Mixin that prevents Minecraft from synchronization hotbar slot while we're changing slots.
    // Actually, we could just lock the player hotbar swapper, but we love democracy and
    // allow the player to choose what they want to do. The right to choose freely is the main goal
    // of this mod, because some servers may consider this mod a hack, but it's your right to choose what's
    // hack and what's not. You can choose ClickCrystals and SwitchTotems combo and what can these filthy
    // server admins do? Ban you? Ha-ha. IT'S YOUR RIGHT. YOUR FREEDOM! 🦅🦅🦅🦅
    @Inject(method = "ensureHasSentCarriedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V", shift = At.Shift.BEFORE), cancellable = true)
    public void freedom(CallbackInfo ci) {
        if (!SwitchTotems.ensureYourFreedomIsRespectedByFilthyServerAntiCheats) return;
        ci.cancel();
    }
}
