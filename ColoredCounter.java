package net.runelite.client.plugins.pandemic;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColoredCounter extends Counter {


	private Color col;

	public ColoredCounter(BufferedImage image, Plugin plugin, int count, Color color)
	{
		super(image, plugin, count);
		this.col = color;
	}

	@Override
	public Color getTextColor()
	{
		return col;
	}


}
