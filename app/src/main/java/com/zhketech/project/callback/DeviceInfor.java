package com.zhketech.project.callback;

import java.io.Serializable;
import java.util.UUID;

/**
 * 设备 信息的实体类
 */
public class DeviceInfor implements Serializable {
	public UUID uuid;
	public String serviceURL;
	private int id;
	private String name;
	private String ipAddr;
	private boolean isOnline = false;
	private String rtspUri = "";
	public int width;
	public int height;
	public int rate;
	public String username;
	public String password;

	public DeviceInfor(UUID uuid, String serviceURL) {
		this.uuid = uuid;
		this.serviceURL = serviceURL;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public String getRtspUri() {
		return rtspUri;
	}

	public void setRtspUri(String rtspUri) {
		this.rtspUri = rtspUri;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "CameraDevice [uuid=" + uuid + ", serviceURL=" + serviceURL
				+ ", id=" + id + ", name=" + name + ", ipAddr=" + ipAddr
				+ ", isOnline=" + isOnline + ", rtspUri=" + rtspUri
				+ ", width=" + width + ", height=" + height + ", rate=" + rate
				+ ", username=" + username + ", password=" + password + "]";
	}

	public DeviceInfor() {
		super();
	}

}