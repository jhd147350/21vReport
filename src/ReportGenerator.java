import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportGenerator {

	private List<RemedyTicket> getTicketList(String filename) {

		List<RemedyTicket> tickets = null;
		FileReader reader = null;
		BufferedReader br = null;
		try {
			reader = new FileReader("data.txt");
			br = new BufferedReader(reader);
			String str = null;
			Map<String, Field> map = new HashMap<String, Field>();
			Map<Integer, Field> fmap = new HashMap<Integer, Field>();
			Field[] fields = RemedyTicket.class.getDeclaredFields();

			for (Field field : fields) {
				Label label = field.getAnnotation(Label.class);
				if (label != null) {
					map.put(label.value(), field);
				}
			}
			boolean first = false;
			tickets = new ArrayList<RemedyTicket>();

			while ((str = br.readLine()) != null) {
				String[] data = str.split("\\t");
				if (!first) {
					for (int i = 0; i < data.length; i++) {
						String string = data[i];
						Field field = map.get(string);
						if (field != null) {
							fmap.put(i, field);
						}
					}
					first = true;
				} else {
					RemedyTicket ticket = new RemedyTicket();
					for (int i = 0; i < data.length; i++) {
						Field field = fmap.get(i);
						if (field != null) {
							String strdata = data[i];
							Object obj = null;
							Class<?> type = field.getType();
							if (type == Date.class) {
								try {
									obj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strdata);
								} catch (Exception e) {
									System.out.print(ticket.getTicketId() + ":字段格式不正确" + field.getName());
									System.out.println("  "+field.getAnnotation(Label.class).value()+":"+strdata);
									field.getName();
									obj = null;
								}
							}
							if (type == Integer.class || type == int.class) {

								try {
									String regEx = "[^0-9]";
									Pattern p = Pattern.compile(regEx);
									Matcher m = p.matcher(strdata);
									obj = Integer.parseInt(m.replaceAll("").trim());
								} catch (Exception e) {
								}
							} else if (type == String.class) {
								obj = strdata;
							}
							if (obj != null) {
								field.setAccessible(true);
								field.set(ticket, obj);
							}
						}
					}
					tickets.add(ticket);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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

	public static void main(String[] args) throws Exception {
		ReportGenerator tester = new ReportGenerator();
		List<RemedyTicket> tickets = tester.getTicketList("data2.txt");
		tester.getAvgTime(tickets, "总体数据", null);
		tester.getSatisfaction(tickets, "总体数据", null);
		System.out
				.println("L1转出的Ticket数量:" + tickets.stream().filter(ticket -> isNotEmpty(ticket.getRtcLink())).count());

		System.out.println("L2转出L3的Ticket数量:" + tickets.stream()
				.filter(ticket -> ticket.SubmitterLevel == 2 && isNotEmpty(ticket.getRtcLink())).count());

		tester.getAvgTime(tickets, "SEV 1的", ticket -> ticket.getServity() == 1);
		tester.getAvgTime(tickets, "SEV 2的", ticket -> ticket.getServity() == 2);
		tester.getAvgTime(tickets, "SEV 3的", ticket -> ticket.getServity() == 3);
		tester.getAvgTime(tickets, "SEV 4的", ticket -> ticket.getServity() == 4);

		Map<String, List<RemedyTicket>> tickets2 = new HashMap<String, List<RemedyTicket>>();

		for (RemedyTicket remedyTicket : tickets) {
			String service = remedyTicket.getService();
			List<RemedyTicket> list = tickets2.get(service);
			if (list == null) {
				list = new ArrayList<RemedyTicket>();
			}
			list.add(remedyTicket);
			tickets2.put(remedyTicket.getService(), list);
		}

		Set<String> keys = tickets2.keySet();
		for (String service : keys) {
			List<RemedyTicket> list = tickets2.get(service);
			tester.getAvgTime(list, service, null);

		}
	}

	public void getSatisfaction(List<RemedyTicket> tickets, String prefix, Predicate<RemedyTicket> pre) {

		int num = 0;
		int totalSatisfaction = 0;
		List<Integer> totalSatisfactionId = new ArrayList<Integer>();
		for (RemedyTicket remedyTicket : tickets) {
			if (pre == null || pre.test(remedyTicket)) {
				Integer satisfaction = remedyTicket.getSatisfaction();
				if (satisfaction != null) {
					num++;
					totalSatisfaction += satisfaction;
					totalSatisfactionId.add(satisfaction);
				}
			}
		}

		System.out.println(
				prefix + "评分数量" + num + "," + totalSatisfactionId + "，满意度：" + (double) totalSatisfaction / num);

		System.out.println();

	}

	public void getAvgTime(List<RemedyTicket> tickets, String prefix, Predicate<RemedyTicket> pre) {

		long resloveTime = 0;
		long initTime = 0;
		int num = 0;
		int reslovedNum = 0;
		long maxTime = 0;
		String ticketId = null;
		List<String> unreslovedTicketId = new ArrayList<String>();
		int alertNum = 0;
		for (RemedyTicket remedyTicket : tickets) {
			if (pre == null || pre.test(remedyTicket)) {
				Date initDate = remedyTicket.getInitTime();
				Date reportDate = remedyTicket.getReportTime();
				Date resloveDate = remedyTicket.getResloveTime();
				String status = remedyTicket.getStatus();
				String id = remedyTicket.getTicketId();
				if ("已解决".equals(status) || "已关闭".equals(status)) {
					long curReslovedTime = resloveDate.getTime() - reportDate.getTime();
					resloveTime += curReslovedTime;
					if (maxTime < curReslovedTime) {
						maxTime = curReslovedTime;
						ticketId = id;
					}
					++reslovedNum;
				} else {
					unreslovedTicketId.add(id);
				}
				String type = remedyTicket.getTicketType();
				if ("alert ticket".equals(type)) {
					alertNum++;
				}
				if (initDate != null) {
					initTime += initDate.getTime() - reportDate.getTime();
				}
				num++;
			}
		}

		System.out.println(prefix + "总数" + num + "，末解决：" + unreslovedTicketId + ",Alert 数量：" + alertNum);

		System.out.println("\t平均响应时间：" + (num > 0 ? format(initTime / num) : "--"));
		System.out.println("\t平均解决时间：" + (reslovedNum > 0 ? format(resloveTime / reslovedNum) : "--"));
		System.out.println("\t最长解决时间：" + format(maxTime) + " - >" + ticketId);
		System.out.println();
	}

	private String format(long l) {

		l = l / 1000;
		long d = l / (3600 * 24);
		long h = (l - d * 3600 * 24) / 3600;
		long m = (l - d * 3600 * 24 - h * 3600) / 60;
		long s = (l - d * 3600 * 24 - h * 3600) % 60;
		String value = getValue(d, "d") + getValue(h, "h") + getValue(m, "m") + getValue(s, "s");
		return isEmpty(value) ? "--" : value;
	}

	public static boolean isEmpty(String string) {
		return string == null || string.trim().isEmpty();
	}

	public static boolean isNotEmpty(String string) {
		return string != null && !string.trim().isEmpty();
	}

	public static boolean isAllNotEmpty(String... strings) {
		if (strings != null) {
			for (String string : strings) {
				if (isEmpty(string)) {
					return false;
				}
			}
		}
		return true;
	}

	private String getValue(long d, String lab) {
		return d > 0 ? (d + lab) + " " : "";
	}

	@Target({ TYPE, METHOD, FIELD })
	@Retention(RUNTIME)
	public @interface Label {
		public String value();

	}

	public static class RemedyTicket {
		@Label("事件 ID*+")
		private String ticketId;
		@Label("标题*")
		private String label;
		@Label("严重程度*")
		private int servity;
		@Label("响应日期+")
		private Date initTime;

		@Label("状态*")
		private String status;

		@Label("报告日期+")
		private Date reportTime;

		@Label("最后解决日期")
		private Date resloveTime;
		@Label("提交日期")
		private Date submitTime;
		@Label("服务*")
		private String service;
		@Label("RTC link")
		private String rtcLink;
		@Label("类型*")
		private String ticketType;

		@Label("客户评分")
		private Integer satisfaction;

		@Label("Uni_Submitter_Group_Level")
		private int SubmitterLevel;

		public String getTicketId() {
			return ticketId;
		}

		public void setTicketId(String ticketId) {
			this.ticketId = ticketId;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public int getServity() {
			return servity;
		}

		public void setServity(int servity) {
			this.servity = servity;
		}

		public Integer getSatisfaction() {
			return satisfaction;
		}

		public void setSatisfaction(Integer satisfaction) {
			this.satisfaction = satisfaction;
		}

		public Date getInitTime() {
			return initTime;
		}

		public void setInitTime(Date initTime) {
			this.initTime = initTime;
		}

		public Date getReportTime() {
			return reportTime;
		}

		public void setReportTime(Date reportTime) {
			this.reportTime = reportTime;
		}

		public Date getResloveTime() {
			return resloveTime;
		}

		public void setResloveTime(Date resloveTime) {
			this.resloveTime = resloveTime;
		}

		public Date getSubmitTime() {
			return submitTime;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public void setSubmitTime(Date submitTime) {
			this.submitTime = submitTime;
		}

		public String getService() {
			return service;
		}

		public void setService(String service) {
			this.service = service;
		}

		public String getRtcLink() {
			return rtcLink;
		}

		public void setRtcLink(String rtcLink) {
			this.rtcLink = rtcLink;
		}

		public int getSubmitterLevel() {
			return SubmitterLevel;
		}

		public void setSubmitterLevel(int submitterLevel) {
			SubmitterLevel = submitterLevel;
		}

		public String getTicketType() {
			return ticketType;
		}

		public void setTicketType(String ticketType) {
			this.ticketType = ticketType;
		}

		@Override
		public String toString() {
			return "RemedyTicket [ticketId=" + ticketId + ", label=" + label + ", servity=" + servity + ", initTime="
					+ initTime + ", reportTime=" + reportTime + ", resloveTime=" + resloveTime + ", submitTime="
					+ submitTime + ", service=" + service + "]";
		}

	}

}