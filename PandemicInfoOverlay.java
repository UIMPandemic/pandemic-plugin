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
import net.runelite.api.queries.EquipmentItemQuery;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.*;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.QueryRunner;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

class PandemicInfoOverlay extends Overlay
{
	private final PandemicPlugin plugin;

	private InfoBoxManager infoBoxManager;

	private final ItemManager itemManager;


	private Counter counter;
	private Counter regionCounter;


	@Inject
	private PandemicInfoOverlay(PandemicPlugin plugin, PandemicConfig config, Client client, ItemManager itemManager, InfoBoxManager infoBoxManager)
	{
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.HIGHEST);
		this.plugin = plugin;
		this.itemManager = itemManager;
		this.infoBoxManager = infoBoxManager;
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Pandemic overlay"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{

		if (counter != null && !plugin.inPandemicSession()){
			infoBoxManager.removeInfoBox(counter);
			infoBoxManager.removeInfoBox(regionCounter);
			counter = null;
			regionCounter = null;
		}
		if(plugin.inPandemicSession()) {

			int itemSpriteId = ItemID.LEATHER_BOOTS;

			if(counter == null) {
				BufferedImage image = itemManager.getImage(itemSpriteId);
				BufferedImage scroll = itemManager.getImage(ItemID.CLUE_SCROLL_EASY_2678);
				counter = new ColoredCounter(image, plugin, plugin.getSession().getAllowedSteps(), Color.WHITE);
				regionCounter = new ColoredCounter(scroll, plugin, plugin.getSession().getStepsGainableInRegion(plugin.getCurrentRegion()), Color.WHITE);
				infoBoxManager.addInfoBox(counter);
				infoBoxManager.addInfoBox(regionCounter);
			}else{
				counter.setCount(plugin.getSession().getAllowedSteps());
				regionCounter.setCount(plugin.getSession().getStepsGainableInRegion(plugin.getCurrentRegion()));
			}


		}


		return null;
	}
}
