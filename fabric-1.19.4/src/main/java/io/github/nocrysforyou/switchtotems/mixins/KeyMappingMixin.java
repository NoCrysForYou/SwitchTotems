package io.github.nocrysforyou.switchtotems.mixins;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import io.github.nocrysforyou.switchtotems.SwitchTotems;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {
    @Inject(method = "isDown", at = @At("RETURN"), cancellable = true)
    public void freedom(CallbackInfoReturnable<Boolean> cir) {
        if (!SwitchTotems.ensureYourFreedomIsSecuredFromNastyServerAdmins || !cir.getReturnValueZ()) return;
        Options options = Minecraft.getInstance().options;
        KeyMapping that = (KeyMapping) (Object) this;
        if (options.keyShift != that && options.keySprint != that && options.keyUse != that && options.keyAttack != that
                && options.keyUp != that && options.keyDown != that && options.keyLeft != that && options.keyRight != that
                && options.keySwapOffhand != that) return;
        cir.setReturnValue(false);
    }

}
