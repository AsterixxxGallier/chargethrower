package mod.asterixxx.chargethrower;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

@Mod.EventBusSubscriber(modid = ChargeThrower.MODID)
public class Listener {
	@SubscribeEvent
	public static void onFireballImpact(ProjectileImpactEvent.Fireball event) {
		Entity entityHit = event.getRayTraceResult().entityHit;
		EntityLivingBase shootingEntity = event.getFireball().shootingEntity;

		// Make sure that the shooting entity can't be hit by the fireball
		if (entityHit == shootingEntity && shootingEntity != null) event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
		EntityLivingBase entity = event.getEntityLiving();
		World world = entity.world;
		if (world.isRemote) return;


		ItemStack stack = event.getItemStack();

		if (stack.getItem() instanceof ItemFireball) {
			Vec3d lookVec = entity.getLookVec();

			Random random = world.rand;

			double accelX =
				(random.nextGaussian() * ModConfig.fireChargeThrowInaccuracy + lookVec.x) * ModConfig.fireChargeThrowVelocity;
			double accelY =
				(random.nextGaussian() * ModConfig.fireChargeThrowInaccuracy + lookVec.y) * ModConfig.fireChargeThrowVelocity;
			double accelZ =
				(random.nextGaussian() * ModConfig.fireChargeThrowInaccuracy + lookVec.z) * ModConfig.fireChargeThrowVelocity;

			double posX = entity.posX;
			// -0.1 because it's the same with snowballs
			double posY = entity.posY + entity.getEyeHeight() - 0.1;
			double posZ = entity.posZ;

			if (ModConfig.fireChargeExplosionPower > 0) {
				EntityLargeFireball fireball = new EntityLargeFireball(world);

				// Code essentially copied from a different EntityLargeFireball constructor because that one is client-side only for no apparent reason
				fireball.setLocationAndAngles(posX, posY, posZ, fireball.rotationYaw, fireball.rotationPitch);
				fireball.setPosition(posX, posY, posZ);
				double d0 = MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
				fireball.accelerationX = accelX / d0 * 0.1D;
				fireball.accelerationY = accelY / d0 * 0.1D;
				fireball.accelerationZ = accelZ / d0 * 0.1D;

				fireball.shootingEntity = entity;
				fireball.explosionPower = ModConfig.fireChargeExplosionPower;

				world.spawnEntity(fireball);
			} else {
				// This constructor is not client-side only, so we can use this here
				EntitySmallFireball fireball = new EntitySmallFireball(world, posX, posY, posZ, accelX, accelY, accelZ);

				fireball.shootingEntity = entity;

				world.spawnEntity(fireball);
			}

			entity.setFire(ModConfig.burnTimeOnFireChargeThrow);

			boolean isPlayer = entity instanceof EntityPlayer;
			boolean isInCreativeMode = false;
			if (entity instanceof EntityPlayer)
				isInCreativeMode = ((EntityPlayer) entity).capabilities.isCreativeMode;

			if (!isPlayer || !isInCreativeMode)
				stack.shrink(1);

			if (isPlayer && (ModConfig.cooldownInCreativeMode || !isInCreativeMode)) {
				EntityPlayer player = (EntityPlayer) entity;
				// Set a cooldown, like the ender pearl cooldown
				player.getCooldownTracker().setCooldown(stack.getItem(), ModConfig.cooldownDuration);
			}
		}
	}
}
