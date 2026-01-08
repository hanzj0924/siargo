package cn.jbolt.common.util;

public class ColorUtil {
	private static final String[] colorClass= {"primary","success","info","danger","warning","secondary"};
	public static String colorClassByLevel(int level){
		if(level<1||level>6){
			return "primary";
		}
		
		return colorClass[level-1];
	}
	private static final String[] colorClass_priorityLevel= {"priorityLevel_1","priorityLevel_2","priorityLevel_3","priorityLevel_4","priorityLevel_5","priorityLevel_6"};

	public static String colorClassByPriorityLevel(int level){
		if(level<1||level>6){
			return "primary";
		}
		return colorClass_priorityLevel[level-1];
	}
}
