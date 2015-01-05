package element;

/**
 * Une potion: un element donnant des bonus aux caracteristiques de celui qui
 * le ramasse.
 */
public class Potion extends Objet {

	private static final long serialVersionUID = 1L;

	private final int gain;
	


	/**
	 * Constructeur. Le nombre de vie est par defaut initialise a 200.
	 *
	 * @param nom le nom de l'element a creer
	 */
	public Potion(String nom, int gainVie) {
		super(nom);
		this.gain = gainVie;
	}

	/**
	 * Retourne la valeur du bonus de force.
	 * @return bonus de force
	 */
	public int getGain() {
		return gain;
	}

	

	@Override
	public String toString(){
		return super.toString() + "[" + getGain() + "]";
	}
}
