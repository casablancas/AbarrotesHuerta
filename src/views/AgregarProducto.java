/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import DB.ConexionBD;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author alejandro
 */
public class AgregarProducto extends javax.swing.JFrame {

    /**
     * Creates new form NuevoProducto
     */
    public AgregarProducto() {
        initComponents();
        this.setTitle("Agregar nuevo producto");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        txtProducto.grabFocus();
        optVinosLicores.setSelected(true);
        btnAgregarProducto.setEnabled(false);
        toolTips();
    }
    
    //Creamos la instancia 'con' de tipo ConexionBD
        ConexionBD cc = new ConexionBD();
        Connection cn = cc.conectar();
        
        String proveedor = "";
        
    public void toolTips()
    {
        txtProducto.setToolTipText("Introduzca el nombre del producto.");
        btnAgregarProducto.setToolTipText("Presione para guardar cambios.");
        btnHome.setToolTipText("Regresar al Menú Principal.");
    }
    
    public void getProveedor(String comboProveedor)
    {
        proveedor = comboProveedor;
    }
    
    
    public void agregarProducto()
    {
        String familia = null;
        
        if (optVinosLicores.isSelected())
            familia = "VINOS Y LICORES";
        else if (optChilesSemillas.isSelected())
            familia = "CHILES Y SEMILLAS";
        else if (optDulceria.isSelected())
            familia = "DULCERIA";
        else if (optMateriasPrimas.isSelected())
            familia = "MATERIAS PRIMAS";
        else if (optAbarrotes.isSelected())
            familia = "ABARROTES";
        else if (optJarcieria.isSelected())
            familia = "JARCIERIA";
        else if (optCarnesFriasLacteos.isSelected())
            familia = "CARNES FRIAS Y LACTEOS";
        else if (optPerfumeria.isSelected())
            familia = "PERFUMERIA";
        else if (optFarmaceuticos.isSelected())
            familia = "FARMACEUTICOS";
        else if (optPapeleria.isSelected())
            familia = "PAPELERIA";
        else if (optAutomotriz.isSelected())
            familia = "AUTOMOTRIZ";
        
        String sql = "INSERT INTO producto (nombre, familia) VALUES (?,?)";
        
        try {
            PreparedStatement pst = cn.prepareStatement(sql);
            pst.setString(1, (txtProducto.getText()).toUpperCase());
            pst.setString(2, familia);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Se ha insertado el producto con éxito.",
            "Inserción correcta", JOptionPane.INFORMATION_MESSAGE);
            optVinosLicores.setSelected(true);
            txtProducto.setText("");
            txtProducto.grabFocus();
            //Capturamos los valores del jcombobox.
            getProveedor(jComboBoxProveedor.getSelectedItem().toString());
            System.out.println("Valor jcombobox: " +proveedor + " o: " + jComboBoxProveedor.getSelectedItem().toString());
        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(null, "No se ha podido insertar el producto porque ya existe.",
//            "Producto ya existente", JOptionPane.INFORMATION_MESSAGE);
            JOptionPane.showMessageDialog(AgregarProducto.this, "Ya existe un producto con este nombre o no ha agregado un nombre al producto.", 
                    "Error al intentar agregar", JOptionPane.ERROR_MESSAGE);
            System.err.println(ex);
            //txtProducto.setText("");
            txtProducto.grabFocus();
            //Logger.getLogger(AgregarProducto.class.getName()).log(Level.SEVERE, null, ex);
        }
        cc.desconectar();
    }
    
