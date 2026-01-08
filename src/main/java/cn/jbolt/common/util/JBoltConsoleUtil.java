package cn.jbolt.common.util;

import java.util.Date;

public class JBoltConsoleUtil {
	public static void printJboltcn(){
		System.out.println("   _ _           _ _               \r\n" + 
			"  (_) |__   ___ | | |_   ___ _ __  \r\n" + 
			"  | | '_ \\ / _ \\| | __| / __| '_ \\ \r\n" + 
			"  | | |_) | (_) | | |_ _ (__| | | |\r\n" + 
			" _/ |_.__/ \\___/|_|\\__(_)___|_| |_|\r\n" + 
			"|__/                               ");
	}
	public static void printMessageWithDate(String message) {
		System.out.println("[JBolt Log]:["+DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss SSS")+"]"+message);
	}
	public static void printErrorMessageWithDate(String message) {
		System.err.println("[JBolt Log]:["+DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss SSS")+"]"+message);
	}
	public static void printMessage(String message) {
		System.out.println("[JBolt Log]:"+message);
	}
}
