package agents;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import jade.core.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;

import models.Producto;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class Persona extends Agent {
    private views.Persona gui;
    public String papel;
    private Producto producto;
    private int dineroDisponible;
    private int mejorPrecio;
    private ArrayList<String> vendedores;
    private ArrayList<models.Producto> productos;
    private Producto p2;    

    protected void setup() {
        gui = new views.Persona(this, productos);
        gui.setVisible(false);

        // Registrar agente como "persona"
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("persona");
        sd.setName(this.getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // Agregar comportamiento ContractNetResponder (Venta de productos)
        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP)
        );
        addBehaviour(new ContractNetResponder(this, template) {
            protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
                if(papel.equals("Comprador")) {
                    throw new RefuseException("No soy vendedor");
                }
                ArrayList<Producto> productos = gui.getProductosVender();
                producto = new Producto(cfp.getContent());
                if (productos.contains(producto)) {
                    ACLMessage propose = cfp.createReply();
                    propose.setPerformative(ACLMessage.PROPOSE);
                    propose.setContent(String.valueOf(productos.get(productos.indexOf(producto)).getPrecio()));
                    return propose;
                } else {
                    System.out.println("Agent " + getLocalName() + ": Refuse");
                    throw new RefuseException("No tengo el producto");
                }
            }

            protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
                ACLMessage inform = accept.createReply();
                inform.setPerformative(ACLMessage.INFORM);
                return inform;
            }
        });

        // Agregar comportamiento AchieveREResponder (Para definir un papel asignado por el planificador)
        template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
        );
        addBehaviour(new AchieveREResponder(this, template) {
            protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
                if(request.getContent().equals(papel)) {
                    throw new RefuseException("El papel a asignar es el mismo que el actual");
                }
                ACLMessage agree = request.createReply();
                agree.setPerformative(ACLMessage.AGREE);
                return agree;
            }

            protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
                try {
                    actualizarPapel(request.getContent());
                    ACLMessage inform = request.createReply();
                    inform.setPerformative(ACLMessage.INFORM);
                    return inform;
                } catch (FIPAException fe) {
                    throw new FailureException(fe.getMessage());
                }
            }
        });

        System.out.println(this.getLocalName() + " iniciado");
    }

    protected void takeDown() {
        // Eliminar vista
        gui.dispose();
        // Eliminar agente del registro
        try {
            DFService.deregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(this.getLocalName() + " finalizado");
    }

    void actualizarPapel(String papel) throws FIPAException {
        this.papel = papel;

        // Nuevo registro
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());

        // Papel de persona
        ServiceDescription sd = new ServiceDescription();
        sd.setType("persona");
        sd.setName(this.getLocalName());
        dfd.addServices(sd);

        // Papel de comprador/vendedor
        sd = new ServiceDescription();
        sd.setType(papel);
        sd.setName(this.getLocalName());
        dfd.addServices(sd);

        // Actualizar registro y actualizar GUI
        DFService.modify(this, dfd);
        
        gui.setPapel(papel);
    }
    

    public ArrayList<String> buscarVendedores() {
        ArrayList<String> vendedores = new ArrayList<String>();

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Vendedor");
        dfd.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, dfd);
            for(int i = 0; i < result.length; i++) {
                vendedores.add(result[i].getName().getLocalName());
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        return vendedores;
    }
    
    public void buscarProducto(Producto producto) {
        ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        vendedores = buscarVendedores();
        Iterator<String> it = vendedores.iterator();
        while(it.hasNext()) {
            msg.addReceiver(new AID(it.next(), AID.ISLOCALNAME));
        }
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        msg.setReplyByDate(new Date(System.currentTimeMillis() + 5000));
        msg.setContent(producto.getNombre());
        dineroDisponible = producto.getPrecio();

        addBehaviour(new ContractNetInitiator(this, msg) {
            protected void handlePropose(ACLMessage propose, Vector v) {
                System.out.println("Vendedor " + propose.getSender().getLocalName() +
                        " ofrece el producto en Bs." + propose.getContent());
            }

            protected void handleRefuse(ACLMessage refuse) {
                System.out.println(refuse.getSender().getLocalName() +
                        ": " + refuse.getContent());
            }

            protected void handleFailure(ACLMessage failure) {
                if (failure.getSender().equals(myAgent.getAMS())) {
                    // Mensaje de la plataforma JADE: El destinatario no existe
                    System.out.println("El vendedor no existe");
                } else {
                    gui.aviso("Vendedor " + failure.getSender().getLocalName() +
                            " falló en realizar la venta");
                }
            }

            protected void handleAllResponses(Vector responses, Vector acceptances) {
                if(responses.size() > 0) { // Aceptar la mejor propuesta
                    mejorPrecio = dineroDisponible; // Comprueba que la propuesta entre en el presupuesto
                    @SuppressWarnings("unused")
                    AID bestProposer = null;
                    ACLMessage accept = null;
                    Enumeration e = responses.elements();
                    while (e.hasMoreElements()) {
                        ACLMessage response = (ACLMessage) e.nextElement();
                        if (response.getPerformative() == ACLMessage.PROPOSE) {
                            ACLMessage reply = response.createReply();
                            reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            acceptances.addElement(reply);
                            int precio = Integer.parseInt(response.getContent());
                            if(precio <= mejorPrecio) {
                                mejorPrecio = precio;
                                bestProposer = reply.getSender();
                                accept = reply;
                            }
                        }
                    }
                    // Aceptar propuesta del Vendedor mas economico
                    if(accept != null) {
                        accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    } else {
                        if (acceptances.isEmpty()){
                            gui.aviso("No se consiguió el producto");
                        }
                        else {
                            gui.aviso("Dinero insuficiente para comprar el producto");
                        }
                    }
                } else { // No hubo ninguna respuesta
                    gui.aviso("No se consiguieron vendedores");
                }
            }

            protected void handleInform(ACLMessage inform) {
                gui.aviso("Se realizó la compra del producto al vendedor " + 
                        inform.getSender().getLocalName() +
                        " por " + mejorPrecio + " Bs.");
            }
        });
    }
    
    /*public void aplicarPapel(String persona, String papel){
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID(persona, AID.ISLOCALNAME));
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
        msg.setContent(papel);

        // Agregar comportamiento AchieveREInitiator (Asignar papel a persona)
        addBehaviour(new AchieveREInitiator(this, msg) {
            protected void handleInform(ACLMessage inform) {
                System.out.println(inform.getSender().getLocalName() + " recibió de manera correcta su papel");
            }

            protected void handleRefuse(ACLMessage refuse) {
                System.out.println(refuse.getSender().getLocalName() + ": " + refuse.getContent());
            }

            protected void handleFailure(ACLMessage failure) {
                if (failure.getSender().equals(myAgent.getAMS())) {
                    // Mensaje de la plataforma JADE: El destinatario no existe
                    System.out.println("La persona no existe");
                } else {
                    System.out.println(failure.getSender().getLocalName() + ": " + failure.getContent());
                }
            }
        });   
    }*/
}