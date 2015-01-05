import controle.Console;
import element.Personnage;

import java.rmi.RemoteException;
import java.util.Random;

/**
 * Created by clement on 05/01/2015.
 */
public class TestGuerrier {

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

			Personnage bidule = new Personnage("Bidule", 200, 0);

			Random r = new Random();
			new Console(bidule, r.nextInt(100), r.nextInt(100), port, ipArene);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}