/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import DB.ConexionBD;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.ImageIcon;

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
        //btnNuevoPedido.setEnabled(false);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                //Nuevo pedido
            }
        });
    }
    
    public void nuevoPedido(){
        
        //Si la lista de pedidos contiene algo.
        if(!listaPedidosVacia())
        {
            if (JOptionPane.showConfirmDialog(rootPane, "¿Desea crear un nuevo pedido? Los datos contenidos en el pedido actual serán eliminados.",
                    "Nuevo pedido.", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                eliminarColaPedidos();
                mostrarPedidos();
            }
        //Si a lista de pedidos está vacía.
        }else
            JOptionPane.showMessageDialog(null, "No hay ningún producto en la lista de pedidos.",
                "Lista de pedidos vacía", JOptionPane.ERROR_MESSAGE);
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
                System.out.println("Se ha encontrado algo en la base de datos.");
                //Regresa el puntero al inicio para no perder el primer dato de la tabla.
                rs.beforeFirst();
            } 
            else 
            {
                //si entra a este else quiere decir que no obtuviste ningun registro 
                //o sea que el ResultSet fue nulo.
                System.err.println("No se ha encontrado nada en la base de datos");
//                JOptionPane.showMessageDialog(null, "No hay ningún producto con ese nombre en la base de datos.",
//                "No se encontró el elemento", JOptionPane.INFORMATION_MESSAGE);
                txtBusqueda.grabFocus();
                cc.desconectar();
            }
            
            while(rs.next())
                {
                    productos[0] = rs.getString("nombre");
                    productos[1] = rs.getString("familia");
                    model.addRow(productos);
                }
                tablaProductosRegistrados.setModel(model);
                cc.desconectar();
            
        } catch (SQLException ex) {
            //Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
//            JOptionPane.showMessageDialog(null, "Se ha agotado el tiempo de espera para conectarse a la base de datos.",
//            "Advertencia", JOptionPane.WARNING_MESSAGE);
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
            
            //Reviso si hay algo en la lista de pedidos para activar/desactivar los botones.
            if(rs.next())
            {
                btnNuevoPedido.setEnabled(true);
                btnGeneraPDF.setEnabled(true);
                btnImprimePedido.setEnabled(true);
                //Para no perder el primer dato de la tabla.
                rs.beforeFirst();
            }
            else
            {
                btnNuevoPedido.setEnabled(false);
                btnGeneraPDF.setEnabled(false);
                btnImprimePedido.setEnabled(false);
            }
            
            
            while(rs.next())
                {
                    btnNuevoPedido.setEnabled(true);
                    pedidos[0] = rs.getString("cantidad");
                    pedidos[1] = rs.getString("nombre");
                    pedidos[2] = rs.getString("familia");
                    model.addRow(pedidos);
                }
                tablaPedidos.setModel(model);
                cc.desconectar();
            
        } catch (SQLException ex) {
            //Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
//            JOptionPane.showMessageDialog(null, "Se ha agotado el tiempo de espera para conectarse a la base de datos.",
//            "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    //Agrega los productos a la lista de pedidos.
    public void agregarPedido()
    {
        int fila = tablaProductosRegistrados.getSelectedRow();
        String producto = tablaProductosRegistrados.getValueAt(fila, 0).toString();
        String familia = tablaProductosRegistrados.getValueAt(fila, 1).toString();
        
        String sql = "INSERT INTO pedido (nombre, familia, cantidad) VALUES (?,?,?)";
        try {
            String cant = JOptionPane.showInputDialog("Especifique la cantidad de productos a pedir:", "");
            int cantidad = Integer.parseInt(cant);
            
            PreparedStatement pst = cn.prepareStatement(sql);
            pst.setString(1, producto);
            pst.setString(2, familia);
            pst.setInt(3, cantidad);
            pst.executeUpdate();
//            JOptionPane.showMessageDialog(null, "Se ha insertado el pedido a la cola con éxito.",
//            "Inserción correcta", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            //Logger.getLogger(AgregarProducto.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(HacerPedido.this, "Ya existe este producto en la lista de pedidos.", 
                    "Error al intentar agregar al pedido", JOptionPane.ERROR_MESSAGE);
            
        }
        cc.desconectar();
    }
    
    //Elimina la lista actual de los pedidos.
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
        cc.desconectar();
    }
    
    //Verifica que no se dupliquen los productos en los pedidos.
    public void pedidoDuplicado(String nombre)
    {
        String sql = "SELECT nombre from pedido WHERE nombre = '"+nombre+"'";
        
        try {
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            if(rs.next())
            {
                JOptionPane.showMessageDialog(HacerPedido.this, "Ya existe un producto con este nombre.", 
                        "Error al intentar agregar", JOptionPane.ERROR_MESSAGE);
            
            }else
            {
                agregarPedido();
            }
        } catch (SQLException ex) {
            Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
        }
        cc.desconectar();
    }
    
    public boolean listaPedidosVacia()
    {
        //Creamos nuestra sentencia SQL.
        String sql = "SELECT nombre, familia, cantidad FROM pedido";
        
        Statement st;
        try{
            st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            //Validamos que el resultset contenga datos o esté vacío.
            if (rs != null && rs.next() ) 
            { 
                //codigo para tratar al conjunto de registros o al registro obtenido 
                System.out.println("Se ha encontrado algo en la base de datos.");
                //Regresa el puntero al inicio para no perder el primer dato de la tabla.
                rs.beforeFirst();
                return false;
            } 
            else 
            {
                //si entra a este else quiere decir que no obtuviste ningun registro 
                //o sea que el ResultSet fue nulo.
                System.err.println("No se ha encontrado nada en la base de datos");
//                JOptionPane.showMessageDialog(null, "No hay ningún producto en la lista de pedidos.",
//                "Lista de pedidos vacía", JOptionPane.ERROR_MESSAGE);
                return true;
            }
            
        } catch (SQLException ex) {
            //Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
        }
        return true;
    }
    
    //Función alternativa para generar PDF de JTable.
    private void print() {
    Document document = new Document(PageSize.A4.rotate());
    String username = System.getProperty("user.name");
            
            //String filepath = "/Users/alejandro/NetBeansProjects/QRGenerator/qrCode.png";
            String filepath = "/Users/"+username+"/Desktop/jajasaludosss.pdf";
    try {
      PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filepath));

      document.open();
      PdfContentByte cb = writer.getDirectContent();

      cb.saveState();
      Graphics2D g2 = cb.createGraphicsShapes(500, 500);

      Shape oldClip = g2.getClip();
      g2.clipRect(0, 0, 500, 500);

      tablaPedidos.print(g2);
      g2.setClip(oldClip);

      g2.dispose();
      cb.restoreState();
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
    document.close();
  }
    
    //Función imprime JTable con los datos.
    public void printPedido()
    {
        Calendar cal = Calendar.getInstance();
        String dia = cal.get(Calendar.DATE)+"/";
        //String mes = ""+cal.get(Calendar.MONTH);
        String mes = new SimpleDateFormat("MMMM").format(cal.getTime());
        String año = "/"+cal.get(Calendar.YEAR);
        //String fecha = cal.get(Calendar.DATE)+"/"+cal.get((Calendar.MONTH))+"/"+cal.get(Calendar.YEAR);
        String hora = cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);
        
        //Obtenemos el mes con el nombre completo.
        System.out.println(new SimpleDateFormat("MMMM").format(cal.getTime()));
        //Obtenemos el mes con las primeras 3 letras.
        System.out.println(new SimpleDateFormat("MMM").format(cal.getTime()));
        //Obtenemos el mes con 2 digitos.
        System.out.println(new SimpleDateFormat("MM").format(cal.getTime()));
        //Obtenemos el mes con 1 dígito.
        System.out.println(new SimpleDateFormat("M").format(cal.getTime()));
        
        System.out.println("Abarrotes Huerta: "+dia+(mes+1)+año+"  "+hora);
        
        try {
            MessageFormat headerFormat = new MessageFormat("Lista de pedidos de los productos:\n");
            MessageFormat footerFormat = new MessageFormat("Abarrotes Huerta: " +dia+mes+año+"  "+hora);
            //tablaDatos.print(JTable.PrintMode.NORMAL, headerFormat, footerFormat);
            //tablaDatos.print(JTable.PrintMode.valueOf(lastid));
            tablaPedidos.print(JTable.PrintMode.NORMAL, headerFormat, footerFormat, rootPaneCheckingEnabled, null, rootPaneCheckingEnabled);
        } catch (PrinterException ex) {
            //Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Algo ha salido mal, no se puede imprimir el pedido.");
        }
    }
    
    //Función genera PDF de JTable.
    public void generatePDF(String nombreArchivo) throws DocumentException, FileNotFoundException, IOException
    { 
            Document document = new Document();
            PdfWriter writer;
            
            String username = System.getProperty("user.name");
            
            //String filepath = "/Users/alejandro/NetBeansProjects/QRGenerator/qrCode.png";
            String filepath = "/Users/"+username+"/Desktop/"+nombreArchivo+".pdf";

            writer = PdfWriter.getInstance(document, new FileOutputStream(filepath));

            // writer = PdfWriter.getInstance(document, new
            // FileOutputStream("my_jtable_fonts.pdf"));

            document.open();
            PdfContentByte cb = writer.getDirectContent();

            PdfTemplate tp = cb.createTemplate(500, 500);
            Graphics2D g2;

            g2 = tp.createGraphicsShapes(500, 500);

            // g2 = tp.createGraphics(500, 500);
            tablaPedidos.print(g2);
            g2.dispose();
            //cb.addTemplate(tp, 30, 300);
            cb.addTemplate(tp, 75, 300);
            
//            //String archivo = System.getProperty("user.dir")+"/qrCode.png";
//            String archivo = System.getProperty(filepath);
//            Desktop d = Desktop.getDesktop();
//            d.open(new File(archivo));

            // step 5: we close the document
            document.close();
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
        btnNuevoPedido = new javax.swing.JButton();
        btnGeneraPDF = new javax.swing.JButton();
        btnImprimePedido = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getResource("/views/store.png")).getImage());
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

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/busqueda/magnifying-glass.png"))); // NOI18N

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
        jLabel3.setText("Asignación de productos a la lista de pedidos");

        javax.swing.GroupLayout Panel_listadoLayout = new javax.swing.GroupLayout(Panel_listado);
        Panel_listado.setLayout(Panel_listadoLayout);
        Panel_listadoLayout.setHorizontalGroup(
            Panel_listadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel_listadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        tablaPedidos.setSelectionBackground(new java.awt.Color(239, 108, 0));
        jScrollPane2.setViewportView(tablaPedidos);

        javax.swing.GroupLayout Panel_pedidoLayout = new javax.swing.GroupLayout(Panel_pedido);
        Panel_pedido.setLayout(Panel_pedidoLayout);
        Panel_pedidoLayout.setHorizontalGroup(
            Panel_pedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_pedidoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                .addContainerGap())
        );
        Panel_pedidoLayout.setVerticalGroup(
            Panel_pedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_pedidoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                .addContainerGap())
        );

        Panel_productos.setBackground(new java.awt.Color(255, 255, 255));
        Panel_productos.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(191, 54, 12), 2, true), "Productos registrados", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(191, 54, 12))); // NOI18N

        tablaProductosRegistrados.setAutoCreateRowSorter(true);
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
        tablaProductosRegistrados.setSelectionBackground(new java.awt.Color(239, 108, 0));
        jScrollPane1.setViewportView(tablaProductosRegistrados);

        javax.swing.GroupLayout Panel_productosLayout = new javax.swing.GroupLayout(Panel_productos);
        Panel_productos.setLayout(Panel_productosLayout);
        Panel_productosLayout.setHorizontalGroup(
            Panel_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 399, Short.MAX_VALUE)
            .addGroup(Panel_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel_productosLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        Panel_productosLayout.setVerticalGroup(
            Panel_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(Panel_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Panel_productosLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/home/home (2).png"))); // NOI18N
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/home/home (1).png"))); // NOI18N
        jButton1.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/home/home.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnPedido.setFont(new java.awt.Font("Lucida Grande", 1, 12)); // NOI18N
        btnPedido.setForeground(new java.awt.Color(85, 139, 47));
        btnPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/greater/cart.png"))); // NOI18N
        btnPedido.setText("Agregar");
        btnPedido.setBorderPainted(false);
        btnPedido.setContentAreaFilled(false);
        btnPedido.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPedido.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPedido.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedidoActionPerformed(evt);
            }
        });

        btnNuevoPedido.setFont(new java.awt.Font("Lucida Grande", 1, 11)); // NOI18N
        btnNuevoPedido.setForeground(new java.awt.Color(13, 71, 161));
        btnNuevoPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/refresh/new-file.png"))); // NOI18N
        btnNuevoPedido.setText("Nuevo pedido");
        btnNuevoPedido.setBorderPainted(false);
        btnNuevoPedido.setContentAreaFilled(false);
        btnNuevoPedido.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNuevoPedido.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNuevoPedido.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNuevoPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoPedidoActionPerformed(evt);
            }
        });

        btnGeneraPDF.setFont(new java.awt.Font("Lucida Grande", 1, 12)); // NOI18N
        btnGeneraPDF.setForeground(new java.awt.Color(183, 28, 28));
        btnGeneraPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/pdf/pdf.png"))); // NOI18N
        btnGeneraPDF.setText("Generar PDF");
        btnGeneraPDF.setBorderPainted(false);
        btnGeneraPDF.setContentAreaFilled(false);
        btnGeneraPDF.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGeneraPDF.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGeneraPDF.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGeneraPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGeneraPDFActionPerformed(evt);
            }
        });

        btnImprimePedido.setFont(new java.awt.Font("Lucida Grande", 1, 12)); // NOI18N
        btnImprimePedido.setForeground(new java.awt.Color(66, 66, 66));
        btnImprimePedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/printer/printer.png"))); // NOI18N
        btnImprimePedido.setText("Imprimir pedido");
        btnImprimePedido.setBorderPainted(false);
        btnImprimePedido.setContentAreaFilled(false);
        btnImprimePedido.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnImprimePedido.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimePedido.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimePedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimePedidoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Panel_generalLayout = new javax.swing.GroupLayout(Panel_general);
        Panel_general.setLayout(Panel_generalLayout);
        Panel_generalLayout.setHorizontalGroup(
            Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel_busqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Panel_listado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_generalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel_generalLayout.createSequentialGroup()
                        .addComponent(Busqueda_producto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNuevoPedido))
                    .addGroup(Panel_generalLayout.createSequentialGroup()
                        .addComponent(Panel_productos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(Panel_pedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(Panel_generalLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnImprimePedido)
                        .addGap(19, 19, 19)
                        .addComponent(btnGeneraPDF)))
                .addContainerGap())
        );
        Panel_generalLayout.setVerticalGroup(
            Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel_generalLayout.createSequentialGroup()
                .addComponent(Panel_busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Busqueda_producto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevoPedido))
                .addGap(18, 18, 18)
                .addComponent(Panel_listado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel_generalLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Panel_productos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Panel_pedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(Panel_generalLayout.createSequentialGroup()
                        .addGap(157, 157, 157)
                        .addComponent(btnPedido)))
                .addGap(18, 18, 18)
                .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(btnGeneraPDF)
                    .addComponent(btnImprimePedido))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        cc.desconectar();
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

    private void btnNuevoPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoPedidoActionPerformed
        // TODO add your handling code here:
        nuevoPedido();
    }//GEN-LAST:event_btnNuevoPedidoActionPerformed

    private void btnGeneraPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGeneraPDFActionPerformed
        // TODO add your handling code here:
