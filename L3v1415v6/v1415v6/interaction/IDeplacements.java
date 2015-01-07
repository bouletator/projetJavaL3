package interaction;

import java.awt.Point;

public interface IDeplacements {
	/**
     * Deplace ce sujet d'une case en direction du sujet dont la reference est donnee en parametre
     * ref de soi-meme pour du sur-place, 0 pour errer et ref d'un voisin (s'il existe)
     * On ne manipule que la VueElement
     * @param ref la reference de l'element cible
     */
    public void seDirigerVers(int ref);

    /**
     * Deplace ce sujet d'une case en direction de la case specifiee
     * On ne manipule que la VueElement
     * @param pvers case cible
     */
    public void seDirigerVers(Point pvers);
}
