package element;

import interfaceGraphique.VueElement;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Hashtable;

/**
 * Un element du jeu: un personnage ou une potion, avec un nom, une liste d'autres elements
 * qu'il connait et ses caracteristiques (au moins le nombre de vies).
 */
public class Element implements IElement, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Nom de l'element.
	 */
	protected String nom;
	
	/**
	 * Caracteristiques de l'element (au moins vie).
	 */
	protected Hashtable<String, Integer> caract = new Hashtable<String,Integer>();
	
	/**
	 * Constructeur. Le nombre de vie est par defaut initialise a 1.
	 * @param nom le nom de l'element a creer
	 */
	public Element(String nom) {		
		this.nom = nom;
		caract.put("vie", 1);
	}

	public String getNom() {
		return this.nom;
	}

	public int getVie() {
		return caract.get("vie");
	}
	
	public void setVie(int vie) {
		caract.put("vie", vie);
	}

	/**
	 * Retourne la valeur associee a la caracteristique specifiee.
	 * @param c nom de la caracterisique
	 * @return valeur correspondant a la caracteristique, ou null si elle n'existe pas
	 */
	public Integer getCaract(String c) {
		return caract.get(c);
	}	

	/** 
	 * Retourne toute la table des caracteristiques.
	 * @return the caract
	 */
	public Hashtable<String, Integer> getCaract() {
		return caract;
	}

	/**
	 * Ajoute la caracteristique specifiee avec la valeur specifiee. Si la 
	 * caracteristique existe deja, la valeur sera ecrasee.
	 * @param c caracteristique
	 * @param val valeur
	 */
	public void ajouterCaract(String c, int val) {
		caract.put(c, val);
	}
	
	public void strategie(VueElement ve, Hashtable<Integer,VueElement> voisins, Integer refRMI) throws RemoteException {
		// Un element basique ne fait "rien", comme une potion
	}
	
	public void parler(String s, VueElement ve) throws RemoteException {
		ve.setPhrase(s);	
	}

	@Override
	public String toString(){
		return nom;
	}
	/**
	 * Affecte la valeur de charisme
	 * @param charisme
	 */
	public void setCharisme(int charisme) {
	ajouterCaract("charisme", charisme);
	}
	
}
