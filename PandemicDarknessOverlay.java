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

public class PandemicDarknessOverlay extends Overlay {

	private final PandemicPlugin plugin;
	private final PandemicConfig config;

	private final BackgroundComponent backgroundComponent = new BackgroundComponent();

	private final int MAX_DARKNESS = 180;
	private final int MIN_DARKNESS = 50;



	@Inject
	private PandemicDarknessOverlay(PandemicPlugin plugin, PandemicConfig config)
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
		//TODO magic numbers to settings
		PandemicSession session = plugin.getSession();


		int alpha = MAX_DARKNESS - session.getAllowedSteps()*10;
		if(alpha < MIN_DARKNESS) alpha = MIN_DARKNESS;
		if(alpha > MAX_DARKNESS) alpha = MAX_DARKNESS;
		backgroundComponent.setRectangle(this.getBounds());
		if(session.getAllowedSteps() < 1) alpha = 255;
		Color pandemicColor = new Color(0, 0, 0, alpha);
		graphics.setColor(pandemicColor);
		graphics.fillRect(-1000, -1000, 2000, 2000);

		this.backgroundComponent.render(graphics);
		return getBounds().getSize();

    }
}
