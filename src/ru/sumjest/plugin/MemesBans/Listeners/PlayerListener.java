package ru.sumjest.plugin.MemesBans.Listeners;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import ru.sumjest.plugin.MemesBans.MemesBans;
import ru.sumjest.plugin.MemesBans.Utilities.BanData;



public class PlayerListener implements Listener
{
	
	@EventHandler
	public void onPlayerPreLogin(PlayerLoginEvent event)
	{
		BanData data =MemesBans.mysql.getBanData(event.getPlayer().getName());
		if(data!=null) 
		{
			if(data.timeban==0) 
			{
				event.setResult(Result.KICK_OTHER);
				event.setKickMessage(MemesBans.config.getString("you_are_banned").replaceAll("%admin%", data.admin).replaceAll("%reason%", data.reason));
			}else
			{
				long remaing = (data.timeban + data.time*3600)-System.currentTimeMillis()/1000;
				if(remaing>0) 
				{
					event.setResult(Result.KICK_OTHER);
					long hours = remaing/3600;
					int min = (int) ((remaing-hours*3600)/60);
					event.setKickMessage(MemesBans.config.getString("you_are_timebanned").replaceAll("%admin%", data.admin).replaceAll("%reason%", data.reason).replaceAll("%hour%", String.valueOf(hours)).replaceAll("%min%", String.valueOf(min)));
					return;
				}
				MemesBans.mysql.delBandata(event.getPlayer().getName());
			}
				
				
		
		}
	}

}
