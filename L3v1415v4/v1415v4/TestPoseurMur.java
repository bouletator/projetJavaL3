import controle.Console;
import element.Mur;
import element.PoseurMur;

import java.rmi.RemoteException;
import java.util.Random;

/**
 * Created by clement on 06/01/2015.
 */
public class TestPoseurMur {

	/**
	 * @param args arguments from call
	 * @throws java.rmi.RemoteException
	 */
	public static void main(String[] args) {

		try {
			int port=5099;	//par defaut, port de l'arene=5099

			if (args.length!=0) port=Integer.parseInt(args[0]);

			String ipArene = "localhost";
			if (args.length!=0) if (args[1].contentEquals("")) ipArene=args[1];

			PoseurMur bidule = new PoseurMur("PoseurDeMur", 90, 0,20,90,1,10,1);
			bidule.ajouterMur(new Mur("invincible"));
			bidule.ajouterMur(new Mur("rempart de Troyes"));
			Random r = new Random();
			new Console(bidule, r.nextInt(100), r.nextInt(100), port, ipArene);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
