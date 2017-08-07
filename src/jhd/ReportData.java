package jhd;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jhd147350 TS周报需要输出的数据
 * 
 */
public class ReportData {
	/**
	 * all表示前面所有数据 severity表示reportDataBySeverity的数据 service表示reportDataByService的数据
	 */
	public enum MyDataType {
		ALL, SEV1, SEV2, SEV3, SEV4, Remedy, Bluemix, IoT, Cloudant, DashDB, SSO, Blockchain, MessageHub
	}

	int allTickets;
	int customerTickets;
	int resolvedTickets;
	String reopenTickets = "请人工统计";
	String outOfSlaTickets;
	String issueTickets = "请人工统计";
	String avgIntilaResponseTime;
	String avgResolvedTime;
	String maxResolvedTime;
	int ticketCreatedByL1;
	String ticketResovledByL1 = "请人工统计";
	String ticketTranferredByL1 = "由于部分L2不使用Remedy，请人工统计";
	String avgCustomerSatisfaction;

	List<BySeverity> reportDataBySeverity = new ArrayList<>();
	List<ByService> reportDataByService = new ArrayList<>();
	// ------------

	public BySeverity getBySeverityInstance() {
		return new BySeverity();
	}

	public class BySeverity {
		MyDataType severity;
		int num;
		String avgIntilaResponseTime;

		@Override
		public String toString() {
			return format(severity, num, avgIntilaResponseTime);
		}
	}

	public ByService getByServiceInstance() {
		return new ByService();
	}

	public class ByService {
		MyDataType service;
		int num;
		int resolvedNum;
		int openNum;
		String avgIntilaResponseTime;
		String avgResolvedTime;

		@Override
		public String toString() {
			return format(service, num, resolvedNum, openNum, avgIntilaResponseTime, avgResolvedTime);
		}
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		// 2
		stringBuffer.append(format("Total", "Data"));
		stringBuffer.append(format("All Tickets", allTickets));
		stringBuffer.append(format("Customer Tickets", customerTickets));
		stringBuffer.append(format("Resolved Tickets", resolvedTickets));
		stringBuffer.append(format("Reopen Tickets", reopenTickets));
		stringBuffer.append(format("Out of SLA", outOfSlaTickets));
		stringBuffer.append(format("Issue Tickets", issueTickets));
		stringBuffer.append(format("Avg Response", avgIntilaResponseTime));
		stringBuffer.append(format("Avg Resolve", avgResolvedTime));
		stringBuffer.append(format("Maximum Resolve", maxResolvedTime));
		stringBuffer.append(format("Created By L1", ticketCreatedByL1));
		stringBuffer.append(format("Resolved By L1", ticketResovledByL1));
		stringBuffer.append(format("Tranferred by L1", ticketTranferredByL1));
		// （数量+平均分）
		stringBuffer.append(format("Avg Satisfaction", avgCustomerSatisfaction));
		stringBuffer.append("---------------------------------------------------\r\n");

		// 3
		stringBuffer.append(format("SEV", "Num", "avgResponse"));
		for (BySeverity temp : reportDataBySeverity) {
			stringBuffer.append(temp.toString());
		}
		stringBuffer.append("---------------------------------------------------\r\n");
		// 6
		stringBuffer.append(format("Service", "Num", "solved", "unsolved", "avgResponse", "avgSolved"));
		for (ByService temp : reportDataByService) {
			stringBuffer.append(temp.toString());
		}

		return stringBuffer.toString();
	}

	private String format(Object... data) {
		if (data == null)
			return null;
		String str = null;
		switch (data.length) {
		case 2:
			str = String.format("%16s: %-14s\r\n", data);
			break;
		case 3:
			str = String.format("%16s: %-3s %-11s\r\n", data);
			break;
		case 6:
			str = String.format("%16s: %-3s %-6s %-8s %-11s %-14s\r\n", data);
			break;
		default:
			System.out.println("--- data append err ---");
			break;
		}
		return str;
	}

}
