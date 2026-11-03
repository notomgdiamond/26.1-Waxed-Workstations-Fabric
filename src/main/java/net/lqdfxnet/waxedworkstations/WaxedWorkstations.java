package net.lqdfxnet.waxedworkstations;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.ChatFormatting;
import java.util.Optional;
import java.util.Set;

public final class WaxedWorkstations implements ModInitializer {
    private static final Set<ResourceKey<PoiType>> EXCLUDED = Set.of(
        PoiTypes.NETHER_PORTAL, PoiTypes.LIGHTNING_ROD, PoiTypes.LODESTONE, PoiTypes.HOME
    );

    @Override
    public void onInitialize() {
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (!(level instanceof ServerLevel server) || !player.isShiftKeyDown()) return InteractionResult.PASS;
            ItemStack stack = player.getItemInHand(hand);
            if (!(stack.getItem() instanceof HoneycombItem) && !(stack.getItem() instanceof AxeItem)) return InteractionResult.PASS;
            BlockPos pos = hit.getBlockPos();
            BlockState state = level.getBlockState(pos);
            Optional<Holder<PoiType>> optional = PoiTypes.forState(state);
            if (optional.isEmpty()) return InteractionResult.PASS;
            Holder<PoiType> holder = optional.get();
            ResourceKey<PoiType> key = holder.getKey();
            if (key == null || EXCLUDED.contains(key)) return InteractionResult.PASS;
            PoiManager manager = server.getPoiManager();
            boolean exists = manager.existsAtPosition(key, pos);
            if (stack.getItem() instanceof HoneycombItem && exists) {
                manager.remove(pos);
                server.playSound(null, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1, 1);
                server.sendParticles(ParticleTypes.WAX_ON, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 8, 0.2, 0.2, 0.2, 0);
                player.sendOverlayMessage(Component.literal("Workstation waxed").withStyle(ChatFormatting.GOLD));
                if (!player.getAbilities().instabuild) stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
            if (stack.getItem() instanceof AxeItem && !exists) {
                manager.add(pos, holder);
                server.playSound(null, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1, 1);
                server.sendParticles(ParticleTypes.WAX_OFF, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 8, 0.2, 0.2, 0.2, 0);
                player.sendOverlayMessage(Component.literal("Workstation unwaxed").withStyle(ChatFormatting.GREEN));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });
    }
}
