package com.zhketech.project.bean;

public class AlarmBen {

	private String sender;//报警者ip
	private VideoBen videoBen;//被报警的实体类对象
	private String alertType; //报警类型
	private String reserved; //保留字段
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public VideoBen getVideoBen() {
		return videoBen;
	}
	public void setVideoBen(VideoBen videoBen) {
		this.videoBen = videoBen;
	}
	public String getAlertType() {
		return alertType;
	}
	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}
	public String getReserved() {
		return reserved;
	}
	public void setReserved(String reserved) {
		this.reserved = reserved;
	}
	public AlarmBen(String sender, VideoBen videoBen, String alertType, String reserved) {
		super();
		this.sender = sender;
		this.videoBen = videoBen;
		this.alertType = alertType;
		this.reserved = reserved;
	}
	public AlarmBen() {
		super();
	}
	@Override
	public String toString() {
		return "AlarmBen [sender=" + sender + ", videoBen=" + videoBen + ", alertType=" + alertType + ", reserved="
				+ reserved + "]";
	}
}
