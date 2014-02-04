package huyendodelasteroide;

/**
 * @(#)Inteligencia.java
 *
 * Scores Applet application
 *
 * @author Antonio Mejorado
 * @version 1.00 2008/6/19
 */
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;
import javax.swing.ImageIcon;

public class Inteligencia extends Applet implements Runnable, MouseListener, MouseMotionListener {

    private static final long serialVersionUID = 1L;
    // Se declaran las variables.
    private int incX;    // Incremento en x
    private int incY;    // Incremento en y
    private int vidas;    // vidas del elefante.
    private final int MIN = -5;    //Rango minimo al generar un numero al azar.
    private final int MAX = 6;    //Rango maximo al generar un numero al azar.
    private Image dbImage;    // Imagen a proyectar
    private Image gameover;    //Imagen a desplegar al acabar el juego.	 
    private Image live_image;        //Imagen a desplegar que representa una vida.
    private ImageIcon lives;
    private Image explosion;
    private Graphics dbg;	// Objeto grafico
    private AudioClip bomb;    //Objeto AudioClip 
    private Planeta jupiter;    // Objeto de la clase Planeta
    private Asteroide asteroid;    //Objeto de la clase Asteroide
    private int speed; //contains the speed of the asteroid
    private boolean object_clicked;
    private boolean is_exploding;
    private int explosion_cycles;
    private int explosion_cycles_counter;
    private int explosion_Y;
    private int explosion_X;

    /**
     * Metodo <I>init</I> sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se inizializan las variables o se crean los objetos a
     * usarse en el <code>Applet</code> y se definen funcionalidades.
     */
    public void init() {
        vidas = 3;    // Le asignamos un valor inicial a las vidas
        this.setSize(600, 350);
        int posX = (int) (Math.random() * (getWidth() / 4));    // posicion en x es un cuarto del applet
        int posY = (int) (Math.random() * (getHeight() / 4));    // posicion en y es un cuarto del applet
        URL eURL = this.getClass().getResource("/images/jupiter.gif");
        jupiter = new Planeta(posX, posY, Toolkit.getDefaultToolkit().getImage(eURL));
        int posrX = (int) (Math.random() * (getWidth() / 4)) + getWidth() / 2;    //posision x es tres cuartos del applet
        int posrY = (int) (Math.random() * (getHeight() / 4)) + getHeight() / 2;    //posision y es tres cuartos del applet
        URL rURL = this.getClass().getResource("/images/asteroid.gif");
        asteroid = new Asteroide(posrX, posrY, Toolkit.getDefaultToolkit().getImage(rURL));
        asteroid.setPosX(asteroid.getPosX() - asteroid.getAncho());
        asteroid.setPosY(asteroid.getPosY() - asteroid.getAlto());
        speed = 1;
        setBackground(Color.black);

        addMouseListener(this);
        addMouseMotionListener(this);
        //Se cargan los sonidos.
        URL baURL = this.getClass().getResource("/sounds/8-bit-explosion.wav");
        bomb = getAudioClip(baURL);
        URL goURL = this.getClass().getResource("/images/gameover_1.jpg");
        gameover = Toolkit.getDefaultToolkit().getImage(goURL);
        URL livesURL = this.getClass().getResource("/images/whiterectangle.gif");
        live_image = Toolkit.getDefaultToolkit().getImage(livesURL);
        lives = new ImageIcon(live_image);

        URL expURL = this.getClass().getResource("/images/explosion_bien.gif");
        explosion = Toolkit.getDefaultToolkit().getImage(expURL);
        explosion_cycles = 10;
        explosion_cycles_counter = -1;
        object_clicked = false;
    }

    /**
     * Metodo <I>start</I> sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se crea e inicializa el hilo para la animacion este metodo
     * es llamado despues del init o cuando el usuario visita otra pagina y
     * luego regresa a la pagina en donde esta este <code>Applet</code>
     *
     */
    public void start() {
        // Declaras un hilo
        Thread th = new Thread(this);
        // Empieza el hilo
        th.start();
    }

    /**
     * Metodo <I>run</I> sobrescrito de la clase <code>Thread</code>.<P>
     * En este metodo se ejecuta el hilo, es un ciclo indefinido donde se
     * incrementa la posicion en x o y dependiendo de la direccion, finalmente
     * se repinta el <code>Applet</code> y luego manda a dormir el hilo.
     *
     */
    public void run() {
        while (vidas > 0) {
            actualiza();
            checaColision();

            // Se actualiza el <code>Applet</code> repintando el contenido.
            repaint();

            try {
                // El thread se duerme.
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                System.out.println("Error en " + ex.toString());
            }
        }
    }

