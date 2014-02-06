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
import java.util.LinkedList;
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
    private LinkedList<Asteroide> misAsteroides;
    private URL rURL = this.getClass().getResource("/images/asteroid.gif");
    private int score = 0;
    private int asteroidScoreBonus = 100;
    private int asteroidScoreDeduction = 20;
    private int asteroidsFallenCounter = 0;

    private boolean illegalCollision; 
    
    /**
     * Metodo <I>init</I> sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se inizializan las variables o se crean los objetos a
     * usarse en el <code>Applet</code> y se definen funcionalidades.
     */
    public void init() {
        vidas = 5;    // Le asignamos un valor inicial a las vidas
        this.setSize(500, 500);

        int posX = (int) (Math.random() * (getWidth()));    // posicion en x es un cuarto del applet
        int posY = getHeight();    // posicion en y es un cuarto del applet
        URL eURL = this.getClass().getResource("/images/jupiter.gif");
        jupiter = new Planeta(posX, posY, Toolkit.getDefaultToolkit().getImage(eURL));
        if (jupiter.getPosX() > getWidth() - jupiter.getAncho()) {
            jupiter.setPosX(getWidth() - jupiter.getAncho());
        }
        if (jupiter.getPosY() > getHeight() - jupiter.getAlto()) {
            jupiter.setPosY(getHeight() - jupiter.getAlto());
        }

        misAsteroides = generateRandomAsteroidList(5, 10);

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

        object_clicked = false;
        illegalCollision = false; 
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

        for (Asteroide asteroid : misAsteroides) {
            asteroid.updateAsteroid();
        }

    }

    /**
     * Metodo usado para checar las colisiones del objeto elefante y asteroid
     * con las orillas del <code>Applet</code>.
     */
    public void checaColision() {
        for (Asteroide asteroid : misAsteroides) {
            if (asteroid.isIn_collision() && asteroid.getCollisionCyclesCounter() > 0) {
                asteroid.decreaseCollisionCyclesCounter();
            } else {
                //check collision with applet bottom
                if (asteroid.getPosY() > getHeight() - asteroid.getAlto() && !asteroid.isIn_collision()) {
                    bomb.play();
                    asteroid.collide();
                    if (vidas > 0) {
                        asteroidsFallenCounter++;
                        if (asteroidsFallenCounter >= 10) {
                            vidas--;
                            speed++;
                            asteroidsFallenCounter = 0;
                        }
                        score -= asteroidScoreDeduction;
                    }
                } else if (jupiter.intersecta(asteroid) && !asteroid.isIn_collision()) {
                    int displacement = asteroid.getPosX() - jupiter.getPosX();
                    if (asteroid.getPosY() < jupiter.getPosY() && displacement > -5 && displacement < (jupiter.getAncho() - asteroid.getAncho() + 5)) {
                        bomb.play();    //sonido al colisionar
                        asteroid.collide();
                        //incremento el puntaje y la velocidad
                        score += asteroidScoreBonus;
                    } else {
                        //If collision is from any other size
                        //Dont allow for the trespassing of the planet perimeter with the asteroid.
                        object_clicked = false;
                    }

                } else if (asteroid.getCollisionCyclesCounter() <= 0 && asteroid.isIn_collision()) {
                    asteroid.stopCollision();

                    //El asteroide se mueve al azar en la mitad derecha del appler.
                    asteroid.setPosX((int) (Math.random() * getWidth()));
                    if (asteroid.getPosX() > getWidth() - asteroid.getAncho()) {
                        //correct displacement out of screen
                        asteroid.setPosX(getWidth() - asteroid.getAncho());
                    }
                    asteroid.setPosY(0);
                    asteroid.setSpeed(speed);
                }

            }
        }
        
        int bounceoff = 5;
        //checks planet collision with applet
        if(jupiter.getPosX()<=0 || jupiter.getPosY()<=0 ||
           jupiter.getPosX()>=(getWidth()-jupiter.getAncho())||
           jupiter.getPosY()>=getHeight()-jupiter.getAlto()
          ){
            if(object_clicked){
                object_clicked = false;
            }
            if(jupiter.getPosX()<=0){
                jupiter.setPosX(jupiter.getPosX()+bounceoff);
            }
            if(jupiter.getPosX()>=(getWidth()-jupiter.getAncho())){
                jupiter.setPosX(jupiter.getPosX()-bounceoff);
            }
            if(jupiter.getPosY()<=0){
                jupiter.setPosY(jupiter.getPosY()+bounceoff);
            }
            if(jupiter.getPosY()>=(getHeight()-jupiter.getAlto())){
                jupiter.setPosY(jupiter.getPosY()-bounceoff);
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
            if (jupiter != null && !misAsteroides.isEmpty()) {
                int text_length = 70;
                g.setColor(Color.WHITE);
                Font newf = g.getFont().deriveFont(Font.BOLD);
                g.setFont(newf);
                g.drawString("vidas:", 15, 15);
                for (int i = 0; i < vidas; i++) {
                    g.drawImage(lives.getImage(), text_length + i * lives.getIconWidth(), 0, this);
                }
                String scoreString = "puntaje: "+score; 
                g.drawString(scoreString, getWidth()-150, 15);
                

                g.drawImage(jupiter.getImagenI(), jupiter.getPosX(), jupiter.getPosY(), this);

                for (Asteroide asteroid : misAsteroides) {
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

    public Asteroide crearAsteroide() {
        //randomly position asteroid at the top of the screen
        int posrX = (int) (Math.random() * getWidth());
        int posrY = (int) (Math.random() * -200) ;

        Asteroide newAsteroid = new Asteroide(posrX, posrY, Toolkit.getDefaultToolkit().getImage(rURL));
        if (newAsteroid.getPosX() > getWidth() - newAsteroid.getAncho()) {
            //correct displacement out of screen
            newAsteroid.setPosX(getWidth() - newAsteroid.getAncho());
        }
        return newAsteroid;
    }

    public LinkedList<Asteroide> generateRandomAsteroidList(int lower, int upper) {
        int R = (int) (Math.random() * (upper - lower)) + lower;
        LinkedList<Asteroide> asteroides = new LinkedList<Asteroide>();
        for (int i = 0; i < R; i++) {
            asteroides.add(crearAsteroide());
        }
        return asteroides;
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
