

import java.rmi.RemoteException;
import java.util.Random;

import controle.Console;
import element.JeTeVois;
import element.Personnage;

/**
 * Test de la Console avec un Element qui s'ajoute a l'Arene (apres lancement Arene et IHM). A lancer en plusieurs exemplaires.
 */
public class TestPersonnageAlea {

	public static void main(String[] args) {
		
		try {
			int port = 5099; // par defaut, 5099
			if (args.length > 0) {
				port = Integer.parseInt(args[0]);
			}
			
			String ipArene = "localhost"; // par défaut, localhost
			if (args.length > 1) { 
				ipArene = args[1];
			}

			Random r = new Random(System.currentTimeMillis());

			Personnage iSeeYou = new Personnage("ISeeYou", 99,0,1,1,0);
			new Console(iSeeYou, r.nextInt(100), r.nextInt(100), port, ipArene);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}