    /*public void productoDuplicado(String nombre)
    {
        String sql = "SELECT nombre from pedido WHERE nombre = '"+nombre+"'";
        
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            if(rs.next())
            {
                JOptionPane.showMessageDialog(AgregarProducto.this, "Ya existe un producto con este nombre.", 
                        "Error al intentar agregar", JOptionPane.ERROR_MESSAGE);
            
            }else
            {
                agregarProducto();
            }
        } catch (SQLException ex) {
            Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
    public void activaBoton()
    {
        if(txtProducto.getText().equals(""))
            btnAgregarProducto.setEnabled(false);
        else
            btnAgregarProducto.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupFamilias = new javax.swing.ButtonGroup();
        Panel_general = new javax.swing.JPanel();
        Ingreso_datos = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        optAbarrotes = new javax.swing.JRadioButton();
        optDulceria = new javax.swing.JRadioButton();
        optFarmaceuticos = new javax.swing.JRadioButton();
        optVinosLicores = new javax.swing.JRadioButton();
        optJarcieria = new javax.swing.JRadioButton();
        optChilesSemillas = new javax.swing.JRadioButton();
        optCarnesFriasLacteos = new javax.swing.JRadioButton();
        optPerfumeria = new javax.swing.JRadioButton();
        optPapeleria = new javax.swing.JRadioButton();
        optMateriasPrimas = new javax.swing.JRadioButton();
        optAutomotriz = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        txtProducto = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jComboBoxProveedor = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnHome = new javax.swing.JButton();
        btnAgregarProducto = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getResource("/views/store.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        Panel_general.setBackground(new java.awt.Color(255, 255, 255));

        Ingreso_datos.setBackground(new java.awt.Color(255, 255, 255));
        Ingreso_datos.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(191, 54, 12), 2, true));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(191, 54, 12), 2, true), "Seleccione la famila", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(191, 54, 12))); // NOI18N

        optAbarrotes.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupFamilias.add(optAbarrotes);
        optAbarrotes.setText("Abarrotes");
        optAbarrotes.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        optDulceria.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupFamilias.add(optDulceria);
        optDulceria.setText("Dulcería");
        optDulceria.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        optDulceria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optDulceriaActionPerformed(evt);
            }
        });

        optFarmaceuticos.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupFamilias.add(optFarmaceuticos);
        optFarmaceuticos.setText("Farmacéuticos");
        optFarmaceuticos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        optVinosLicores.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupFamilias.add(optVinosLicores);
        optVinosLicores.setText("Vinos y licores");
        optVinosLicores.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        optVinosLicores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optVinosLicoresActionPerformed(evt);
            }
        });

        optJarcieria.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupFamilias.add(optJarcieria);
        optJarcieria.setText("Jarciería");
        optJarcieria.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        optChilesSemillas.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupFamilias.add(optChilesSemillas);
        optChilesSemillas.setText("Chiles y semillas");
        optChilesSemillas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        optChilesSemillas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optChilesSemillasActionPerformed(evt);
            }
        });

        optCarnesFriasLacteos.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupFamilias.add(optCarnesFriasLacteos);
        optCarnesFriasLacteos.setText("Carnes frías y lácteos");
        optCarnesFriasLacteos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        optCarnesFriasLacteos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optCarnesFriasLacteosActionPerformed(evt);
            }
        });

        optPerfumeria.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupFamilias.add(optPerfumeria);
        optPerfumeria.setText("Perfumería");
        optPerfumeria.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        optPapeleria.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupFamilias.add(optPapeleria);
        optPapeleria.setText("Papelería");
        optPapeleria.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        optMateriasPrimas.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupFamilias.add(optMateriasPrimas);
        optMateriasPrimas.setText("Materias primas");
        optMateriasPrimas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        optAutomotriz.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroupFamilias.add(optAutomotriz);
        optAutomotriz.setText("Automotriz");
        optAutomotriz.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(optCarnesFriasLacteos)
                    .addComponent(optVinosLicores)
                    .addComponent(optChilesSemillas)
                    .addComponent(optMateriasPrimas))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addComponent(optFarmaceuticos)
                                .addGap(18, 18, 18))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(optDulceria)
                                    .addComponent(optPerfumeria))
                                .addGap(39, 39, 39)))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(optPapeleria)
                            .addComponent(optJarcieria)
                            .addComponent(optAbarrotes)))
                    .addComponent(optAutomotriz))
                .addGap(18, 18, 18))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(optAbarrotes)
                    .addComponent(optDulceria)
                    .addComponent(optVinosLicores))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(optFarmaceuticos)
                    .addComponent(optJarcieria)
                    .addComponent(optChilesSemillas))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(optPerfumeria)
                    .addComponent(optPapeleria)
                    .addComponent(optCarnesFriasLacteos))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(optMateriasPrimas)
                    .addComponent(optAutomotriz))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(191, 54, 12), 2, true), "Nombre del producto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(191, 54, 12))); // NOI18N

        txtProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtProductoKeyTyped(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtProductoKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(191, 54, 12), 2, true), "Seleccione el proveedor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(191, 54, 12))); // NOI18N

        jComboBoxProveedor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Proveedor 1", "Proveedor 2", "Proveedor 3", "Proveedor 4", "Proveedor 5", "Proveedor 6", "Proveedor 7", "Proveedor 8", "Proveedor 9", "Proveedor 10", "Proveedor 11" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBoxProveedor, 0, 278, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(jComboBoxProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout Ingreso_datosLayout = new javax.swing.GroupLayout(Ingreso_datos);
        Ingreso_datos.setLayout(Ingreso_datosLayout);
        Ingreso_datosLayout.setHorizontalGroup(
            Ingreso_datosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Ingreso_datosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Ingreso_datosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        Ingreso_datosLayout.setVerticalGroup(
            Ingreso_datosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Ingreso_datosLayout.createSequentialGroup()
                .addGroup(Ingreso_datosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(Ingreso_datosLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel6.setBackground(new java.awt.Color(191, 54, 12));

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Registro de un nuevo producto");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        btnHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/home/store (2).png"))); // NOI18N
        btnHome.setBorderPainted(false);
        btnHome.setContentAreaFilled(false);
        btnHome.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });

        btnAgregarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save/diskette.png"))); // NOI18N
        btnAgregarProducto.setBorderPainted(false);
        btnAgregarProducto.setContentAreaFilled(false);
        btnAgregarProducto.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAgregarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarProductoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Panel_generalLayout = new javax.swing.GroupLayout(Panel_general);
        Panel_general.setLayout(Panel_generalLayout);
        Panel_generalLayout.setHorizontalGroup(
            Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel_generalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Ingreso_datos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(Panel_generalLayout.createSequentialGroup()
                        .addComponent(btnHome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAgregarProducto)))
                .addContainerGap())
            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        Panel_generalLayout.setVerticalGroup(
            Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel_generalLayout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Ingreso_datos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnHome)
                    .addComponent(btnAgregarProducto))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Panel_general, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel_general, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        new Principal().setVisible(true);
        this.dispose();
        cc.desconectar();
    }//GEN-LAST:event_formWindowClosing

    private void optDulceriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optDulceriaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_optDulceriaActionPerformed

    private void optVinosLicoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optVinosLicoresActionPerformed
        // TODO add your handling code here:
        if(evt.getSource() == optVinosLicores)
        {
            setBackground(Color.GREEN);
        }
    }//GEN-LAST:event_optVinosLicoresActionPerformed

    private void optChilesSemillasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optChilesSemillasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_optChilesSemillasActionPerformed

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        // TODO add your handling code here:
        new Principal().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnHomeActionPerformed

    private void btnAgregarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarProductoActionPerformed
        // TODO add your handling code here:
        String producto = txtProducto.getText();
        if(!producto.equals(""))
            agregarProducto();
        else
            JOptionPane.showMessageDialog(null, "No se puede insertar un producto vacío.",
            "Ha ocurrido un error", JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_btnAgregarProductoActionPerformed

    private void txtProductoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProductoKeyReleased
        // TODO add your handling code here:
        activaBoton();
    }//GEN-LAST:event_txtProductoKeyReleased

    private void txtProductoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProductoKeyTyped
        // TODO add your handling code here:
        char c = evt.getKeyChar(); 
        
        //Convertimos cualquier letra ingresada en el textfield en mayúscula.
        if(Character.isLowerCase(c))
        {
            evt.setKeyChar(Character.toUpperCase(c));
            if(Character.isDigit(c) || Character.isLetter(c))
            {
                btnAgregarProducto.setEnabled(true);
//              getToolkit().beep();
            }
            else
                btnAgregarProducto.setEnabled(false);
        }
    }//GEN-LAST:event_txtProductoKeyTyped

    private void optCarnesFriasLacteosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optCarnesFriasLacteosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_optCarnesFriasLacteosActionPerformed

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
            java.util.logging.Logger.getLogger(AgregarProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AgregarProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AgregarProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AgregarProducto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AgregarProducto().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Ingreso_datos;
    private javax.swing.JPanel Panel_general;
    private javax.swing.JButton btnAgregarProducto;
    private javax.swing.JButton btnHome;
    private javax.swing.ButtonGroup buttonGroupFamilias;
    private javax.swing.JComboBox<String> jComboBoxProveedor;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JRadioButton optAbarrotes;
    private javax.swing.JRadioButton optAutomotriz;
    private javax.swing.JRadioButton optCarnesFriasLacteos;
    private javax.swing.JRadioButton optChilesSemillas;
    private javax.swing.JRadioButton optDulceria;
    private javax.swing.JRadioButton optFarmaceuticos;
    private javax.swing.JRadioButton optJarcieria;
    private javax.swing.JRadioButton optMateriasPrimas;
    private javax.swing.JRadioButton optPapeleria;
    private javax.swing.JRadioButton optPerfumeria;
    private javax.swing.JRadioButton optVinosLicores;
    private javax.swing.JTextField txtProducto;
    // End of variables declaration//GEN-END:variables
}
