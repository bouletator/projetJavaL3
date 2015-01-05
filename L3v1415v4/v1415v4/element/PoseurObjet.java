package element;

import java.util.LinkedList;

/**
 * Created by clement on 05/01/2015.
 */
public class PoseurObjet extends Personnage{

	private final int maxObjets;

	private LinkedList<Objet> listeObjet = new LinkedList<Objet>();

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
	public PoseurObjet(String nom, int force, int charisme, int defense, int determination, int vitesse, int vision, int nbObjetMAx) {
		super(nom, force, charisme, defense, determination, vitesse, vision);
		this.maxObjets = nbObjetMAx;
	}

	/***
	 * Ajoute un objet à la liste si le nombre d'objet n'est pas supérieur ou égal au max
	 * @param o objet à ajouter
	 */
	public void ajouterObjet(Objet o)
	{
		if(listeObjet.size() >= maxObjets)
		{
			return;
		}

		listeObjet.add(o);
	}

	/***
	 * Retourne le premier objet de la liste
	 * @return listeObjet.pop();
	 */
	public Objet getPremierObjet(){
		return listeObjet.pop();
	}
}
