package element;

import interaction.Actions;
import interaction.Deplacements;
import interfaceGraphique.VueElement;
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

		System.out.println(ve.getPoint());

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

    @Override
	protected boolean isBonnePotion(Element element) {
		Potion potion = (Potion) element;

		if(super.isBonnePotion(element))return true;

		///On ne se soucie pas de la force

		// limite: defense maximale à perdre en prenant une potion
		final int DEFENSE_MAX_LOSS = 15;

		//si la potion diminue la vitesse ou le charisme ou trop de defense on ne la prend pas
		if (potion.getVitesse() < 0
				|| potion.getCharisme() < 0
				|| potion.getDefense() < DEFENSE_MAX_LOSS)
			return false;

		//si la potion augmente la vitesse, le charisme ou la défense en vérifiant les conditions précédentes alors on les prend
		if (potion.getVitesse() > 0 || potion.getCharisme() > 0 || potion.getDefense() > 0) return true;

		return false;
	}

	@Override
	protected boolean isDanger(Personnage personnage) {
		if(super.isDanger(personnage))return true;

		final int CRITICAL_RANGE = 11;

		// cette valeur détermine les points de vie restants en cas de coup critique
		int thisMinHPApresAttaque = (CRITICAL_RANGE + personnage.getForce()) * (1 - this.getDefense()/100);
		int thisMaxHPApresAttaque = (personnage.getForce()) * (1 - this.getDefense()/100);

		// si le personnage peut nous faire très mal mais pas tuer en un coup critique
		// et si on peut le convertir
		if (thisMaxHPApresAttaque > 0 && thisMinHPApresAttaque <= CRITICAL_RANGE) {
			//TODO A choisir. Peut être que c'est une situation plus dangeureuse que prévue
			return false;
		}

		// si on peut convertir le personnage
		if (personnage.getForce() < this.getCharisme()) return false;

		// si on peut tuer le personnage d'un coup
		if ((CRITICAL_RANGE + this.getForce()) * (1 - personnage.getDefense()/100) < 0)
			return false;

		//TODO Cas de base
		return false;
	}
}
