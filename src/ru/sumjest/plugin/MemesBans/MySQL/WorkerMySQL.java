package ru.sumjest.plugin.MemesBans.MySQL;



import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


import ru.sumjest.plugin.MemesBans.ApiMySQL.MySQL;
import ru.sumjest.plugin.MemesBans.Utilities.BanData;

public class WorkerMySQL 
{

	String Host,Port,Username,Password,Database,Table;
	
	MySQL mysql;
	
	Connection c;
	
	public WorkerMySQL(String host, String port, String username, String password, String database, String table)
	{
		Host = host;
		Port = port;
		Username = username;
		Password = password;
		Database = database;
		Table = table;
	}
	
	public void Connect()
	{
		mysql = new MySQL(Host, Port, Database, Username, Password);		
		try{ c = mysql.openConnection(); System.out.println("[MemesBans] Connected to database \"" + Database + "\".");}
		catch(Exception ex) {ex.printStackTrace();}		
	}
	
	public boolean CreateTable()
	{
		Statement statement;
		
		try
		{
			statement = c.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXIST `" + Database + "`.`" + Table + "` (\r\n" + 
					"  `player` VARCHAR(30) NOT NULL,\r\n" + 
					"  `admin` VARCHAR(30) NOT NULL,\r\n" + 
					"  `timeban` TIMESTAMP NOT NULL,\r\n" + 
					"  `time` INT NOT NULL,\r\n" + 
					"  `reason` VARCHAT(45) NOT NULL,\r\n" + 
					"  PRIMARY KEY (`player`),\r\n" + 
					"  UNIQUE INDEX `player_UNIQUE` (`player` ASC))\r\n" + 
					"ENGINE = InnoDB\r\n" + 
					"DEFAULT CHARACTER SET = utf8;\r\n");
			return true;
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public void setBandata(BanData data)
	{
		try {
			if(!mysql.checkConnection())Connect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Statement state;
		
		
		try {
			state = c.createStatement();

			ResultSet res = state.executeQuery("SELECT * FROM " + Database + "." + Table + " WHERE player='" + data.player + "';");
			if(res.next()) {System.err.println("[MemesBans] The player \""+ data.player + "\" is already in the database"); return;}
		    state.executeUpdate("INSERT INTO " + Database + "." + Table + " (player,admin,timeban,time,reason) VALUES('" + data.player + "','" + data.admin + "','" + data.timeban + "','" + data.time + "','" + data.reason + "');");
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public int delBandata(String player)
	{
		try {
			if(!mysql.checkConnection())Connect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		Statement state;
		
		try {
			state = c.createStatement();
			ResultSet res = state.executeQuery("SELECT * FROM " + Database + "." + Table + " WHERE player='" + player + "';");
			if(!res.next()) {return 1;}
			state.executeUpdate("DELETE FROM `" + Database + "`.`" + Table +"` WHERE `player`='" + player + "';");
			return 0;
		}catch(Exception e)
		{
			e.printStackTrace();
			return 2;
		}
	}
	public BanData getBanData(String player)
	{
		try
		{
			if(!mysql.checkConnection()) {Connect();}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		Statement statement;
		ResultSet result;
		
		try
		{
			statement = c.createStatement();
			result = statement.executeQuery("SELECT player,admin,timeban,time,reason FROM `" + Database + "`.`" + Table + "` WHERE player='" + player + "';");
			if(!result.next()) {return null;}
			return new BanData(result.getString(1), result.getString(2), result.getLong(3), result.getInt(4), result.getString(5));
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
}