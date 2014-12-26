package serveur;

import interfaceGraphique.VueElement;

import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;

import controle.IConsole;


/**
 * Definit les methodes qui pourront s'appliquer a travers l'arene en RMI (toutes celles qui levent RemoteException)
 */
public interface IArene extends Remote {
	
	/**
	 * Renvoie une reference pour la console en train de se connecter
	 * @throws RemoteException
	 */
	public int allocateRef() throws RemoteException;
	
	/**
	 * Connexion d'un element au serveur
	 * @param ve la vue de l'element a se connecter
	 * @throws RemoteException
	 */
	public void connect(VueElement ve, String ipConsole) throws RemoteException;
	
	/**
	 * Calcule la liste de toutes les representations d'elements presentes dans l'arene. 
	 * @return la liste des representations
	 * @throws RemoteException
	 */
	public ArrayList<VueElement> getWorld() throws RemoteException;
	
	/**
	 * Renvoie la console associee a la reference specifiee.
	 * @param ref ref a trouver
	 * @return console
	 */
	public IConsole consoleFromRef(int ref) throws RemoteException;
	
	
	/**
	 * Liste les voisins (representations d'element) d'une coordonnee dans l'arene sous la forme de couples (reference,coordonnees) dans une Hashtable
	 * @throws RemoteException
	 */
	public Hashtable<Integer, VueElement> voisins(Point pos, int ref) throws RemoteException;

	public int getPort() throws RemoteException;
	
	public Hashtable<Integer, String> getIpAddrConsoles() throws RemoteException;
	
}

