package onlinestore;

import javax.swing.UIManager;
import jade.core.Runtime; 
import jade.core.Profile; 
import jade.core.ProfileImpl; 
import jade.wrapper.*;
import java.util.ArrayList;
import models.Producto;
/**
 *
 * @author Zaidibeth
 */
public class OnlineStore {
    
    public static void main(String[] args) {
       try {
            // Usar look & feel nativo en las vistas
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        // Plataforma JADE
        Runtime runtime = Runtime.instance();

        // Perfil predetermiando (localhost:1099)
        Profile profile = new ProfileImpl();

        // Contenedor principal
        AgentContainer mainContainer = runtime.createMainContainer(profile);

        // Crear agentes
        try {
            // RMA (Jade Boot GUI)
            AgentController ac = mainContainer.createNewAgent("rma",
                    "jade.tools.rma.rma", null);
            ac.start();

            // Planificador
            ac = mainContainer.createNewAgent("Login",
                    "agents.Login", null);
            ac.start();
          
            // 4 personas
            String[] personas = {"Gina", "Gabriel", "Zaidibeth", "Katherine"};
            for(int i = 0; i < personas.length; i++) {
                ac = mainContainer.createNewAgent(personas[i],
                        "agents.Persona", null);
                ac.start();
            }
        } 
        catch (StaleProxyException e) {
            e.printStackTrace();
        }
        
        ArrayList<Producto> productos=new ArrayList<Producto>();
        productos.add(new Producto("J7","Celulares"));
        productos.add(new Producto("P8","Celulares"));
        productos.add(new Producto("Lenovo Thinkpad","Laptops"));
        productos.add(new Producto("Sony vaio","Laptops"));
        productos.add(new Producto("Lapicero","Oficina"));
        productos.add(new Producto("Lapiz","Oficina"));
        productos.add(new Producto("A1","Smartwatch"));
        productos.add(new Producto("z01","Smartwatch"));
        productos.add(new Producto("Cocina","Hogar"));
        productos.add(new Producto("Televisor","Hogar"));
        
        //fin main
    }
    
    //fin clase
}
