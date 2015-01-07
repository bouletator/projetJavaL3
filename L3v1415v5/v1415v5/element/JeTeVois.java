package element;

import interaction.Actions;
import interaction.Deplacements;
import interfaceGraphique.VueElement;
import serveur.IArene;
import utilitaires.Calculs;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.Hashtable;

/**
 * Created by clement on 07/01/2015.
 */
public class JeTeVois extends Personnage {

	private Hashtable <Integer, VueElement> listePersonnagesVus = new Hashtable<Integer, VueElement>();

	/**
	 * Constructeur d'un personnage avec un nom et une quantite de force et de charisme.
	 * Au depart, le personnage n'a ni leader ni equipe.
	 *
	 * @param nom      nom
	 * @param force    force
	 * @param charisme charisme
	 * @param hp       hp
	 * @param vitesse  vitesse
	 * @param defense  defense
	 */
	public JeTeVois(String nom, int force, int charisme, int hp, int vitesse, int defense) {
		super(nom, force, charisme, hp, vitesse, defense);
	}

	public void strategie(VueElement ve, Hashtable<Integer,VueElement> voisins, Integer refRMI) throws RemoteException
	{
		Actions actions = new Actions(ve, voisins); //je recupere les voisins (distance < 10)
		Deplacements deplacements = new Deplacements(ve,voisins);

		if (0 == voisins.size()) { // je n'ai pas de voisins, j'erre
			//TODO : si on a pas de voisin immédiat il ne faut pas faire n'importe quoi !
			parler("J'erre...", ve);
			deplacements.seDirigerVers(0); //errer

		} else {
			//On mémorise tous les personnages vus.
			this.listePersonnagesVus.putAll(voisins);

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
				if(elemPlusProche instanceof Potion) { // potion
					// ramassage

					if(isBonnePotion(elemPlusProche)) {
						parler("Je ramasse une potion", ve);
						actions.ramasser(refRMI, refPlusProche, ve.getControleur().getArene());
					}

				} else { // personnage
					Personnage adversaire = (Personnage) elemPlusProche;
					if(isDanger(adversaire))
					{
						fuir(cible);
					}
					else if(!memeEquipe && adversaire.getLeader() == -1 && (adversaire.getEquipe().size()) > this.getEquipe().size()) { // on cherche à se faire convertir par l'autre leader si son équipe est plus grande
						// duel
						parler("Je vais voir ailleurs ! " + refPlusProche, ve);
						deplacements.seDirigerVers(refPlusProche);
					}
					else if(!memeEquipe && adversaire.getDefense() < this.getCharisme())
					{
						parler("Je convertis quelqu'un qui n'est pas dangereux",ve);
						convertir(ve, cible);
					}
					else {
						parler("J'erre...", ve);
						deplacements.seDirigerVers(0); // errer
					}
				}
			} else { // si voisins, mais plus eloignes
				if(!memeEquipe && dernierPersonnage(ve)) { //On cherche à aller tuer notre leader si il ne reste qu'un personnage
					parler("Je vais vers mon leader " + this.getLeader(), ve);
					deplacements.seDirigerVers(this.getLeader());

				}
				else if(cible.getControleur().getElement() instanceof Personnage && isDanger((Personnage)cible.getControleur().getElement()))
				{
					parler("Je fuis un danger",ve);
					fuir(cible);
				}
				else if(cible.getControleur().getElement() instanceof Potion && isBonnePotion(cible.getControleur().getElement()))
				{
					parler("Je vais chercher une potion que j'aime",ve);
					deplacements.seDirigerVers(refPlusProche);
				}
				else if(this.getLeader() == -1 && this.getEquipe().size() == 0)//On est pas leader
				{
					parler("Je vais me trouver un leader !", ve);
					deplacements.seDirigerVers(trouverMeilleurLeader(ve.getControleur().getArene()));
				}
				else if(this.getLeader() == -1 && this.getEquipe().size() > 0)//On est leader
				{
					parler("Je vais vers le plus proche car il ne présente pas de danger", ve);
					deplacements.seDirigerVers(refPlusProche);
				}
				else {
					parler("J'erre...", ve);
					deplacements.seDirigerVers(new Point(50,50)); // On aime le centre
				}
			}
		}
	}




}
