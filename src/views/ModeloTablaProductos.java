package views;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import models.Producto;

public class ModeloTablaProductos extends AbstractTableModel{
    
    private ArrayList<Producto> Productos;
        private String[] columnas = {"Descripción", "Precio", "Categoria"};

        public ModeloTablaProductos(ArrayList<Producto> Productos) {
            super();
            this.Productos = Productos;
        }

        public String getColumnName(int col) {
            return columnas[col];
        }

        @Override
        public int getColumnCount() {
            return columnas.length;
        }

        @Override
        public int getRowCount() {
            return Productos.size();
        }

        @Override
        public Object getValueAt(int row, int col) {
            Object object = null;
            switch(col) {
            case 0:
                object = (Object) Productos.get(row).getNombre();
                break;
            case 1:
                object = (Object) Productos.get(row).getPrecio();
                break;
            case 2:
                object=(Object) Productos.get(row).getCategoria();
                break;
            }
            return object;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        public void agregarProducto(Producto Producto) {
            Productos.add(Producto);
            fireTableDataChanged();
        }

        public Producto getProducto(int index) {
            return Productos.get(index);
        }

        public void actualizarProducto(int index, Producto ProductoActualizado) {
            Productos.set(index, ProductoActualizado);
            fireTableDataChanged();
        }

        public void eliminarProducto(int index) {
            Productos.remove(index);
            fireTableDataChanged();
        }

        public ArrayList<Producto> getProductos() {
            return Productos;
        }
    
}