//        String nombreArvhivo = JOptionPane.showInputDialog("Ingrese nombre del archivo (sin especificar formato o extensión):", "Nombre del archivo");
//        
//        try {
//            generatePDF(nombreArvhivo);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (DocumentException ex) {
//            Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        print();

        String username = System.getProperty("user.name");
            
        //String filepath = "/Users/alejandro/NetBeansProjects/QRGenerator/qrCode.png";
        String filepath = "/Users/"+username+"/Desktop/jajasaludosss.pdf";

        try {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(filepath));
            doc.open();
            PdfPTable pdfTable = new PdfPTable(tablaPedidos.getColumnCount());
            //adding table headers
            for (int i = 0; i < tablaPedidos.getColumnCount(); i++) {
                pdfTable.addCell(tablaPedidos.getColumnName(i));
            }
            //extracting data from the JTable and inserting it to PdfPTable
            for (int rows = 0; rows < tablaPedidos.getRowCount() - 1; rows++) {
                for (int cols = 0; cols < tablaPedidos.getColumnCount(); cols++) {
                    pdfTable.addCell(tablaPedidos.getModel().getValueAt(rows, cols).toString());

                }
            }
            doc.add(pdfTable);
            doc.close();
            System.out.println("done");
        } catch (DocumentException ex) {
            Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnGeneraPDFActionPerformed

    private void btnImprimePedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimePedidoActionPerformed
        // TODO add your handling code here:
        printPedido();
    }//GEN-LAST:event_btnImprimePedidoActionPerformed

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
    private javax.swing.JButton btnGeneraPDF;
    private javax.swing.JButton btnImprimePedido;
    private javax.swing.JButton btnNuevoPedido;
    private javax.swing.JButton btnPedido;
    private javax.swing.JButton jButton1;
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
