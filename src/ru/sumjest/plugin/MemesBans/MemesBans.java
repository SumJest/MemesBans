package ru.sumjest.plugin.MemesBans;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import ru.sumjest.plugin.MemesBans.Listeners.PlayerListener;
import ru.sumjest.plugin.MemesBans.MySQL.WorkerMySQL;
import ru.sumjest.plugin.MemesBans.Utilities.BanData;

public class MemesBans extends JavaPlugin
{
	
	public static Configuration config;
	public static WorkerMySQL mysql;
	
	public void onEnable()
	{
		registerEvents(this, new PlayerListener());
		this.saveDefaultConfig();
		//config.addDefault("command.ban.usage", "§aИспользуйте: /<command> [player] {time}");
		config = getConfig();
		if(config.getBoolean("mysql.enabled")) {mysql = new WorkerMySQL(config.getString("mysql.host"), config.getString("mysql.port"), config.getString("mysql.username"), config.getString("mysql.password"), config.getString("mysql.database"), config.getString("mysql.table")); mysql.Connect();}
	}
	
	public void onDisabe()
	{
		saveConfig();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(label.equalsIgnoreCase("ban"))
		{
			if(sender instanceof Player && !((Player)sender).hasPermission("ru.sumjest.plugin.memesbans.ban")) {sender.sendMessage(config.getString("permission")); return true;}
			if(args.length < 2) { sender.sendMessage(config.getString("command.ban.usage")); return true;}
			if(Bukkit.getServer().getPlayer(args[0])==null) {sender.sendMessage(config.getString("player_not_found")); return true;}
			String reason = "";
			for(int i = 1; i < args.length; i ++)
			{
				if(reason == "") {reason += args[i];}
				else { reason += " " + args[i];}
			}
			mysql.setBandata(new BanData(args[0],sender.getName(),0L,0,reason)); Bukkit.getServer().getPlayer(args[0]).kickPlayer(config.getString("you_are_banned").replaceAll("%admin%", sender.getName()).replaceAll("%reason%", reason));
			if(sender instanceof Player) {Bukkit.getServer().broadcastMessage(config.getString("admin_ban_player").replaceAll("%admin%", sender.getName()).replaceAll("%player%", args[0]).replaceAll("%reason%", reason));}
		}else if(label.equalsIgnoreCase("memesbans"))
		{
			if(sender instanceof Player && !((Player)sender).hasPermission("ru.sumjest.plugin.memesbans.memesbans")) {sender.sendMessage(config.getString("permission")); return true;}
			if(args.length==0) { sender.sendMessage(config.getString("command.memesbans.message"));}
			else if(args[0].equalsIgnoreCase("reload")) { reloadConfig(); sender.sendMessage(config.getString("command.memesbans.reload"));}
			else {sender.sendMessage(config.getString("command.memesbans.usage"));}
		}else if(label.equalsIgnoreCase("unban"))
		{
			if(sender instanceof Player && !((Player)sender).hasPermission("ru.sumjest.plugin.memesbans.unban")) {sender.sendMessage(config.getString("permission")); return true;}
			if(args.length==0) {sender.sendMessage(config.getString("command.unban.usage")); return true;}
			int res = mysql.delBandata(args[0]);
			if(res==1)
			{
				sender.sendMessage(config.getString("player_not_banned").replaceAll("%player%", args[0]));
			}else if(res==0)
			{
				sender.sendMessage(config.getString("player_unbanned").replaceAll("%player%", args[0]));
			}
		}else if(label.equalsIgnoreCase("timeban"))
		{
			if(sender instanceof Player && !((Player)sender).hasPermission("ru.sumjest.plugin.memesbans.timeban")) {sender.sendMessage(config.getString("permission")); return true;}
			if(args.length < 3) { sender.sendMessage(config.getString("command.timeban.usage")); return true;}
			if(!isInteger(args[1])) {sender.sendMessage(config.getString("command.timeban.usage")); return true;}
			if(Bukkit.getServer().getPlayer(args[0])==null) {sender.sendMessage(config.getString("player_not_found").replaceAll("%player%", args[0])); return true;}
			String reason = "";
			for(int i = 2; i < args.length; i ++)
			{
				if(reason == "") {reason += args[i];}
				else { reason += " " + args[i];}
			}
			mysql.setBandata(new BanData(args[0],sender.getName(),System.currentTimeMillis()/1000,Integer.parseInt(args[1]),reason)); Bukkit.getServer().getPlayer(args[0]).kickPlayer(config.getString("you_are_timebanned").replaceAll("%admin%", sender.getName()).replaceAll("%reason%", reason).replaceAll("%hour%", args[1]).replaceAll("%min%", "0"));
			if(sender instanceof Player) {Bukkit.getServer().broadcastMessage(config.getString("admin_ban_player").replaceAll("%admin%", sender.getName()).replaceAll("%player%", args[0]).replaceAll("%reason%", reason));}
		}
		else if(label.equalsIgnoreCase("offban"))
		{
			if(sender instanceof Player && !((Player)sender).hasPermission("ru.sumjest.plugin.memesbans.offban")) {sender.sendMessage(config.getString("permission")); return true;}
			if(args.length < 2) { sender.sendMessage(config.getString("command.offban.usage")); return true;}
			String reason = "";
			for(int i = 1; i < args.length; i ++)
			{
				if(reason == "") {reason += args[i];}
				else { reason += " " + args[i];}
			}
			mysql.setBandata(new BanData(args[0],sender.getName(),0L,0,reason));
			sender.sendMessage(config.getString("player_offline_ban").replaceAll("%player%", args[0]).replaceAll("%reason%", reason));
		}else if(label.equalsIgnoreCase("offtimeban"))
		{
			if(sender instanceof Player && !((Player)sender).hasPermission("ru.sumjest.plugin.memesbans.offtimeban")) {sender.sendMessage(config.getString("permission")); return true;}
			if(args.length < 3) { sender.sendMessage(config.getString("command.offtimeban.usage")); return true;}
			if(!isInteger(args[1])) {sender.sendMessage(config.getString("command.offtimeban.usage")); return true;}
			String reason = "";
			for(int i = 2; i < args.length; i ++)
			{
				if(reason == "") {reason += args[i];}
				else { reason += " " + args[i];}
			}
			mysql.setBandata(new BanData(args[0],sender.getName(),System.currentTimeMillis()/1000,Integer.parseInt(args[1]),reason));
			sender.sendMessage(config.getString("player_offline_timeban").replaceAll("%player%", args[0]).replaceAll("%reason%", reason).replaceAll("%hour%", args[1]));
		}
		return true;
	}
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}
	public void registerEvents(Plugin plugin, Listener... listeners)
	{
		for(Listener listener:listeners)
		{
			plugin.getServer().getPluginManager().registerEvents(listener, plugin);
		}
	}
	
}
