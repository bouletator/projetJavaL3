/**
 * 
 */
package element;


import interaction.Actions;
import interaction.Deplacements;
import interfaceGraphique.VueElement;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;

import utilitaires.Calculs;

/**
 * Un personnage: un element possedant des caracteristiques et etant capable
 * de jouer une strategie. 
 */
public class Personnage extends Element implements IPersonnage {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Reference du leader de ce personnage, -1 si aucun.
	 */
	private int leader;
	
	/**
	 * Reference des personnages de l'equipe de ce personnage. 
	 * Vide si le leader n'est pas egal a -1.
	 */
	private ArrayList<Integer> equipe;

	/**
	 * Constructeur d'un personnage avec un nom et une quantite de force et de charisme.
	 * Au depart, le personnage n'a ni leader ni equipe.
	 * @param nom
	 * @param force
	 * @param charisme
	 */
	public Personnage(String nom, int force, int charisme) {
		super(nom);
		ajouterCaract("force", force);
		ajouterCaract("charisme", charisme);

		leader = -1;
		equipe = new ArrayList<Integer>();
	}
	
	/**
	 * Retourne la valeur de force.
	 * @return bonus de force
	 */
	public int getForce() {
		return getCaract("force");
	}
	
	/**
	 * Retourne la valeur de charisme.
	 * @return bonus de charisme
	 */
	public int getCharisme() {
		return getCaract("charisme");
	}

	/**
	 * Retourne le leader.
	 * @return leader (-1 si aucun)
	 */
	public int getLeader() {
		return leader;
	}

	/**
	 * Retourne la liste des personnages de l'equipe.
	 * @return equipe
	 */
	public ArrayList<Integer> getEquipe() {
		return equipe;
	}
	

	@Override
	public String toString(){
		String lead = (leader != -1)? ", leader: " + leader: "";
		String eq = "";
		
		if(!equipe.isEmpty()) {
			eq += ", equipe: ";
			
			for(int i = 0; i < equipe.size(); i++) {
				eq += equipe.get(i);
				
				if(i < equipe.size() - 1) {
					eq += " ";
				}
			}
		}
		
		return super.toString() + "[" + getForce() + ", " + getCharisme() + lead + eq + "]";
	}
	
	
	
	
	@Override
	public void setLeader(int ref) throws RemoteException {
		leader = ref;
	}
	
	@Override
	public void clearLeader() throws RemoteException {
		leader = -1;
	}
	
	@Override
	public void ajouterEquipe(int ref) throws RemoteException {
		equipe.add((Integer) ref);
	}

	@Override
	public void enleverEquipe(int ref) throws RemoteException {
		equipe.remove((Integer) ref);
	}

	@Override
	public void enleverTouteEquipe() throws RemoteException {
		equipe.clear();
	}
	
	

	
	
	
	
	
	
	
	
	
	
	/**
	 * Met en place la strategie. On ne peut utiliser que les methodes de la 
	 * classe Actions.
	 * @param ve vue de l'element
	 * @param voisins element voisins de cet element
	 * @param refRMI reference attribuee a cet element par le serveur
	 * @throws RemoteException
	 */
	public void strategie(VueElement ve, Hashtable<Integer,VueElement> voisins, Integer refRMI) throws RemoteException {
        Actions actions = new Actions(ve, voisins); //je recupere les voisins (distance < 10)
        Deplacements deplacements = new Deplacements(ve,voisins);
        if (0 == voisins.size()) { // je n'ai pas de voisins, j'erre
        	parler("J'erre...", ve);
        	deplacements.seDirigerVers(0); //errer
            
        } else {
			VueElement cible = Calculs.chercherElementProche(ve, voisins);
			
			int distPlusProche = Calculs.distanceChebyshev(ve.getPoint(), cible.getPoint());
			int refPlusProche = cible.getRef();
			
			if((leader == -1 || leader != ((Personnage) cible.getControleur().getElement()).getLeader()) && // pas dans la meme equipe
					leader != refPlusProche && // un leader de l'autre
					((Personnage) cible.getControleur().getElement()).getLeader() != refRMI) { 

	        	System.out.println("Je m'occupe de l'element le plus proche");
				
				// le plus proche est a proximite
				if (distPlusProche <= 2) { 
					if(cible.getControleur().getElement() instanceof Potion) { // si la cible est une potion
						//ramassage
						parler("Je tente de ramasser une potion", ve);
						actions.ramasser(refRMI, refPlusProche, ve.getControleur().getArene());
						
					} else if(cible.getControleur().getElement() instanceof Personnage) { // si la cible est un personnage
						//jeu
						parler("Je joue avec " + refPlusProche, ve);
						actions.interaction(refRMI, refPlusProche, ve.getControleur().getArene());
					}
					
				} else { // je vais vers le plus proche
		        	parler("Je vais vers mon voisin " + refPlusProche, ve);
		        	deplacements.seDirigerVers(refPlusProche);
				}
			} else {
	        	parler("J'erre...", ve);
	        	deplacements.seDirigerVers(0); //errer
			}
        }
	}
}
