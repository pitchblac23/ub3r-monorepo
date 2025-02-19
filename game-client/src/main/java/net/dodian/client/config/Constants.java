package net.dodian.client.config;

/**
 * Just a good old dirty constants file for the client,
 * so we have some easy to access values that might change.
 */
public class Constants {

	/*
	 * Client Configurations
	 */
	public static String WINDOW_TITLE     		= "Dodian";

	/*
	 * Server Connection Details
	 */
	public static String SERVER_HOSTNAME  		= "127.0.0.1"; //"play.dodian.net"; for live server
	public static int    SERVER_GAME_PORT 		= 43594; //43594 main game, 6565 is beta testing
	public static int	 SERVER_JAGGRAB_PORT	= SERVER_GAME_PORT;

	/*
	 * Updating, Web & Cache
	 */
	public static String CLIENT_DOWNLOAD_URL	= "https://dodian.net/client/DodianClient.jar";
	public static String CACHE_DOWNLOAD_URL		= "https://www.dropbox.com/scl/fi/fwhlcznd0zl33ql5gvyqv/dodian-temp.zip?rlkey=a95btwkge4f0kzpzt3w32hnqf&dl=1"; // Offical .net "https://dodian.net/client/cacheosrs.zip";
	public static String CACHE_LOCAL_DIRECTORY	= System.getProperty("user.home") + "/.dodian-temp/";
}
