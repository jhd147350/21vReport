package jhd;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jhd147350
 *         SLA定义：https://doc.21vtech.com/pages/viewpage.action?pageId=1606435
 */
public class SLA {
	public static Map<Integer, Long> SLA_MAP = null;// =new HashMap<Integer,Long>();
	static {
		SLA_MAP = new HashMap<Integer, Long>();
		SLA_MAP.put(1, 15l * 60000l);
		SLA_MAP.put(2, 15l * 60000l);
		SLA_MAP.put(3, 120l * 60000l);
		SLA_MAP.put(4, 120l * 60000l);
		SLA_MAP.put(5, 120l * 60000l);
		SLA_MAP.put(6, 480l * 60000l);
		SLA_MAP.put(7, 480l * 60000l);
		SLA_MAP.put(8, 120l * 60l * 60000l);
		SLA_MAP.put(9, 0l);
		SLA_MAP.put(10, 0l);
	}

}
