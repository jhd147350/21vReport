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
	 * all表示前面所有数据
	 * severity表示reportDataBySeverity的数据
	 * service表示reportDataByService的数据
	 */
	public enum MyDataType {  
		  ALL, SEVERITY, SERVICE  
		} 
	int allTickets;
	int customerTickets;
	int resolvedTickets;
	int reopenTickets;
	int outOfSlaTickets;
	int issueTickets;
	String avgIntilaResponseTime;
	String avgResolvedTime;
	String maxResolvedTime;
	int ticketCreatedByL1;
	int ticketResovledByL1;
	int ticketTranferredByL1;
	String avgCustomerSatisfaction;

	List<BySeverity> reportDataBySeverity = new ArrayList<>();
	List<ByService> reportDataByService = new ArrayList<>();
	// --------
	/*
	 * int numOfSev1; int numOfSev2; int numOfSev3; int numOfSev4;
	 * 
	 * String avgIntilaResponseTimeOfSev1; String avgIntilaResponseTimeOfSev2;
	 * String avgIntilaResponseTimeOfSev3; String avgIntilaResponseTimeOfSev4;
	 */

	// ------------

	public class BySeverity {
		int severity;
		int num;
		String avgIntilaResponseTime;

		@Override
		public String toString() {
			return format(severity, num, avgIntilaResponseTime);
		}
	}

	public class ByService {
		String service;
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
		stringBuffer.append("----------------------------------------------\n");

		// 3
		stringBuffer.append(format("SEV", "Num", "avgResponse"));
		for (BySeverity temp : reportDataBySeverity) {
			stringBuffer.append(temp.toString());
		}
		// stringBuffer.append(reportDataBySeverity.toString());
		/*
		 * stringBuffer.append(format("Sev1", "2", "55541545"));
		 * stringBuffer.append(format("Sev2", "2", "55541545"));
		 * stringBuffer.append(format("Sev3", "2", "55541545"));
		 * stringBuffer.append(format("Sev4", "2", "55541545"));
		 */
		stringBuffer.append("----------------------------------------------\n");
		// 6
		stringBuffer.append(format("Service", "Num", "solved", "unsolved", "avgResponse", "avgSolved"));
		for (ByService temp : reportDataByService) {
			stringBuffer.append(temp.toString());
		}

		/*
		 * stringBuffer.append(format("Remedy", 2, 2, 2, "fsdfs", "dfgdg"));
		 * stringBuffer.append(format("Bluemix", 2, 2, 2, "fsdfs", "dfgdg"));
		 * stringBuffer.append(format("IoT", 2, 2, 2, "fsdfs", "dfgdg"));
		 * stringBuffer.append(format("Cloudant", 2, 2, 2, "fsdfs", "dfgdg"));
		 * stringBuffer.append(format("Blueworkslive", 2, 2, 2, "fsdfs",
		 * "dfgdg")); stringBuffer.append(format("DashDB", 2, 2, 2, "fsdfs",
		 * "dfgdg")); stringBuffer.append(format("IDaaS/SSO", 2, 2, 2, "fsdfs",
		 * "dfgdg")); stringBuffer.append(format("MessageHub", 2, 2, 2, "fsdfs",
		 * "dfgdg"));
		 */
		return stringBuffer.toString();
	}

	private String format(Object... data) {
		if (data == null)
			return null;
		String str = null;
		switch (data.length) {
		case 2:
			str = String.format("%16s: %-14s\n", data);
			break;
		case 3:
			str = String.format("%16s: %-3s %-11s\n", data);
			break;
		case 6:
			str = String.format("%16s: %-3s %-6s %-8s %-11s %-14s\n", data);
			break;
		default:
			System.err.println("err -----------");
			break;
		}
		return str;
	}

}
