package jhd;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import jhd.ReportData.MyDataType;

public class Main {

	public static void main(String[] args) throws Exception {
		ReportGenerator2 tester = new ReportGenerator2();
		List<RemedyTicket> tickets = Utils.getTicketList();

		/*
		 * Map<String, List<RemedyTicket>> tickets2 = new HashMap<String,
		 * List<RemedyTicket>>();
		 * 
		 * for (RemedyTicket remedyTicket : tickets) { String service =
		 * remedyTicket.getService(); List<RemedyTicket> list = tickets2.get(service);
		 * if (list == null) { list = new ArrayList<RemedyTicket>(); }
		 * list.add(remedyTicket); tickets2.put(remedyTicket.getService(), list); }
		 * 
		 * Set<String> keys = tickets2.keySet(); for (String service : keys) {
		 * List<RemedyTicket> list = tickets2.get(service); tester.getDataByType(list,
		 * service, null); }
		 */

		ReportData reportData = new ReportData();
		// tester.setDataByType2(tickets, MyDataType.ALL, null, reportData);
		Map<MyDataType, Predicate<RemedyTicket>> myData = new HashMap<MyDataType, Predicate<RemedyTicket>>();
		myData.put(MyDataType.ALL, ticket -> true);
		myData.put(MyDataType.SEV1, ticket -> ticket.getServity() == 1);
		myData.put(MyDataType.SEV2, ticket -> ticket.getServity() == 2);
		myData.put(MyDataType.SEV3, ticket -> ticket.getServity() == 3);
		myData.put(MyDataType.SEV4, ticket -> ticket.getServity() == 4);
		myData.put(MyDataType.Remedy, ticket -> ticket.getService().equals("Remedy"));
		myData.put(MyDataType.Bluemix, ticket -> ticket.getService().equals("Bluemix"));
		myData.put(MyDataType.IoT, ticket -> ticket.getService().equals("Internet-of-Things"));
		myData.put(MyDataType.Cloudant, ticket -> ticket.getService().equals("Cloudant-NoSQL-DB"));
		myData.put(MyDataType.DashDB, ticket -> ticket.getService().equals("DashDB"));
		myData.put(MyDataType.SSO, ticket -> ticket.getService().equals("SSO"));
		myData.put(MyDataType.Blockchain, ticket -> ticket.getService().equals("BlockChain"));
		myData.put(MyDataType.MessageHub, ticket -> ticket.getService().equals("MessageHub"));

		for (MyDataType temp : MyDataType.values()) {
			tester.setDataByType(tickets, temp, myData.get(temp), reportData);
		}

		System.out.println(LocalDateTime.now());
		System.out.println(reportData.toString());
	}

}
