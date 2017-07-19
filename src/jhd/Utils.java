package jhd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//工具类
public class Utils {

	// 将 data.txt中的数据转换成RemedyTicket数组
	public static List<RemedyTicket> getTicketList() {

		final String ticketDataFileName = "data.txt";
		List<RemedyTicket> tickets = new ArrayList<RemedyTicket>();
		FileReader reader = null;
		BufferedReader br = null;
		try {
			reader = new FileReader(ticketDataFileName);
			br = new BufferedReader(reader);
			// 表示一行数据
			String strRow = null;
			Map<String, Field> map = new HashMap<String, Field>();
			Map<Integer, Field> fmap = new HashMap<Integer, Field>();
			// 得到所有声明的字段
			Field[] fields = RemedyTicket.class.getDeclaredFields();

			// 得到Remedyticket中的所有注解
			for (Field field : fields) {
				// 拿到自定义的注解
				Label label = field.getAnnotation(Label.class);
				if (label != null) {
					// 将注解和字段映射一一对应
					map.put(label.value(), field);
				}
			}
			// 是否第一行，第一行是表头
			boolean firstLine = true;

			// 一次读取一行数据
			while ((strRow = br.readLine()) != null) {
				// 根据 \t 进行分割数据
				String[] data = strRow.split("\\t");
				/*for (String temp : data) {
					//System.err.print(temp + "|");
				}
				System.out.println();*/

				// 第一行表头处理如下
				if (firstLine) {
					// 如果是第一行 就把第一行的数据存入fmap，让data中的第一行数据对应起来
					for (int i = 0; i < data.length; i++) {
						String string = data[i];
						Field field = map.get(string);
						if (field != null) {
							fmap.put(i, field);
						}
					}
					firstLine = false;
				}
				// 所有数据行的处理如下
				else {
					RemedyTicket ticket = new RemedyTicket();
					for (int i = 0; i < data.length; i++) {
						Field field = fmap.get(i);
						if (field != null) {
							String strColumn = data[i];
							Object obj = null;
							Class<?> type = field.getType();
							// TODO 按类型 处理数据，不知道用switch可以解决不
							if (type == Date.class) {
								// 处理 这种数据 2017-6-29 22:53:49
								try {
									obj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strColumn);
								} catch (Exception e) {

									//e.printStackTrace();
									System.err.println(field.getAnnotation(Label.class).value()+":"+strColumn);
									obj = null;

								}
							}
							if (type == Integer.class || type == int.class) {

								try {
									String regEx = "[^0-9]";
									Pattern p = Pattern.compile(regEx);
									Matcher m = p.matcher(strColumn);
									obj = Integer.parseInt(m.replaceAll("").trim());
								} catch (Exception e) {
								}
							} else if (type == String.class) {
								obj = strColumn;
							}
							if (obj != null) {
								field.setAccessible(true);
								// 向ticket 对象赋值
								field.set(ticket, obj);
							}
						}
					}
					// 将对象添加到数组中
					tickets.add(ticket);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭file和buffer reader
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		return tickets;
	}

}
