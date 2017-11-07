package org.ctlv.proxmox.generator;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.ProxmoxAPI;
import org.json.JSONException;

public class Main {

	public static void main(String[] args) throws LoginException, JSONException, IOException {
		// TODO Auto-generated method stub
		ProxmoxAPI api = new ProxmoxAPI();
		api.startCT("srv-px5","12407");

	}

}
