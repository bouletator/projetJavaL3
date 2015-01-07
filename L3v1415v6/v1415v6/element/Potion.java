package element;

/**
 * Une potion: un element donnant des bonus aux caracteristiques de celui qui
 * le ramasse.
 */
public class Potion extends Element {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Different de la vie de la potion, qui est ajoute au personnage lorsqu'il la ramasse. 
	 */
	protected boolean ramassee;
	
	/**
	 * Constructeur d'une potion avec un nom et une quantite de force et de charisme
	 * (ces quantites sont celles ajoutees lorsqu'un Personnage ramasse cette potion).
	 * @param nom
	 * @param force
	 * @param charisme
	 */
	public Potion(String nom, int force, int charisme , int HP, int vitesse , int defense) {
		super(nom);
		caract.put("vie", 0); // pas de bonus de vie
		ajouterCaract("force", force);
		ajouterCaract("charisme", charisme);

		/* ---------------------------------------------------
		 * Nouvelles caractéristiques
		 * ------------------------------------------------ */
		ajouterCaract("hp", HP);
		ajouterCaract("vitesse", vitesse);
		ajouterCaract("defense", defense);
		
		ramassee = false;
	} 
	
	/* -----------------------------------------------------------
	 * 		ACCESSEURS
	 * -------------------------------------------------------- */
	
	/** ----------------------------------------------------------
	 * 		public int getForce()
	 *  ----------------------------------------------------------
	 * Retourne la valeur du bonus de force.
	 * -----------------------------------------------------------
	 * @return bonus de force
	 * -------------------------------------------------------- */
	public int getForce() {
		return getCaract("force");
	}
	
	/** ----------------------------------------------------------
	 * 		public int getCharisme()
	 *  ----------------------------------------------------------
	 * Retourne la valeur du bonus du charisme.
	 * -----------------------------------------------------------
	 * @return bonus de charisme 
	 * -------------------------------------------------------- */
	public int getCharisme() {
		return getCaract("charisme");
	}
	
	
	/** ----------------------------------------------------------
	 * 		public int getVitesse()
	 *  ----------------------------------------------------------
	 * Retourne la valeur du bonus de vitesse.
	 * -----------------------------------------------------------
	 * @return bonus de vitesse
	 * -------------------------------------------------------- */
	public int getVitesse() {
		return getCaract("vitesse");
	}
	
	/** ----------------------------------------------------------
	 * 		public int getDefense()
	 *  ----------------------------------------------------------
	 * Retourne la valeur du bonus de defense.
	 * -----------------------------------------------------------
	 * @return bonus de defense
	 * -------------------------------------------------------- */
	public int getDefense() {
		return getCaract("defense");
		}
	
	/** ----------------------------------------------------------
	 * 		public int getHP()
	 *  ----------------------------------------------------------
	 * Retourne la valeur du bonus de HP
	 * @return bonus de HP
	 * -------------------------------------------------------- */
	public int getHP() {
		return getCaract("hp");
	}

	
	@Override
	/** ----------------------------------------------------------
	 * 		public int getVie()
	 *  ----------------------------------------------------------
	 * Retourne si la potion existe toujours ou pas.
	 * @return vie de la potion
	 * -------------------------------------------------------- */
	public int getVie() {
		return ramassee? 0: 1;
	}
	
	
	
	/** ----------------------------------------------------------
	 * 		public void setVie(int vie)
	 *  ----------------------------------------------------------
	 *  Modifie ramassee en fonction de la vie passée en paramètre (<= 0 : 
	 *  ramassee, > 0 : non ramassee).
	 * -------------------------------------------------------- */
	/**
	 */
	@Override
	public void setVie(int vie) {
		ramassee = vie <= 0;
	}


	@Override
	/** ----------------------------------------------------------
	 * 		public String toString
	 *  ----------------------------------------------------------
	 *  Cette méthode détermine l'affichage des différentes stats
	 *  ----------------------------------------------------------
	 * @return string
	 * ------------------------------------------------------- */
	public String toString(){
		return super.toString() + "[" + " HP : " + getHP() + ", For : " + getForce() + ", Vit : " + getVitesse()   + ", Def : " + getDefense() + ", Char : "  + getCharisme() + "]" ;
	}
}