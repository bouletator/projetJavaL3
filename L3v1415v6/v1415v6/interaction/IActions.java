package interaction;

import java.rmi.RemoteException;

import serveur.IArene;

/**
 * Interface contenant les methodes (les actions) passant par le reseau. 
 *
 */
public interface IActions {
	

    
    /**
	 * Appele par l'element. Permet a l'element ref1 (Personnage) de ramasser l'element ref2 (Potion), 
	 * qui modifie les caracteristiques du personnage.
	 * @param ref1 personnage ramassant la potion
	 * @param ref2 potion ramasse
	 * @param arene arene
	 */
	public void ramasser(int ref, int ref2, IArene arene) throws RemoteException;
    
    //Maintenue dans Console public void perdreVie(int viePerdue) throws RemoteException;
    
    //public VueElement update() throws RemoteException;

   // public void shutDown(String cause) throws RemoteException;
    
	//public void ajouterConnu(int ref) throws RemoteException;
	
	/**
	 * Appele par le run de la console. Permet a l'attaquant (ref1) d'attaquer (executer une frappe) le defenseur (ref2).
	 * Les regles de combat s'appliquent (en fonction de la force, de la defense et de l'esquive).
	 * Les caracteristiques du defenseur sont mises a jour (pas d'impact sur attaquant)
	 * Les deux protagonistes ajoutent leur adversaire dans les elements deja vus. 
	 * @param ref1 attaquant
	 * @param ref2 defenseur
	 * @param arene arene
	 */
	public void interaction(int ref1, int ref2, IArene arene) throws RemoteException ;

}
