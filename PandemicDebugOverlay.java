/*
 * Copyright (c) 2018, Seth <http://github.com/sethtroll>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.pandemic;

import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

class PandemicDebugOverlay extends Overlay
{
	private final PandemicPlugin plugin;
	private final PandemicConfig config;

	private final PanelComponent panelComponent = new PanelComponent();


	private InfoBoxManager infoBoxManager;

	private final ItemManager itemManager;
	private final Client client;


	private Counter counter;

	private BufferedImage getImage(Item item)
	{
		ItemComposition itemComposition = itemManager.getItemComposition(item.getId());
		return itemManager.getImage(item.getId(), item.getQuantity(), itemComposition.isStackable());
	}

	@Inject
	private PandemicDebugOverlay(PandemicPlugin plugin, PandemicConfig config, Client client, ItemManager itemManager, InfoBoxManager infoBoxManager)
	{
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.HIGHEST);
		this.plugin = plugin;
		this.config = config;
		this.client = client;
		this.itemManager = itemManager;
		this.infoBoxManager = infoBoxManager;
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Pandemic overlay"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{

		panelComponent.getChildren().clear();
		if(config.debugEnabled()) {


			if (plugin.inPandemicSession()) {

				PandemicSession session = plugin.getSession();
				panelComponent.getChildren().add(LineComponent.builder()
						.left("Steps taken")
						.right(Integer.toString(session.getStepsTaken()))
						.build());

				panelComponent.getChildren().add(LineComponent.builder()
						.left("Damage taken")
						.right(Integer.toString(session.getDamageTaken()))
						.build());

				panelComponent.getChildren().add(LineComponent.builder()
						.left("Allowed steps")
						.right(Integer.toString(session.getAllowedSteps()))
						.build());

			}


			panelComponent.getChildren().add(LineComponent.builder()
					.left("Region ID")
					.right(Integer.toString(plugin.getCurrentRegion()))
					.build());

		}
		return panelComponent.render(graphics);
	}
}
