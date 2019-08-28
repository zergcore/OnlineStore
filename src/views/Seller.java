package views;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import models.Producto;
import views.ModeloTablaProductos;
import views.EditProduct;

public class Seller extends javax.swing.JFrame {

    private agents.Persona agente;
    private views.ModeloTablaProductos modeloTablaProductos= new views.ModeloTablaProductos(new ArrayList<Producto>());
    
    public Seller(agents.Persona vendedor) {
        agente=vendedor;
        initComponents();
        
        //Listeners
        tblProducts.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
        @Override
            public void valueChanged(ListSelectionEvent event) {
                boolean enable = tblProducts.getSelectedRow() > -1;
                btnOfertar.setEnabled(enable);
        }
    });
        
        /*addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                agente.doDelete();
            }
        });*/   
        
    }
    
    public void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
    
        // Metodo que usaría el agente comprador para obtener sus Paquetes
    public ArrayList<Producto> getProductos() {
        return modeloTablaProductos.getProductos();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProducts = new javax.swing.JTable();
        btnEliminar = new javax.swing.JButton();
        btnOfertar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblProducts.setModel(modeloTablaProductos);
        tblProducts.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tblProducts);

        btnEliminar.setText("Eliminar oferta");

        btnOfertar.setText("Ofertar");
        btnOfertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOfertarActionPerformed(evt);
            }
        });

        btnEditar.setText("Editar oferta");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnOfertar)
                        .addGap(54, 54, 54)
                        .addComponent(btnEditar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEliminar)))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOfertar)
                    .addComponent(btnEliminar)
                    .addComponent(btnEditar))
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOfertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOfertarActionPerformed
        if (tblProducts.getSelectedRow()>-1){
            int precio=Integer.parseInt(JOptionPane.showInputDialog("En que precio deseas ofertar?"));
            tblProducts.getModel().setValueAt(precio, tblProducts.getSelectedRow(), 1);   
        }
        
    }//GEN-LAST:event_btnOfertarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        if(tblProducts.getSelectedRow()>-1){
            Producto producto=modeloTablaProductos.getProducto(tblProducts.getSelectedRow());
            EditProduct editproduct;
            editproduct = new EditProduct(this, "Editar Producto", true, producto);
            producto=editproduct.mostrar();
            modeloTablaProductos.actualizarProducto(tblProducts.getSelectedRow(), producto);
        }
    }//GEN-LAST:event_btnEditarActionPerformed
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnOfertar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblProducts;
    // End of variables declaration//GEN-END:variables

}
