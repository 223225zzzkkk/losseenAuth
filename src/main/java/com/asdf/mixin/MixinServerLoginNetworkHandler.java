package com.asdf.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.util.Uuids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/**这个mixin的作用是判断客户端发来的UUID是不是离线UUID
 * 如果是就使用离线模式的登录逻辑，否则不做修改**/
@Mixin(ServerLoginNetworkHandler.class)
public abstract class MixinServerLoginNetworkHandler {
    @Shadow
    abstract void startVerify(GameProfile profile);


    @Inject(at = @At(value = "HEAD",
            target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V"),
            method = "onHello(Lnet/minecraft/network/packet/c2s/login/LoginHelloC2SPacket;)V", cancellable = true)
    private void init(LoginHelloC2SPacket packet, CallbackInfo ci) {
        if (packet.profileId().equals(Uuids.getOfflinePlayerUuid(packet.name()))) {
            startVerify(Uuids.getOfflinePlayerProfile(packet.name()));
            ci.cancel();
        }
    }
}
