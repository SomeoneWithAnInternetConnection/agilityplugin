package internetperson.plugins;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Binder;
import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.Overlay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
	name = "Agility plugin"
)
public class AgilityPlugin extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(AgilityPlugin.class);

	@Inject
	Client client;

	@Inject
	AgilityOverlay overlay;

	@Override
	public void configure(Binder binder)
	{
		binder.bind(AgilityOverlay.class);
	}

	@Override
	public Overlay getOverlay()
	{
		return overlay;
	}

	@Provides
	AgilityPluginConfiguration provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AgilityPluginConfiguration.class);
	}

}
