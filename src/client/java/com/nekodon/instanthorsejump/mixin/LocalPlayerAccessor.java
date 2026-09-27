package com.nekodon.instanthorsejump.mixin;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LocalPlayer.class)
public interface LocalPlayerAccessor {
    @Accessor("jumpRidingScale")
    void instantHorseJump$setJumpRidingScale(float value);

    @Invoker("sendRidingJump")
    void instantHorseJump$invokeSendRidingJump();
}


