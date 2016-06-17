/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import DB.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author alejandro
 */
public class HacerPedido extends javax.swing.JFrame {

    /**
     * Creates new form HacerPedido
     */
    public HacerPedido() {
        initComponents();
        this.setTitle("Hacer un nuevo pedido");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        txtBusqueda.grabFocus();
        mostrarProductos("");
        mostrarPedidos();
        tablaProductosRegistrados.setEditingRow(ERROR);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                //close();
            }
        });
    }
    
    //Creamos la instancia 'con' de tipo ConexionBD
        ConexionBD cc = new ConexionBD();
        Connection cn = cc.conectar();
    
    
    //Realiza consulta de los productos.
    public void mostrarProductos(String valor)
    {
        DefaultTableModel model;
        
        //Encabezados de la tabla.
        String [] titulos = {"Producto", "Familia"};
        String [] productos = new String[2];
        
        //Creamos nuestra sentencia SQL.
        String sql = "SELECT nombre, familia FROM producto WHERE nombre LIKE '%"+valor+"%' ";
        
        //Creamos el objeto para la tabla que muestra los datos de la base de datos.
        model = new DefaultTableModel(null, titulos);
        
        Statement st;
        try{
            st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            //Validamos que el resultset contenga datos o esté vacío.
            if (rs != null && rs.next() ) 
            { 
                //codigo para tratar al conjunto de registros o al registro obtenido 
                System.out.println("Se ha encontrado algo");
                //Regresa el puntero al inicio para no perder el primer dato de la tabla.
                rs.beforeFirst();
            } 
            else 
            {
                //si entra a este else quiere decir que no obtuviste ningun registro 
                //o sea que tu ResultSet fue nulo.
                //System.err.println("No se ha encontrado nada");
                JOptionPane.showMessageDialog(null, "No hay ningún producto con ese nombre en la base de datos.",
                "No se encontró el elemento", JOptionPane.INFORMATION_MESSAGE);
                txtBusqueda.setText("");
                txtBusqueda.grabFocus();
            }
            
            while(rs.next())
                {
                    productos[0] = rs.getString("nombre");
                    productos[1] = rs.getString("familia");
                    model.addRow(productos);
                }
                tablaProductosRegistrados.setModel(model);
            
        } catch (SQLException ex) {
            //Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    
    //Realiza consulta de los pedidos.
    public void mostrarPedidos()
    {
        DefaultTableModel model;
        
        //Encabezados de la tabla.
        String [] titulos = {"Cantidad","Producto", "Familia"};
        String [] pedidos = new String[3];
        
        //Creamos nuestra sentencia SQL.
        String sql = "SELECT nombre, familia, cantidad FROM pedido";
        
        //Creamos el objeto para la tabla que muestra los datos de la base de datos.
        model = new DefaultTableModel(null, titulos);
        
        Statement st;
        try{
            st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            //Validamos que el resultset contenga datos o esté vacío.
//            if (rs != null && rs.next() ) 
//            { 
//                //codigo para tratar al conjunto de registros o al registro obtenido 
//                System.out.println("Se ha encontrado algo");
//                //Regresa el puntero al inicio para no perder el primer dato de la tabla.
//                rs.beforeFirst();
//            } 
//            else 
//            {
//                //si entra a este else quiere decir que no obtuviste ningun registro 
//                //o sea que tu ResultSet fue nulo.
//                //System.err.println("No se ha encontrado nada");
//                JOptionPane.showMessageDialog(null, "No hay ningún producto con ese nombre en la base de datos.",
//                "No se encontró el elemento", JOptionPane.INFORMATION_MESSAGE);
//                txtBusqueda.grabFocus();
//            }
            
            while(rs.next())
                {
                    pedidos[0] = rs.getString("cantidad");
                    pedidos[1] = rs.getString("nombre");
                    pedidos[2] = rs.getString("familia");
                    model.addRow(pedidos);
                }
                tablaPedidos.setModel(model);
            
        } catch (SQLException ex) {
            //Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    
    public void agregarPedido()
    {
        int fila = tablaProductosRegistrados.getSelectedRow();
        String producto = tablaProductosRegistrados.getValueAt(fila, 0).toString();
        String familia = tablaProductosRegistrados.getValueAt(fila, 1).toString();
        
        String cant = JOptionPane.showInputDialog("Especifique la cantidad de productos a pedir:", "");
        int cantidad = Integer.parseInt(cant);
        
        String sql = "INSERT INTO pedido (nombre, familia, cantidad) VALUES (?,?,?)";
        try {
            PreparedStatement pst = cn.prepareStatement(sql);
            pst.setString(1, producto);
            pst.setString(2, familia);
            pst.setInt(3, cantidad);
            pst.executeUpdate();
//            JOptionPane.showMessageDialog(null, "Se ha insertado el pedido a la cola con éxito.",
//            "Inserción correcta", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            //Logger.getLogger(AgregarProducto.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(HacerPedido.this, "Ya existe este producto en la lista de pedidos.", "Error al intentar agregar al pedido.", JOptionPane.ERROR_MESSAGE);
            
        }
    }
    
    public void eliminarColaPedidos()
    {
        String sql = "DELETE FROM pedido";
        try {
            
            PreparedStatement pst = (PreparedStatement) cn.prepareStatement(sql);
            pst.executeUpdate();
            
        } catch (SQLException ex) {
            //Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
        }
    }
    
    public void pedidoDuplicado(String nombre)
    {
        String sql = "SELECT nombre from pedido WHERE nombre = '"+nombre+"'";
        
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            if(rs.next())
            {
//                JOptionPane.showMessageDialog(null, "Bienvenido.");
//                Principal ventanaPrincipal = new Principal();
//                ventanaPrincipal.setVisible(true);
                JOptionPane.showMessageDialog(HacerPedido.this, "Ya existe un producto con este nombre.", "Error al intentar agregar.", JOptionPane.ERROR_MESSAGE);
            
            }else
            {
                agregarPedido();
            }
        } catch (SQLException ex) {
            Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Panel_general = new javax.swing.JPanel();
        Panel_busqueda = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        Busqueda_producto = new javax.swing.JPanel();
        txtBusqueda = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        Panel_listado = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        Panel_pedido = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaPedidos = new javax.swing.JTable();
        Panel_productos = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaProductosRegistrados = new JTable(){

            public boolean isCellEditable(int rowIndex, int colIndex) {

                return false; //Las celdas no son editables.

            }
        };
        jButton1 = new javax.swing.JButton();
        btnPedido = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        Panel_general.setBackground(new java.awt.Color(255, 255, 255));

        Panel_busqueda.setBackground(new java.awt.Color(191, 54, 12));

        jLabel8.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Búsqueda de un producto existente");

        javax.swing.GroupLayout Panel_busquedaLayout = new javax.swing.GroupLayout(Panel_busqueda);
        Panel_busqueda.setLayout(Panel_busquedaLayout);
        Panel_busquedaLayout.setHorizontalGroup(
            Panel_busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel_busquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        Panel_busquedaLayout.setVerticalGroup(
            Panel_busquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_busquedaLayout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addContainerGap())
        );

        Busqueda_producto.setBackground(new java.awt.Color(255, 255, 255));
        Busqueda_producto.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(191, 54, 12), 2, true), "Ingrese producto a buscar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(191, 54, 12))); // NOI18N

        txtBusqueda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBusquedaActionPerformed(evt);
            }
        });
        txtBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBusquedaKeyReleased(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/busqueda/search.png"))); // NOI18N

        javax.swing.GroupLayout Busqueda_productoLayout = new javax.swing.GroupLayout(Busqueda_producto);
        Busqueda_producto.setLayout(Busqueda_productoLayout);
        Busqueda_productoLayout.setHorizontalGroup(
            Busqueda_productoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Busqueda_productoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        Busqueda_productoLayout.setVerticalGroup(
            Busqueda_productoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Busqueda_productoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(Busqueda_productoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(txtBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        Panel_listado.setBackground(new java.awt.Color(191, 54, 12));

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Productos registrados                Pedido a realizar");

        javax.swing.GroupLayout Panel_listadoLayout = new javax.swing.GroupLayout(Panel_listado);
        Panel_listado.setLayout(Panel_listadoLayout);
        Panel_listadoLayout.setHorizontalGroup(
            Panel_listadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel_listadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(401, Short.MAX_VALUE))
        );
        Panel_listadoLayout.setVerticalGroup(
            Panel_listadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_listadoLayout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addContainerGap())
        );

        Panel_pedido.setBackground(new java.awt.Color(255, 255, 255));
        Panel_pedido.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(191, 54, 12), 2, true), "Pedido de productos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(191, 54, 12))); // NOI18N

        tablaPedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tablaPedidos);

        javax.swing.GroupLayout Panel_pedidoLayout = new javax.swing.GroupLayout(Panel_pedido);
        Panel_pedido.setLayout(Panel_pedidoLayout);
        Panel_pedidoLayout.setHorizontalGroup(
            Panel_pedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_pedidoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        Panel_pedidoLayout.setVerticalGroup(
            Panel_pedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_pedidoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        Panel_productos.setBackground(new java.awt.Color(255, 255, 255));
        Panel_productos.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(191, 54, 12), 2, true), "Productos registrados", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(191, 54, 12))); // NOI18N

        tablaProductosRegistrados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tablaProductosRegistrados);

        javax.swing.GroupLayout Panel_productosLayout = new javax.swing.GroupLayout(Panel_productos);
        Panel_productos.setLayout(Panel_productosLayout);
        Panel_productosLayout.setHorizontalGroup(
            Panel_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 348, Short.MAX_VALUE)
            .addGroup(Panel_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_productosLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        Panel_productosLayout.setVerticalGroup(
            Panel_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 432, Short.MAX_VALUE)
            .addGroup(Panel_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Panel_productosLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/home/home (2).png"))); // NOI18N
        jButton1.setBorderPainted(false);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/home/home (1).png"))); // NOI18N
        jButton1.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/home/home.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnPedido.setText(">>");
        btnPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedidoActionPerformed(evt);
            }
        });

        jButton2.setText("+");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Panel_generalLayout = new javax.swing.GroupLayout(Panel_general);
        Panel_general.setLayout(Panel_generalLayout);
        Panel_generalLayout.setHorizontalGroup(
            Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel_busqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_generalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Busqueda_producto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(Panel_generalLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(Panel_generalLayout.createSequentialGroup()
                        .addComponent(Panel_productos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnPedido, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(Panel_pedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(Panel_listado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_generalLayout.setVerticalGroup(
            Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel_generalLayout.createSequentialGroup()
                .addComponent(Panel_busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Busqueda_producto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Panel_listado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel_generalLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Panel_productos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Panel_pedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(Panel_generalLayout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(jButton2)
                        .addGap(142, 142, 142)
                        .addComponent(btnPedido)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel_general, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel_general, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        new Principal().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        new Principal().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtBusquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBusquedaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBusquedaActionPerformed

    private void txtBusquedaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBusquedaKeyReleased
        // TODO add your handling code here:
        mostrarProductos(txtBusqueda.getText().toString());
    }//GEN-LAST:event_txtBusquedaKeyReleased

    private void btnPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedidoActionPerformed
        // TODO add your handling code here:
        agregarPedido();
        mostrarPedidos();
    }//GEN-LAST:event_btnPedidoActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        eliminarColaPedidos();
        mostrarPedidos();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HacerPedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HacerPedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HacerPedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HacerPedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HacerPedido().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Busqueda_producto;
    private javax.swing.JPanel Panel_busqueda;
    private javax.swing.JPanel Panel_general;
    private javax.swing.JPanel Panel_listado;
    private javax.swing.JPanel Panel_pedido;
    private javax.swing.JPanel Panel_productos;
    private javax.swing.JButton btnPedido;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tablaPedidos;
    private javax.swing.JTable tablaProductosRegistrados;
    private javax.swing.JTextField txtBusqueda;
    // End of variables declaration//GEN-END:variables
}
