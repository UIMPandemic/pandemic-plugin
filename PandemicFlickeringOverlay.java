package net.runelite.client.plugins.pandemic;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.BackgroundComponent;

import java.awt.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class PandemicFlickeringOverlay extends Overlay {

	private final PandemicPlugin plugin;
	private final PandemicConfig config;

	private final BackgroundComponent backgroundComponent = new BackgroundComponent();

	private final int DEFAULT_RANGE = 20;
	private final int DEFAULT_MIN = 20;
	private final double DEFAULT_FLICKER = 0.4;


	@Getter
	@Setter
	private int range = DEFAULT_RANGE;

	@Getter
	@Setter
	private int minAlpha = DEFAULT_MIN;

	@Getter
	@Setter
	private double flickerSpeed = DEFAULT_FLICKER;


	double alpha = minAlpha;



	@Inject
	private PandemicFlickeringOverlay(PandemicPlugin plugin, PandemicConfig config)
	{
		super(plugin);
		setPosition(OverlayPosition.TOP_CENTER);
		setPriority(OverlayPriority.HIGHEST);
		this.plugin = plugin;
		this.config = config;
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Pandemic overlay"));
	}

    @Override
    public Dimension render(Graphics2D graphics) {
		if(config.flickerEnabled()) {
			double maxAlpha = minAlpha + range;
			alpha += flickerSpeed;

			if (alpha >= maxAlpha || alpha <= minAlpha) flickerSpeed = -flickerSpeed;
			if (alpha >= maxAlpha) alpha = maxAlpha;
			if (alpha <= minAlpha) alpha = minAlpha;

			backgroundComponent.setRectangle(this.getBounds());
			Color pandemicColor = new Color(255, 0, 0, (int) alpha);
			graphics.setColor(pandemicColor);
			graphics.fillRect(-1000, -1000, 2000, 2000);

			this.backgroundComponent.render(graphics);
			return getBounds().getSize();
		}
		return null;

    }
}
