/*
 * Copyright (c) 2018, TheLonelyDev <https://github.com/TheLonelyDev>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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

import com.google.gson.Gson;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;

import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.BoostedLevelChanged;
import net.runelite.api.events.GameTick;


import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
	name = "Pandemic",
	description = "Pandemic Game Mode plug-in",
	tags = {"game mode", "pandemic"}
)
public class PandemicPlugin extends Plugin
{
	private static final String CONFIG_GROUP = "pandemic";
	//private static final String MARK = "Mark tile";
	//private static final String WALK_HERE = "Walk here";

	private static final Gson gson = new Gson();

	@Inject
	private Client client;


	@Inject
	private ConfigManager configManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private PandemicInfoOverlay pandemicInfoOverlay;

	@Inject
	private PandemicDebugOverlay pandemicDebugOverlay;

	@Inject
	private PandemicFlickeringOverlay pandemicFlickeringOverlay;

	@Inject
	private PandemicDarknessOverlay pandemicDarknessOverlay;

	@Inject
	private PandemicTileIndicatorsOverlay pandemicTileIndicatorsOverlay;

	@Inject
	private PandemicConfig pandemicConfig;

	@Inject
	private KeyManager keyManager;




	@Provides
	PandemicConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PandemicConfig.class);
	}

	@Getter
	private PandemicSession session;

	//TODO added this since the getter annotation does not seem to work?? At least intellij does not detect it
	public PandemicSession getSession(){
		return session;
	}

	private WorldPoint previousPlayerPosition = null;


	//TODO remove this
	int getCurrentRegion(){
		return client.getLocalPlayer().getWorldLocation().getRegionID();
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(pandemicInfoOverlay);
		overlayManager.add(pandemicDebugOverlay);

	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(pandemicInfoOverlay);
		overlayManager.remove(pandemicDebugOverlay);
		if(session != null) stopPandemicSession();
	}


	@Subscribe
	public void onGameTick(final GameTick event)
	{

		WorldPoint currentPosition = client.getLocalPlayer().getWorldLocation();
		if(!currentPosition.equals(previousPlayerPosition))
		{
				onPlayerMove(previousPlayerPosition, currentPosition);
		}
		previousPlayerPosition = currentPosition;


		if(inPandemicSession()){
			onPandemicTick();
		}


	}

	public void onPandemicTick(){
		if(client.getLocalDestinationLocation() != null) {
			int distanceToTarget = client.getLocalDestinationLocation().distanceTo(client.getLocalPlayer().getLocalLocation());
			if (session.getAllowedSteps() < distanceToTarget) {
				session.setAlert(true);
			} else {
				session.setAlert(false);
			}
		}
	}

	@Subscribe
	public void onHitsplatApplied(final HitsplatApplied hitsplatApplied){

		if(!inPandemicSession()) return;
		if(hitsplatApplied.getActor().getName().equals(client.getLocalPlayer().getName())){
			session.incrementDamageCount(hitsplatApplied.getHitsplat().getAmount(), client.getLocalPlayer().getWorldLocation().getRegionID());
		}

	}


	//TODO Check if there is really no native onPlayerMove in RuneLite
	protected void onPlayerMove(final WorldPoint from, final WorldPoint to)
	{

		if(from == null){
			//Just logged in!
			if(!isSafe(to))
				startPandemicSession();
			return;
		}


		boolean fromSafe = isSafe(from);
		boolean toSafe = isSafe(to);

		//Entered the safe zone
		if(toSafe){
			if(session != null)
				stopPandemicSession();
		}

		//Exit the safe zone
		else if(fromSafe && !toSafe){
			startPandemicSession();
		}

		//Move within an unsafe area
		else if (!fromSafe && !toSafe) {
			int distanceInTick = from.distanceTo2D(to);

			if (distanceInTick <= 2) {
				//Running is two moves per tick
				if(getSession() == null) startPandemicSession(); //this happens if we update the configuration (changing safety without a move)
				getSession().incrementStepCount(distanceInTick, from.getRegionID());
			}else{
				//Teleport or other movement
				if(distanceInTick > 20) {
					stopPandemicSession();
					startPandemicSession();
				}
			}
		}

	}


	protected boolean isSafe(final WorldPoint point)
	{
		String safeRegionString = pandemicConfig.safeRegionString();
		String[] regions = safeRegionString.split(",");
		String pointRegion = point.getRegionID()+"";
		for(String region : regions){
			if(pointRegion.equals(region)) return true;
		}
		return false;
	}

	protected boolean inPandemicSession(){
		return getSession() != null;
	}

	protected void startPandemicSession(){

		session = new PandemicSession(client.getRealSkillLevel(Skill.HITPOINTS));
		client.addChatMessage(ChatMessageType.GAME, "Pandemic", "You have entered an infected region.", "Pandemic");
		client.setSkyboxColor(0x7c0a02);
		overlayManager.add(pandemicFlickeringOverlay);
		overlayManager.add(pandemicTileIndicatorsOverlay);
		overlayManager.add(pandemicDarknessOverlay);
		//client.getScene().setDrawDistance(0);
		//TODO use for alert client.getLocalDestinationLocation()
		//overlayManager.add(pandemicFlickeringOverlay);
	}

	protected void stopPandemicSession(){
		session = null;
		client.addChatMessage(ChatMessageType.GAME, "Pandemic", "You have entered a safe zone.", "Pandemic");
		client.setSkyboxColor(0x000000);
		overlayManager.remove(pandemicFlickeringOverlay);
		overlayManager.remove(pandemicTileIndicatorsOverlay);
		overlayManager.remove(pandemicDarknessOverlay);
		//overlayManager.remove(pandemicFlickeringOverlay);
	}


}