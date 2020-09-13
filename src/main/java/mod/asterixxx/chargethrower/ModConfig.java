package mod.asterixxx.chargethrower;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ChargeThrower.MODID)
public class ModConfig {
	@Config.Comment("How long entities will burn when throwing fireballs, in seconds; set to 0 to disable")
	public static int burnTimeOnFireChargeThrow = 2;

	@Config.Comment("How fast fire charges will be when thrown")
	public static double fireChargeThrowVelocity = 8;

	@Config.Comment("Direction randomness of thrown fireballs, droppers have 0.05 for example; set to 0 to disable")
	public static double fireChargeThrowInaccuracy = 0.05;

	@Config.Comment("The power of the impact explosion of thrown fire charges; set to 0 to use small fireballs, as shot from blazes and dispensers; ghast fireballs, for example, have an explosion power of 1, which can break netherrack and dirt, but not stone")
	public static int fireChargeExplosionPower = 1;

	@Config.Comment("If set to false, the fire charge throw cooldown is disabled in creative")
	public static boolean cooldownInCreativeMode = false;

	@Config.Comment("The duration of the cooldown after throwing a fire charge, in ticks (20 ticks = 1 second); 20 is default for ender pearls for example")
	public static int cooldownDuration = 30;

	@Mod.EventBusSubscriber(modid = ChargeThrower.MODID)
	private static class EventHandler {
		/**
		 * Inject the new values and save to the config file when the config has been changed from the GUI.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(ChargeThrower.MODID)) {
				ConfigManager.sync(ChargeThrower.MODID, Config.Type.INSTANCE);
			}
		}
	}
}
