package jhd;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
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
 
public class ReportGenerator2 {
 
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
 
            //得到Remedyticket中的注解
            for (Field field : fields) {
                Label label = field.getAnnotation(Label.class);
                if (label != null) {
                    map.put(label.value(), field);
                }
            }
            boolean first = false;
            tickets = new ArrayList<RemedyTicket>();
 
            //一次读取一行数据
            while ((str = br.readLine()) != null) {
            	//根据 \t 进行分割数据
                String[] data = str.split("\\t");
                //第一行表头处理如下
                if (!first) {
                	//如果是第一行 就把第一行的数据存入fmap，让data中的第一行数据对应起来
                	//TODO 建议将first 设置为true 更加表示第一次
                    for (int i = 0; i < data.length; i++) {
                        String string = data[i];
                        Field field = map.get(string);
                        if (field != null) {
                            fmap.put(i, field);
                        }
                    }
                    first = true;
                }
                //所有数据行的处理如下
                else {
                    RemedyTicket ticket = new RemedyTicket();
                    for (int i = 0; i < data.length; i++) {
                        Field field = fmap.get(i);
                        if (field != null) {
                            String strdata = data[i];
                            Object obj = null;
                            Class<?> type = field.getType();
                            //TODO 按类型 处理数据，不知道用switch可以解决不
                            if (type == Date.class) {
                            	//处理 这种数据 2017-6-29 22:53:49
                                try {
                                    obj = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strdata);
                                } catch (Exception e) {
                                    try {
                                        obj = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(strdata);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                        obj = null;
                                    }
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
                                //向ticket 对象赋值
                                field.set(ticket, obj);
                            }
                        }
                    }
                    //将对象添加到数组中
                    tickets.add(ticket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	//关闭file和buffer reader
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
 
        System.out.println(prefix + "评分数量" + num + "," + totalSatisfactionId + "，满意度：" + (double) totalSatisfaction / num);
 
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