


import java.rmi.RemoteException;
import java.util.Random;

import controle.Console;
import element.JeTeVois;
import element.Personnage;
import element.Sniper;

/**
 * Test de la Console avec un Element qui s'ajoute a l'Arene (apres lancement Arene et IHM). A lancer en plusieurs exemplaires.
 */
public class TestPersonnageCentre {

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
			int force = r.nextInt(40)+50;
			System.out.println("Force " + force);
			int vie = r.nextInt(100-force);
			System.out.println("HP "+vie);
			int charisme = r.nextInt(100-force-vie);
			System.out.println("Charisme "+charisme);
			int defense = r.nextInt(100-force-vie-charisme)/60;
			System.out.println("Defense "+defense);
			int vitesse = 1;
			Personnage bidule = new Sniper("Perso", 58+r.nextInt(4),0,20,1,10);
			
			new Console(bidule, 40+r.nextInt(30), 40+r.nextInt(30), port, ipArene);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}