    /**
     * Metodo usado para actualizar la posicion de objetos elefante y asteroid.
     *
     */
    public void actualiza() {
        //Dependiendo de la direccion del elefante es hacia donde se mueve.
        if (!is_exploding) {
            //Acutalizo la posicion del asteroid
            if (jupiter.getPosX() > asteroid.getPosX()) {
                incX = speed;
                asteroid.setPosX(asteroid.getPosX() + incX);
            } else {
                incX = -1 * speed;
                asteroid.setPosX(asteroid.getPosX() + incX);
            }

            if (jupiter.getPosY() > asteroid.getPosY()) {
                incY = speed;
                asteroid.setPosY(asteroid.getPosY() + incY);
            } else {
                incY = -1 * speed;
                asteroid.setPosY(asteroid.getPosY() + incY);
            }
        }

    }

    /**
     * Metodo usado para checar las colisiones del objeto elefante y asteroid
     * con las orillas del <code>Applet</code>.
     */
    public void checaColision() {
        if (is_exploding && explosion_cycles_counter > 0) {
            explosion_cycles_counter--;
        } else {
            if (jupiter.intersecta(asteroid) && !is_exploding) {
                bomb.play();    //sonido al colisionar
                explosion_cycles_counter = explosion_cycles;
                is_exploding = true;
                explosion_X = asteroid.getPosX();
                explosion_Y = asteroid.getPosY();

                //El asteroide se mueve al azar en la mitad derecha del appler.
                asteroid.setPosX((int) (Math.random() * getWidth() / 2) + getWidth() / 2 - asteroid.getAncho());
                asteroid.setPosY((int) (Math.random() * getHeight() / 2) + getHeight() / 2 - asteroid.getAlto());

                object_clicked = false;

                if (vidas > 0) {
                    vidas--;
                    speed++;
                }
            } else {
                is_exploding = false;
            }
        }

    }

    /**
     * Metodo <I>update</I> sobrescrito de la clase <code>Applet</code>,
     * heredado de la clase Container.<P>
     * En este metodo lo que hace es actualizar el contenedor
     *
     * @param g es el <code>objeto grafico</code> usado para dibujar.
     */
    public void update(Graphics g) {
        // Inicializan el DoubleBuffer
        if (dbImage == null) {
            dbImage = createImage(this.getSize().width, this.getSize().height);
            dbg = dbImage.getGraphics();
        }

        // Actualiza la imagen de fondo.
        dbg.setColor(getBackground());
        dbg.fillRect(0, 0, this.getSize().width, this.getSize().height);

        // Actualiza el Foreground.
        dbg.setColor(getForeground());
        paint(dbg);

        // Dibuja la imagen actualizada
        g.drawImage(dbImage, 0, 0, this);
    }

    /**
     * Metodo <I>paint</I> sobrescrito de la clase <code>Applet</code>, heredado
     * de la clase Container.<P>
     * En este metodo se dibuja la imagen con la posicion actualizada, ademas
     * que cuando la imagen es cargada te despliega una advertencia.
     *
     * @param g es el <code>objeto grafico</code> usado para dibujar.
     */
    public void paint(Graphics g) {
        if (vidas > 0) {
            if (jupiter != null && asteroid != null) {
                int text_length = 70;
                g.setColor(Color.WHITE);
                Font newf = g.getFont().deriveFont(Font.BOLD);
                g.setFont(newf);
                g.drawString("vidas:", 15, 15);
                for (int i = 0; i < vidas; i++) {
                    g.drawImage(lives.getImage(), text_length + i * lives.getIconWidth(), 0, this);
                }
                g.drawImage(jupiter.getImagenI(), jupiter.getPosX(), jupiter.getPosY(), this);

                if (is_exploding) {
                    g.drawImage(explosion, explosion_X, explosion_Y, this);
                } else {
                    //Dibuja la imagen en la posicion actualizada
                    g.drawImage(asteroid.getImagenI(), asteroid.getPosX(), asteroid.getPosY(), this);
                }

            } else {
                //Da un mensaje mientras se carga el dibujo	
                g.drawString("No se cargo la imagen..", 20, 20);
            }
        } else {
            g.drawImage(gameover, 0, 0, this);
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {

    }

    /**
     * <I>mousePressed</I>
     * Changes the state of the object to clicked when it is clicked to begin
     * dragging.
     *
     * @param me contains the mouse event captured.
     */
    @Override
    public void mousePressed(MouseEvent me) {
        jupiter.setMx(me.getX());
        jupiter.setMy(me.getY());
        object_clicked = jupiter.planet_is_clicked();

        me.consume();
    }

    /**
     * <I>mouseRealeased</I>
     * Changes the state of the object to not clicked to stop dragging.
     *
     * @param me contains the mouse event captured
     */
    @Override
    public void mouseReleased(MouseEvent me) {
        object_clicked = false;
        me.consume();
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Activated when the mouse is dragged I check if it is in a clicked state
     * so that it can be dragged.
     *
     * @param me contains the mouse event
     */
    @Override
    public void mouseDragged(MouseEvent me) {
        if (object_clicked) {
            jupiter.drag(me);
        }
        me.consume();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
