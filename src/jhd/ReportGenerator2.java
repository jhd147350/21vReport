package jhd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import jhd.ReportData.ByService;
import jhd.ReportData.BySeverity;
import jhd.ReportData.MyDataType;

public class ReportGenerator2 {

	public void setDataByType(List<RemedyTicket> tickets, MyDataType tag, Predicate<RemedyTicket> pre,
			ReportData reportData) {
		long resolveTimeSum = 0;// 解决时长之和
		long ackTimeSum = 0;// 受理时长之和
		int num = 0;// 工单总数量
		int reslovedNum = 0;// 解决的工单总数量
		int alertNum = 0;// alert总数量
		int outOfSlaNum = 0;// 超时工单
		long maxResolveTime = 0;// 最大解决时长
		int satisfactionNum = 0;// 有满意度的工单数量
		int satisfactionSum = 0;// 满意度总和
		int createdByL1Num = 0;// L1创建的工单数量
		//int resolvedByL1Num = 0;// L1解决的工单数量 TODO 暂时不能计算
		//int transferedByL1Num = 0;// L1转出去的工单数量 TODO 暂时不能计算
		List<Integer> totalSatisfactionId = new ArrayList<Integer>();// 有满意度的工单分数集合
		String maxResolveTimeTicketId = null;// 最大解决时长的工单id
		List<String> unreslovedTicketId = new ArrayList<String>();// 未解决所有工单id

		for (RemedyTicket remedyTicket : tickets) {
			// 有条件的 通过pre判断出来
			if (pre == null || pre.test(remedyTicket)) {
				Date initDate = remedyTicket.getInitTime();
				Date submitDate = remedyTicket.getSubmitTime();
				Date resloveDate = remedyTicket.getResloveTime();
				String status = remedyTicket.getStatus();
				String id = remedyTicket.getTicketId();
				if ("已解决".equals(status) || "已关闭".equals(status)) {
					long curReslovedTime = resloveDate.getTime() - submitDate.getTime();
					resolveTimeSum += curReslovedTime;
					if (maxResolveTime < curReslovedTime) {
						maxResolveTime = curReslovedTime;
						maxResolveTimeTicketId = id;
					}
					reslovedNum++;
				} else {
					unreslovedTicketId.add(id);
				}

				if ("alert ticket".equals(remedyTicket.getTicketType())) {
					alertNum++;
				}
				if (initDate != null) {
					// 响应时间-提交时间为受理时长
					long ackTime = initDate.getTime() - submitDate.getTime();
					if (ackTime < 0) {
						System.err.println(remedyTicket.getTicketId() + ":响应时间小于提交时间，请检查数据");
					}
					outOfSlaNum += isOutOfSLA(remedyTicket.getTicketPriority(), ackTime) ? 1 : 0;// 超15分钟就超时了
					ackTimeSum += ackTime;
				}
				Integer satisfaction = remedyTicket.getSatisfaction();
				if (satisfaction != null) {
					satisfactionNum++;
					satisfactionSum += satisfaction;
					totalSatisfactionId.add(satisfaction);
				}
				if (remedyTicket.getSubmitterLevel() == 1) {// L1创建的工单
					createdByL1Num++;
				}
				num++;
			}
		}

		String avgIntilaResponseTime = num > 0 ? format(ackTimeSum / num) : "--";
		String avgResolvedTime = reslovedNum > 0 ? format(resolveTimeSum / reslovedNum) : "--";

		// 根据不同tag 给不同的成员变量赋值
		switch (tag) {
		case ALL:
			reportData.allTickets = num;
			reportData.customerTickets = num - alertNum;
			reportData.resolvedTickets = reslovedNum;
			reportData.outOfSlaTickets = outOfSlaNum + "";
			reportData.avgIntilaResponseTime = avgIntilaResponseTime;
			reportData.avgResolvedTime = avgResolvedTime;
			reportData.maxResolvedTime = format(maxResolveTime) + " ->" + maxResolveTimeTicketId;
			reportData.ticketCreatedByL1 = createdByL1Num;
			reportData.avgCustomerSatisfaction = (double) satisfactionSum / satisfactionNum + " " + totalSatisfactionId;

			break;
		case SEV1:
		case SEV2:
		case SEV3:
		case SEV4:
			BySeverity bySeverity = reportData.getBySeverityInstance();
			bySeverity.severity = tag;
			bySeverity.num = num;
			bySeverity.avgIntilaResponseTime = avgIntilaResponseTime;
			reportData.reportDataBySeverity.add(bySeverity);
			// pre.reportData.reportDataBySeverity.add(bySeverity);

			break;
		case Remedy:
		case Bluemix:
		case IoT:
		case Cloudant:
		case DashDB:
		case SSO:
		case Blockchain:
		case MessageHub:
			ByService byService = reportData.getByServiceInstance();
			byService.service = tag;
			byService.num = num;
			byService.resolvedNum = reslovedNum;
			byService.openNum = num - reslovedNum;
			byService.avgIntilaResponseTime = avgIntilaResponseTime;
			byService.avgResolvedTime = avgResolvedTime;
			reportData.reportDataByService.add(byService);
			break;

		default:
			break;
		}
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

	public boolean isOutOfSLA(int priority, long ackTime) {
		long sla_time = SLA.SLA_MAP.get(priority);
		return ackTime > sla_time ? true : false;
	}
}