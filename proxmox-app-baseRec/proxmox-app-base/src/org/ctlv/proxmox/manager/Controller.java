package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Controller {

	ProxmoxAPI api;
	List<String> ctIDList= new ArrayList<String>();
	
	public Controller(ProxmoxAPI api){
		this.api = api;
	}
	
	// migrer un conteneur du serveur "srcServer" vers le serveur "dstServer"
	public void migrateFromTo(String srcServer, String dstServer) throws LoginException, JSONException, IOException  {
		ctIDList=api.getCTList(srcServer);
		String ctID = ctIDList.get(ctIDList.size()-1);
		api.migrateCT(srcServer, ctID, dstServer);
		ctIDList.remove(ctIDList.size()-1);
	}

	// arrêter le plus vieux conteneur sur le serveur "server"
	public void offLoad(String server) throws LoginException, JSONException, IOException {
		ctIDList=api.getCTList(server);
		ListIterator<String> it = ctIDList.listIterator();
			String ctID = ctIDList.get(0);
			List<String> ListCTStopped= new ArrayList<String>();
			while(ListCTStopped.contains(ctID) && it.hasNext()) {
				String t = it.next();
			}
			api.stopCT(server, ctID);
			ListCTStopped.add(ctID);
			
			

			
		
		
		
	}

}
