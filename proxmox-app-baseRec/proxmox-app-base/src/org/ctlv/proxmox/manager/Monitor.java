package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Monitor implements Runnable {

	Analyzer analyzer;
	ProxmoxAPI api;
	
	public Monitor(ProxmoxAPI api, Analyzer analyzer) {
		this.api = api;
		this.analyzer = analyzer;
	}

	@Override
	public void run() {
		
		while(true) {
			
			// Récupérer les données sur les serveurs
			List<String> nodes = new ArrayList<String>();
			List<LXC> containers= new ArrayList<LXC>();
			Map<String, List<LXC>> myCTsPerServer = new HashMap<String, List<LXC>>();
			
			try {
				nodes=api.getNodes();
			} catch (LoginException | JSONException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ListIterator<String> it = nodes.listIterator();
			
			while(it.hasNext()) {
			String serveur = it.next();
			try {
				containers=api.getCTs(serveur);
			} catch (LoginException | JSONException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			myCTsPerServer.put(serveur, containers);


			
			// Lancer l'analyse
			try {
				analyzer.analyze(myCTsPerServer);
			} catch (LoginException | JSONException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			
			// attendre une certaine période
			try {
				Thread.sleep(Constants.MONITOR_PERIOD * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	}
}
