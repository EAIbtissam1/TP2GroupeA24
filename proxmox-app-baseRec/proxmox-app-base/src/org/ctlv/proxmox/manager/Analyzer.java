package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Analyzer {
	ProxmoxAPI api;
	Controller controller;
	
	public Analyzer(ProxmoxAPI api, Controller controller) {
		this.api = api;
		this.controller = controller;
	}
	public void analyze(Map<String, List<LXC>> myCTsPerServer) throws LoginException, JSONException, IOException  {
	
		  
		long memAllowedOnServer1 = (long) (api.getNode(Constants.SERVER1).getMemory_total() * Constants.MAX_THRESHOLD);
		long memAllowedOnServer2 = (long) (api.getNode(Constants.SERVER2).getMemory_total() * Constants.MAX_THRESHOLD);
		
		//la quantité de RAM utilisée par mes CTs sur chaque serveur
		
		long memOnServer1 =api.getNode(Constants.SERVER1).getMemory_used();		
		long memOnServer2 =api.getNode(Constants.SERVER2).getMemory_used();
		
		
		/* la mémoire de serveur1 dépasse 8% de la mémoire autorisée du serveur1(memAllowedOnServer1/2)
		//et la mémoire de serveur2 est inférieur à 8% de la mémoire autorisée du serveur2(memAllowedOnServer2/2)
		 dans ce cas on va migrer les derniers conteneurs crées un par un jusqu'à l'équilibre  */
		
		while( memOnServer1>memAllowedOnServer1/2 && memOnServer2<memAllowedOnServer2/2) {
			controller.migrateFromTo(Constants.SERVER1,Constants.SERVER2);
		}
		//dans le cas contraire les conteneurs sont migrés du serveur2 vers serveur1 
		
		while(memOnServer1<memAllowedOnServer1/2 && memOnServer2>memAllowedOnServer2/2)	{
		    controller.migrateFromTo(Constants.SERVER2,Constants.SERVER1);

		}
		/*Dans le cas ou l'un des serveurs dépasse 12% de la charge totale( memAllowedOnServer*(3/8))et l'autre en est inférieure 
		 on fait un équilibrage avant de procéder à l'arrét des conteneurs les plus anciens*/
		while(memOnServer1>memAllowedOnServer1*(3/8) && memOnServer2<memAllowedOnServer2*(3/8)) {
			controller.migrateFromTo(Constants.SERVER1,Constants.SERVER2);		
		}
		
		while(memOnServer1<memAllowedOnServer1*(3/8) && memOnServer2>memAllowedOnServer2*(3/8)) {
			controller.migrateFromTo(Constants.SERVER2,Constants.SERVER1);
			}
		
		//Arret des conteneurs les plus anciens pour supporter les nouveaux
		while(memOnServer1>memAllowedOnServer1*(3/8)) {
				controller.offLoad(Constants.SERVER1);
		}
				
		while(memOnServer2>memAllowedOnServer2*(3/8)) {
				controller.offLoad(Constants.SERVER2);
			}
		}
	

}
