package com.zhketech.project.bean;

import java.io.Serializable;

public class SipBean  implements Serializable {
	
	private String flage;
	private String id;
	private String ip;
	private String name;
	private int sentry;
	private String number;
	private VideoBen videoBen;
	public String getFlage() {
		return flage;
	}
	public void setFlage(String flage) {
		this.flage = flage;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSentry() {
		return sentry;
	}
	public void setSentry(int sentry) {
		this.sentry = sentry;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public VideoBen getVideoBen() {
		return videoBen;
	}
	public void setVideoBen(VideoBen videoBen) {
		this.videoBen = videoBen;
	}
	public SipBean(String flage, String id, String ip, String name, int sentry, String number, VideoBen videoBen) {
		super();
		this.flage = flage;
		this.id = id;
		this.ip = ip;
		this.name = name;
		this.sentry = sentry;
		this.number = number;
		this.videoBen = videoBen;
	}
	public SipBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "SipBean [flage=" + flage + ", id=" + id + ", ip=" + ip + ", name=" + name + ", sentry=" + sentry
				+ ", number=" + number + ", videoBen=" + videoBen + "]";
	}
	

}
