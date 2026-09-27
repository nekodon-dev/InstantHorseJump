package com.nekodon.instanthorsejump.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin {
    @Unique
    private boolean instantHorseJump$wasJumpDown;

    @Inject(method = "tick", at = @At("TAIL"))
    private void instantHorseJump$afterKeyboardTick(CallbackInfo ci) {
        KeyboardInput keyboardInput = (KeyboardInput) (Object) this;
        Input keys = keyboardInput.keyPresses;
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || keys == null) {
            instantHorseJump$wasJumpDown = false;
            return;
        }

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof AbstractHorse) || player.jumpableVehicle() == null) {
            instantHorseJump$wasJumpDown = false;
            return;
        }

        boolean jumpDown = keys.jump();

        if (jumpDown && !instantHorseJump$wasJumpDown) {
            // Vanilla applies jump power locally before notifying the server.
            // The ridden horse is simulated by its controlling client.
            player.jumpableVehicle().onPlayerJump(100);
            LocalPlayerAccessor accessor = (LocalPlayerAccessor) (Object) player;
            accessor.instantHorseJump$setJumpRidingScale(1.0F);
            accessor.instantHorseJump$invokeSendRidingJump();
            accessor.instantHorseJump$setJumpRidingScale(0.0F);
        }

        instantHorseJump$wasJumpDown = jumpDown;

        // Hide the held jump key from vanilla's charge/release state machine.
        // All other movement buttons are preserved exactly as they were.
        keyboardInput.keyPresses = new Input(
                keys.forward(),
                keys.backward(),
                keys.left(),
                keys.right(),
                false,
                keys.shift(),
                keys.sprint()
        );
    }
}


