package org.ctlv.proxmox.manager;

import java.io.IOException;

import org.ctlv.proxmox.api.ProxmoxAPI;

public class ManagerMain {

 static Monitor monitor;
	public static void main(String[] args) throws  IOException {
		
		monitor.run();
	}

}