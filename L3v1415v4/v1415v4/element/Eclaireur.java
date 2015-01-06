package element;

import interaction.Actions;
import interaction.Deplacements;
import interfaceGraphique.VueElement;
import utilitaires.Calculs;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.Hashtable;

/**
 * Created by clement on 05/01/2015.
 */
public class Eclaireur	extends Personnage {

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
	 */
	public Eclaireur(String nom, int force, int charisme, int defense, int determination, int vitesse, int vision) {
		super(nom, force, charisme, defense, determination, vitesse, vision);
	}

	/***
	 * Stratégie de base d'un éclaireur
	 * @param ve vue de l'element
	 * @param voisins element voisins de cet element
	 * @param refRMI reference attribuee a cet element par le serveur
	 * @throws java.rmi.RemoteException
	 */

	@Override
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

		if(distPlusProche <= 2)
		{
			if(elemPlusProche instanceof Personnage)
			{
				Personnage p = (Personnage) elemPlusProche;
				if (!memeEquipe && (p.getForce() - this.getDefense()) < (this.getForce() - p.getDefense()))
				{
					parler("J'attaque plus faible que moi", ve);
					actions.setInteractionType(Actions.FRAPPER);
					actions.interaction(ve.getRef(),refPlusProche,ve.getControleur().getArene());
				}
			}
			return;
		}
		if(elemPlusProche instanceof Bombe)
		{
			parler("Je fuis une bombe !!!", ve);
			actions.setInteractionType(Actions.FUIR);
			actions.setDeplacements(deplacements);
			actions.interaction(ve.getRef(),refPlusProche,ve.getControleur().getArene());
		}
		else if(elemPlusProche instanceof Personnage)
		{
			Personnage p = (Personnage) elemPlusProche;
			if (!memeEquipe && (p.getForce() - this.getDefense()) < (this.getForce() - p.getDefense()))
			{
				parler("Je vais attaquer plus faible que moi", ve);
				deplacements.seDirigerVers(refPlusProche);
			}
			else if (!memeEquipe)
			{
				parler("Je fuis un adversaire trop fort...", ve);
				actions.setInteractionType(Actions.FUIR);
				actions.setDeplacements(deplacements);
				actions.interaction(ve.getRef(),refPlusProche,ve.getControleur().getArene());
			}
			else if(refPlusProche == ((Personnage) elemPlusProche).getLeader())
			{
				parler("J'aime mon Leader donc je vais le voir !", ve);
				deplacements.seDirigerVers(refPlusProche);
			}
			else
			{
				parler("Le coin en haut à gauche est cool !", ve);
				deplacements.seDirigerVers(new Point(0,0));
			}
		}
	}
}
