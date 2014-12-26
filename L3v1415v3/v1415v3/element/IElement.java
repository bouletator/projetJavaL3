package element;

import interfaceGraphique.VueElement;

import java.rmi.RemoteException;
import java.util.Hashtable;

/**
 * Interface correspondant a un element du jeu (potion ou personnage), donne
 * toutes les methodes qui passeront par RMI. 
 *
 */
public interface IElement {
	
	/**
	 * Retourne le nom de l'element.
	 */
	public String getNom();

	/**
	 * Retourne le nombre de vies de l'element.
	 */
	public int getVie();
	
	/**
	 * Reinitialise le nombre de vies de l'element.
	 * @param vie le nouveau nombre de vie
	 */
	public void setVie(int vie);
		
	/**
	 * Renvoie les informations concernant l'element courant.
	 * @return chaine de caractere contenant au moins le nom de l'element et le 
	 * nombre de vies tel qu'il sera affiche sur l'interface graphique
	 */
	public String toString();
	
	/**
	 * Strategie appliquee par cet element
	 * @param ve vue de l'element
	 * @param voisins element voisins de cet element
	 * @param refRMI reference attribuee a cet element par le serveur
	 * @throws RemoteException
	 */
	public void strategie(VueElement ve, Hashtable<Integer,VueElement> voisins, Integer refRMI) throws RemoteException;
	
	/**
	 * Ajouter un message dit par l'element et vu sur l'arene. 
	 * @param s message
	 * @param ve vue de l'element
	 * @throws RemoteException
	 */
	public void parler(String s, VueElement ve) throws RemoteException;
	
}
