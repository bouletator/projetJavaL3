package element;

import interaction.Actions;
import interaction.Deplacements;
import interfaceGraphique.VueElement;
import utilitaires.Calculs;

import java.rmi.RemoteException;
import java.util.Hashtable;

/**
 * Created by clement on 05/01/2015.
 */
public class Mage extends PoseurObjet {

	private boolean potionNonMiseDernierTour = true;

	/**
	 * Constructeur d'un personnage avec un nom et une quantite de force et de charisme.
	 * Au depart, le personnage n'a ni leader ni equipe.
	 *
	 * @param nom           Nom du personnage
	 * @param force         Quantité de Force du personnage
	 * @param charisme      Quantité de Charisme du personnage
	 * @param defense       Quantité de Défense du personnage
	 * @param determination Quantité de Détermination du personnage
	 * @param vitesse       Quantité de vitesse du personnage
	 * @param vision        Portée de la vision du personnage
	 * @param nbObjetMAx
	 */
	public Mage(String nom, int force, int charisme, int defense, int determination, int vitesse, int vision, int nbObjetMAx) {
		super(nom, force, charisme, defense, determination, vitesse, vision, nbObjetMAx);
	}

	public void strategie(VueElement ve, Hashtable<Integer,VueElement> voisins, Integer refRMI) throws RemoteException {
		Actions actions = new Actions(ve, voisins); //je recupere les voisins (distance < 10)
		Deplacements deplacements = new Deplacements(ve,voisins);

		if (0 == voisins.size()) { // je n'ai pas de voisins, j'erre
			parler("J'erre...", ve);
			deplacements.seDirigerVers(0); //errer
			return;
		}
		VueElement cible = Calculs.chercherElementProche(ve, voisins);

		int distPlusProche = Calculs.distanceChebyshev(ve.getPoint(), cible.getPoint());

		int refPlusProche = cible.getRef();
		Element elemPlusProche = cible.getControleur().getElement();

		// dans la meme equipe ?
		boolean memeEquipe = false;

		if(elemPlusProche instanceof Personnage) {
			memeEquipe = (this.getLeader() != -1 && this.getLeader() == ((Personnage) elemPlusProche).getLeader()) || // meme leader
					this.getLeader() == refPlusProche || // cible est le leader de this
					((Personnage) elemPlusProche).getLeader() == refRMI; // this est le leader de cible
		}

		if(distPlusProche <= 2) { // si suffisamment proches
			if(elemPlusProche instanceof Personnage && !memeEquipe) { // ennemi trop proche
				parler("Je fuis un adversaire",ve);
				actions.setInteractionType(Actions.FUIR);
				actions.interaction(ve.getRef(), refPlusProche, ve.getControleur().getArene());
				potionNonMiseDernierTour = true;
			}
		}
		else if(distPlusProche <=5 && potionNonMiseDernierTour)
		{
			//TODO : mettre en place une potion dans la direction de l'adversaire
			potionNonMiseDernierTour = false;
		}
		else if(distPlusProche <= 5 && !potionNonMiseDernierTour)
		{
			parler("J'ai mis une potion sur la route de "+refPlusProche,ve);
			actions.setInteractionType(Actions.FUIR);
			actions.interaction(ve.getRef(),refPlusProche,ve.getControleur().getArene());
		}
		else
		{ // si voisins, mais plus eloignes
			if(!memeEquipe && !(elemPlusProche instanceof Potion))
			{
				parler("Je vais vers mon voisin " + refPlusProche, ve);
				deplacements.seDirigerVers(refPlusProche);
				potionNonMiseDernierTour = true;
			}
			else
			{
				parler("J'erre...", ve);
				deplacements.seDirigerVers(0); // errer
				potionNonMiseDernierTour = true;
			}
		}
	}

	public void ajouterPotion(Potion p)
	{
		ajouterObjet(p);
	}
}
