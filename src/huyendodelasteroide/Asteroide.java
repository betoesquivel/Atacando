package huyendodelasteroide;

/**
 * Clase Raton
 *
 * @author Antonio Mejorado
 * @version 1.00 2008/6/13
 */
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;

public class Asteroide extends CuerpoCeleste {

    private int speed = 1;
    private boolean in_collision;
    private boolean changeImage;
    private int collisionCycles;
    private int collisionCyclesCounter;

    private URL rURL = this.getClass().getResource("/images/asteroid.gif");
    URL expURL = this.getClass().getResource("/images/explosion_bien.gif");
    private Image normalImage = Toolkit.getDefaultToolkit().getImage(rURL);    //icono.
    private Image collisionImage = Toolkit.getDefaultToolkit().getImage(expURL);    //icono.

    /**
     * Metodo constructor que hereda los atributos de la clase
     * <code>CuerpoCeleste</code>.
     *
     * @param posX es la <code>posiscion en x</code> del objeto raton.
     * @param posY es el <code>posiscion en y</code> del objeto raton.
     * @param image es la <code>imagen</code> del objeto raton.
     */
    public Asteroide(int posX, int posY, Image image) {
        super(posX, posY, image);
        in_collision = false;
        changeImage = false;
        collisionCycles = 25;
        collisionCyclesCounter = -1;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isIn_collision() {
        return in_collision;
    }

    public void setIn_collision(boolean in_collision) {
        this.in_collision = in_collision;
    }

    public boolean isChangeImage() {
        return changeImage;
    }

    public void setChangeImage(boolean changeImage) {
        this.changeImage = changeImage;
    }

    public int getCollisionCycles() {
        return collisionCycles;
    }

    public void setCollisionCycles(int collisionCycles) {
        this.collisionCycles = collisionCycles;
    }

    public int getCollisionCyclesCounter() {
        return collisionCyclesCounter;
    }

    public void setCollisionCyclesCounter(int collisionCyclesCounter) {
        this.collisionCyclesCounter = collisionCyclesCounter;
    }

    public URL getrURL() {
        return rURL;
    }

    public void setrURL(URL rURL) {
        this.rURL = rURL;
    }

    public URL getExpURL() {
        return expURL;
    }

    public void setExpURL(URL expURL) {
        this.expURL = expURL;
    }

    public Image getNormalImage() {
        return normalImage;
    }

    public void setNormalImage(Image normalImage) {
        this.normalImage = normalImage;
    }

    public Image getCollisionImage() {
        return collisionImage;
    }

    public void setCollisionImage(Image collisionImage) {
        this.collisionImage = collisionImage;
    }

    public void decreaseCollisionCyclesCounter() {
        setCollisionCyclesCounter(collisionCyclesCounter - 1);
    }

    public void collide() {
        in_collision = true;
        collisionCyclesCounter = collisionCycles;
        changeImage = true;
    }

    public void stopCollision() {
        in_collision = false;
        collisionCyclesCounter = -1;
        changeImage = true;
    }

    public void updateAsteroidImage() {
        if (in_collision) {
            setImageIcon(new ImageIcon(collisionImage));
        } else {
            setImageIcon(new ImageIcon(normalImage));
        }
        changeImage = false;
    }

    public void updateAsteroid() {
        if (!in_collision) {
            fall();
        }

        if (changeImage) {
            updateAsteroidImage();
        }
    }

    public void fall() {
        setPosY(getPosY() + speed);
    }
}
