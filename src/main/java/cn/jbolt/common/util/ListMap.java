/**
 * 
 */
package cn.jbolt.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author 小木
 *
 */
public class  ListMap<K,T> extends HashMap<K,List<T>>  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ListMap(){
		super();
	}
	
	/**
	 * 添加实体
	 * @param key
	 * @param value
	 */
	
	public void addItem(K key,T value){
		List<T> l = get(key);
		if(l==null){
			l = new ArrayList<T>();
			l.add(value);
			put(key,l);
		}else{
			l.add(value);
		}
	}
	

}
