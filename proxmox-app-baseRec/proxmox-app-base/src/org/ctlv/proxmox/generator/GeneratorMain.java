package org.ctlv.proxmox.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.ctlv.proxmox.api.data.Node;
import org.json.JSONException;

public class GeneratorMain {
	
	static Random rndTime = new Random(new Date().getTime());
	public static int getNextEventPeriodic(int period) {
		return period;
	}
	public static int getNextEventUniform(int max) {
		return rndTime.nextInt(max);
	}
	public static int getNextEventExponential(int inv_lambda) {
		float next = (float) (- Math.log(rndTime.nextFloat()) * inv_lambda);
		return (int)next;
	}
	
	public static void main(String[] args) throws InterruptedException, LoginException, JSONException, IOException {
		
	
		long baseID = Constants.CT_BASE_ID;
		int lambda = 30;
		int i=8,j=12408;
		
		
		Map<String, List<LXC>> myCTsPerServer = new HashMap<String, List<LXC>>();

		ProxmoxAPI api = new ProxmoxAPI();
		Random rndServer = new Random(new Date().getTime());
		Random rndRAM = new Random(new Date().getTime()); 
		
		long memAllowedOnServer1 = (long) (api.getNode(Constants.SERVER1).getMemory_total() * Constants.MAX_THRESHOLD);

		long memAllowedOnServer2 = (long) (api.getNode(Constants.SERVER2).getMemory_total() * Constants.MAX_THRESHOLD);
		while (i<100 && j<=12499) {
		 // la quantité de RAM utilisée par mes CTs sur chaque serveur
		
			long memOnServer1 =api.getNode(Constants.SERVER1).getMemory_used();
			long memOnServer2 =api.getNode(Constants.SERVER2).getMemory_used();

		/* Mémoire autorisée sur chaque serveur. On a droit à 16% des ressources sur chaque serveur(memAllowedOnServer1) 
		 * dont 66% sur le serveur1 et 33% sur le serveur2 */
			
		    float memRatioOnServer1 = memAllowedOnServer1/Constants.CT_CREATION_RATIO_ON_SERVER1;

			float memRatioOnServer2 = memAllowedOnServer2/Constants.CT_CREATION_RATIO_ON_SERVER2;

			if (memOnServer1 < memRatioOnServer1 && memOnServer2 < memRatioOnServer2) {  // Exemple de condition de l'arrét de la génération de CTs
				
				// choisir un serveur aléatoirement avec les ratios spécifiés 66% vs 33%
				String serverName;
				if (rndServer.nextFloat() < Constants.CT_CREATION_RATIO_ON_SERVER1)
					serverName = Constants.SERVER1;
				
				else
					serverName = Constants.SERVER2;
				
				// créer un contenaire sur ce serveur
			
				// ID varie entre 12400 et 12499( groupe A2)
				String CtID=String.valueOf(j); 
				// le nombre de conteneurs que nous avons crées
				String baseName="ct-tpgei-ctlv-bA24-ct"+i; 
				
				// on a choisi 256 comme valeur de RAM pour ne pas surcharger le serveur
				
				api.createCT(serverName,CtID, baseName, Constants.RAM_SIZE[0]);
				i++;
				j++;
								
				// planifier la prochaine création
				int timeToWait = getNextEventExponential(lambda); // par exemple une loi expo d'une moyenne de 30sec
				
				// attendre jusqu'au prochain ï¿½vï¿½nement
				Thread.sleep(1000 * timeToWait);
				//api.startCT(serverName,CtID);

			}
			else {
				System.out.println("Servers are loaded, waiting ...");
				Thread.sleep(Constants.GENERATION_WAIT_TIME* 1000);
			}
		}
		
	}

}
