package jhd;

import java.util.Date;

/**
 * 
 * @author jhd147350 从Remedy中导出的数据项
 */
public class RemedyTicket {
	// 提交者组
	// 受派组*+
	// 目标日期
	// SLM 实时状态
	// 关闭日期
	// SLA 保持
	// 报告来源*
	// 报告来源
	// 提交人*
	// 以上导出数据，均未加入

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
	// @Label("报告日期+")
	// private Date reportTime;
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
	@Label("Uni_Submitter_Group_Level") // 当该值为1时表示这是由L1创建的工单
	private int SubmitterLevel;
	@Label("工单优先级")
	private Integer ticketPriority;

	public Integer getTicketPriority() {
		return ticketPriority;
	}

	public void setTicketPriority(Integer ticketPriority) {
		this.ticketPriority = ticketPriority;
	}

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

	/*
	 * public Date getReportTime() { return reportTime; }
	 * 
	 * public void setReportTime(Date reportTime) { this.reportTime = reportTime; }
	 */

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
				+ initTime + ", reportTime=" + /* reportTime + */", resloveTime=" + resloveTime + ", submitTime="
				+ submitTime + ", service=" + service + "]";
	}

}
