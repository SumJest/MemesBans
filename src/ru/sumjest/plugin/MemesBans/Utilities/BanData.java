package ru.sumjest.plugin.MemesBans.Utilities;

public class BanData 
{

	public String player,admin, reason;
	public Long timeban = 0L;
	public int time = 0;
	
	public BanData(String player, String admin, String reason)
	{
		this.player = player;
		this.admin = admin;
		this.reason = reason;
	}
	
	public BanData(String player, String admin, Long timeban, int time, String reason)
	{
		this.player = player;
		this.admin = admin;
		this.timeban = timeban;
		this.time = time;
		this.reason = reason;
	}
	
	
	
}
