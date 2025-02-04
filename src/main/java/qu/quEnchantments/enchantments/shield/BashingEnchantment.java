package qu.quEnchantments.enchantments.shield;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import qu.quEnchantments.enchantments.ModEnchantments;
import qu.quEnchantments.enchantments.QuEnchantment;

public class BashingEnchantment extends QuEnchantment {
    public BashingEnchantment(Rarity weight, EnchantmentTarget type,EquipmentSlot ... slotTypes) {
        super(weight, type, slotTypes);
    }

    @Override
    public int getMinPower(int level) {
        return 15;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public void onBlock(LivingEntity defender, LivingEntity attacker, ItemStack stack, int level) {
        if (defender.world.isClient) return;
        double dx = defender.getX() - attacker.getX();
        double dz = defender.getZ() - attacker.getZ();
        while (dx * dx + dz * dz < 1.0E-4) {
            dx = (Math.random() - Math.random()) * 0.01;
            dz = (Math.random() - Math.random()) * 0.01;
        }
        attacker.knockbackVelocity = (float) (MathHelper.atan2(dz, dx) * 57.2957763671875 - (double) attacker.getYaw());
        attacker.takeKnockback(0.6f, dx, dz);
        if (EnchantmentHelper.getLevel(ModEnchantments.NIGHTBLOOD, stack) > 0) {
            ItemStack shield = defender.getActiveItem();
            shield.setDamage(shield.getMaxDamage() - 1);
            shield.damage(50, defender, e -> e.sendToolBreakStatus(defender.getActiveHand()));
            defender.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8f, 0.8f + defender.world.random.nextFloat() * 0.4f);
            Random random = defender.world.getRandom();
            for (int i = 0; i < 10; ++i) {
                double d = random.nextGaussian() * 0.02;
                double e = random.nextGaussian() * 0.02;
                double f = random.nextGaussian() * 0.02;
                ((ServerWorld) defender.world).spawnParticles(ParticleTypes.LARGE_SMOKE, defender.getParticleX(1.0), defender.getRandomBodyY(), defender.getParticleZ(1.0), 1, d, e, f, 0.0);
            }
        }
    }
}
