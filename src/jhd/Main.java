package jhd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

	
	 public static void main(String[] args) throws Exception {
	        ReportGenerator2 tester = new ReportGenerator2();
	        List<RemedyTicket> tickets = tester.getTicketList("data.txt");
	        tester.getAvgTime(tickets, "总体数据", null);
	        tester.getSatisfaction(tickets, "总体数据", null);
	        System.out.println("L1转出的Ticket数量:" + tickets.stream().filter(ticket -> isNotEmpty(ticket.getRtcLink())).count());
	 
	        System.out.println("L2转出L3的Ticket数量:" + tickets.stream().filter(ticket -> ticket.getSubmitterLevel() == 2 && isNotEmpty(ticket.getRtcLink())).count());
	 
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
	 
}
