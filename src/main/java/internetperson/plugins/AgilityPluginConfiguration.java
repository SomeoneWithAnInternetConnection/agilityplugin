package internetperson.plugins;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(
	keyName = "agilityplugin",
	name = "Agility plugin",
	description = "Configuration for the agility plugin"
)
public interface AgilityPluginConfiguration extends Config
{
	@ConfigItem(
		keyName = "enabled",
		name = "Enable overlay",
		description = "Configures whether the overlay is enabled"
	)
	default boolean enabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "Draw clickboxes",
		name = "Show clickbox geometry",
		description = "Shows clickbox geometry"
	)
	default boolean clickboxes() { return true; }

	@ConfigItem(
			keyName = "Draw wireframes",
			name = "Show wireframe geometry",
			description = "Shows wireframe geometry"
	)
	default boolean wireframes() { return true; }

	@ConfigItem(
			keyName = "Draw AABBs",
			name = "Show AABB geometry",
			description = "Shows AABB geometry"
	)
	default boolean AABBs() { return true; }
}
