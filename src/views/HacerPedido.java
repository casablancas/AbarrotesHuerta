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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author alejandro
 */
public class HacerPedido extends javax.swing.JFrame {
    
    private final TransferHandler handler = new TableRowTransferHandler();

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
        toolTips();
        btnImprimePedido.setVisible(false);
//        btnPedido.setEnabled(false);
//        llenadoComboBox();
//        jButton1.setVisible(false);
        
//        tablaProductosRegistrados.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//        tablaProductosRegistrados.setTransferHandler(handler);
//        tablaPedidos.setDropMode(DropMode.INSERT_ROWS);
//        tablaProductosRegistrados.setDragEnabled(true);
//        tablaPedidos.setFillsViewportHeight(true);

        tablaProductosRegistrados.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tablaProductosRegistrados.setDragEnabled(true);
        tablaPedidos.setDropMode(DropMode.INSERT_ROWS);
        tablaPedidos.setTransferHandler(new TS());
        
        //Disable row Cut, Copy, Paste
//        ActionMap map = tablaProductosRegistrados.getActionMap();
//        Action dummy = new AbstractAction() {
//            @Override public void actionPerformed(ActionEvent e) { /* Dummy action */ }
//        };
//        map.put(TransferHandler.getCutAction().getValue(Action.NAME),   dummy);
//        map.put(TransferHandler.getCopyAction().getValue(Action.NAME),  dummy);
//        map.put(TransferHandler.getPasteAction().getValue(Action.NAME), dummy);
        
        //btnNuevoPedido.setEnabled(false);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                //Nuevo pedido
            }
        });
    }
    
    public void toolTips()
    {
        btnPedido.setToolTipText("Elija un elemento de la lista de PRODUCTOS registrados y despúes de clic en este botón para agregarlo a lista de pedidos.");
        txtBusqueda.setToolTipText("Introduzca los caracteres a buscar.");
        btnHome.setToolTipText("Regresar al Menú Principal.");
        btnGeneraPDF.setToolTipText("Presione para generar un documento PDF.");
        btnImprimePedido.setToolTipText("Presione para impresión rápida del pedido.");
        btnNuevoPedido.setToolTipText("Crear un nuevo pedido (se eliminarán los datos del pedido actual).");
        btnPedidoRemove.setToolTipText("Elija un elemento de la lista de PEDIDOS y despúes de clic en este botón para eliminarlo de la lista.");
    }
    
    //Confirmación eliminar producto de la lista.
    public void confirmacion(){
        if (JOptionPane.showConfirmDialog(rootPane, "¿Realmente desea eliminar este elemento?",
                "Confirmación para eliminar producto de la base de datos", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            //Eliminamos el producto seleccionado de la tabla.
            eliminarProducto();
            //showTable();
        }
    }
    
    //Confirmación eliminar pedido de la lista.
    public void confirmacion2()
    {
        if (JOptionPane.showConfirmDialog(rootPane, "¿Realmente desea eliminar este elemento?",
                "Confirmación para eliminar producto de la lista de pedidos", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            //Eliminamos el pedido seleccionado de la tabla.
            eliminarElementoPedido();
        }
    }
    
    public void nuevoPedido(){
        
        //Si la lista de pedidos contiene algo.
        if(!listaPedidosVacia())
        {
            if (JOptionPane.showConfirmDialog(rootPane, "¿Desea crear un nuevo pedido?\nLos datos contenidos en el pedido actual serán eliminados.",
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
        
//        String producto;
//        String familia;
//        String proveedor1;
//        String proveedor2;
    
    //Realiza consulta de los productos.
    public void mostrarProductos(String valor)
    {
        tablaProductosRegistrados.clearSelection();
        tablaPedidos.clearSelection();
        
        //btnPedido.setEnabled(false);
        
        DefaultTableModel model;
        
        //Encabezados de la tabla.
        String [] titulos = {"Nombre", "Familia", "Prov. 1", "Prov. 2"};
        String [] productos = new String[4];
        
        //Creamos nuestra sentencia SQL.
        String sql = "SELECT nombre, familia, proveedor1, proveedor2 FROM producto WHERE nombre LIKE '%"+valor+"%' ORDER BY familia";

        //Cambio de Query, el usuario quiere que solamente aparezca el nombre del producto.
//        String sql = "SELECT nombre FROM producto WHERE nombre LIKE '%"+valor+"%' ORDER BY nombre";
        
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
                    productos[2] = rs.getString("proveedor1");
                    productos[3] = rs.getString("proveedor2");
                    model.addRow(productos);
                }
                tablaProductosRegistrados.setModel(model);
                tablaProductosRegistrados.getColumn("Nombre").setMinWidth(250);
                tablaProductosRegistrados.getColumn("Familia").setMaxWidth(1);
                tablaProductosRegistrados.getColumn("Prov. 1").setMaxWidth(1);
                tablaProductosRegistrados.getColumn("Prov. 2").setMaxWidth(1);
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
//        llenadoComboBox();
        
        tablaProductosRegistrados.clearSelection();
        
        DefaultTableModel model;
        
        //Encabezados de la tabla.
        String [] titulos = {"Nombre", "Familia", "Prov. 1", "Prov. 2"};
        String [] pedidos = new String[4];
        
        //Creamos nuestra sentencia SQL.
        String sql = "SELECT nombre, familia, proveedor1, proveedor2 FROM pedido ORDER BY familia";
        
        //Cambio de Query, el usuario quiere que solamente aparezca el nombre del producto.
//        String sql = "SELECT nombre FROM pedido";
        
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
//                    pedidos[0] = rs.getString("cantidad");
                    pedidos[0] = rs.getString("nombre");
                    pedidos[1] = rs.getString("familia");
                    pedidos[2] = rs.getString("proveedor1");
                    pedidos[3] = rs.getString("proveedor2");
                    model.addRow(pedidos);
                }
                tablaPedidos.setModel(model);
                tablaPedidos.getColumn("Nombre").setMinWidth(250);
                tablaPedidos.getColumn("Familia").setMaxWidth(1);
                //tablaPedidos.getColumn("Prov. 1").setMaxWidth(1);
                //tablaPedidos.getColumn("Prov. 2").setMaxWidth(1);
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
        if(fila>=0){
//            btnPedido.setEnabled(true);
            String producto = (String) tablaProductosRegistrados.getValueAt(fila, 0);
            String familia = (String) tablaProductosRegistrados.getValueAt(fila, 1);
            String proveedor1 = (String) tablaProductosRegistrados.getValueAt(fila, 2);
            String proveedor2 = (String) tablaProductosRegistrados.getValueAt(fila, 3);

            String sql = "INSERT INTO pedido (nombre, familia, proveedor1, proveedor2) VALUES (?,?,?,?)";
                try {
//                    String cant = JOptionPane.showInputDialog("Especifique la cantidad de productos a pedir:", "");
//                    int cantidad = Integer.parseInt(cant);
                    
//                    if(proveedor1.equals(""))
//                        proveedor1="";
//                    
//                    if(proveedor2.equals(""))
//                        proveedor2="";

                    PreparedStatement pst = cn.prepareStatement(sql);
                    pst.setString(1, producto);
                    pst.setString(2, familia);
                    pst.setString(3, proveedor1);
                    pst.setString(4, proveedor2);
//                    pst.setInt(3, cantidad);
                    pst.executeUpdate();
        //            JOptionPane.showMessageDialog(null, "Se ha insertado el pedido a la cola con éxito.",
        //            "Inserción correcta", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    //Logger.getLogger(AgregarProducto.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(HacerPedido.this, "Ya existe este producto en la lista de pedidos.", 
                            "Error al intentar agregar al pedido", JOptionPane.ERROR_MESSAGE);

                }
                cc.desconectar();
            }else
                JOptionPane.showMessageDialog(null, "Debe elegir un elemento de la lista de productos que desee agregar a la lista de pedidos.", "No se seleccionó ningún elemento.", JOptionPane.ERROR_MESSAGE);
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
    
    //Elimina los pedidos que se seleccionan por proveedor 1
    public void eliminaColaPedidoProveedor1(String valor)
    {
        String sql = "DELETE FROM pedido WHERE proveedor1 LIKE '%"+valor+"%' OR proveedor2 LIKE '%"+valor+"%' ";
        try{
            PreparedStatement pst = (PreparedStatement) cn.prepareStatement(sql);
            pst.executeUpdate();
            mostrarPedidos();
            
        }catch (SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        }
        cc.desconectar();
    }
    
    //Elimina los pedidos que se seleccionan por proveedor 2
    public void eliminaColaPedidoProveedor2(String valor)
    {
        String sql = "DELETE FROM pedido WHERE proveedor2 LIKE '%"+valor+"%' ";
        try{
            PreparedStatement pst = (PreparedStatement) cn.prepareStatement(sql);
            pst.executeUpdate();
            mostrarPedidos();
            
        }catch (SQLException ex){
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
    
    public void generaPDFconBordes(String nombreArchivo)
    {
        String username = System.getProperty("user.name");
            
        //String filepath = "/Users/alejandro/NetBeansProjects/QRGenerator/qrCode.png";
        String filepath = "/Users/"+username+"/Desktop/"+nombreArchivo+".pdf";

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
    }
    
    //Última función que se estaba utilizando
    public void generatePDF2()
    {
        String nombreArvhivo = JOptionPane.showInputDialog("Ingrese nombre del archivo (sin especificar formato o extensión):", "Nombre del archivo");
        
        try {
            generatePDF(nombreArvhivo);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void generateJasperReport() throws FileNotFoundException
    {
        String username = System.getProperty("user.name");
        
        //Obtenemos el path absoluto del archivo .jasper en la PC
        String filepath = "/Users/"+username+"/Desktop/report1.jasper";
        
        //Obtenemos el path relativo del archivo .jasper de las carpetas del JAR
        File resPath = new File(getClass().getResource("/reports/report1.jasper").getFile());
        InputStream path2 = getClass().getResourceAsStream("/reports/report1.jasper");
       
        
        java.io.File file = new java.io.File("TestWindow.java");
        String path = file.getAbsolutePath();
        String only_path = path.substring(0,path.lastIndexOf('/'));
        System.out.println("PATH: "+resPath.toString());
        
        String reportName = "report1";
        
        //path absoluto para acceder al .jasper en la PC
        //String pathJasper = "/Users/alejandro/NetBeansProjects/Abarrotera-Huerta/src/reports/report1.jasper";
        String pathJasper = "/Users/alejandro/NetBeansProjects/Abarrotera-Huerta/build/classes/reports/report1.jasper";
        
        JasperReport jr = null;
        try {
            jr = (JasperReport) JRLoader.loadObjectFromLocation(pathJasper);
            JasperPrint jp = JasperFillManager.fillReport(jr, null, cc.conectar());
            JasperViewer jv = new JasperViewer(jp, false);
            JasperExportManager.exportReportToPdf(jp);
            jv.setVisible(true);
            jv.setTitle("Pedidos por proveedor");
            
            cc.desconectar();
        } catch (JRException ex) {
            Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void generateJasperReportParameter()
    {
        Map parametro = new HashMap();
        parametro.put("proveedor1", jComboBoxProveedor.getSelectedItem());
        
        System.out.println("Parámetro: "+ jComboBoxProveedor.getSelectedItem());
        System.out.println("Contenido: "+parametro);
        
        //Obtenemos el path relativo del archivo .jasper de las carpetas del JAR
        File resPath = new File(getClass().getResource("/reports/reportParameter.jasper").getFile());
        
        JasperReport jr = null;
        try {
            jr = (JasperReport) JRLoader.loadObjectFromLocation(resPath.toString());
            JasperPrint jp = JasperFillManager.fillReport(jr, parametro, cc.conectar());
            JasperViewer jv = new JasperViewer(jp, false);
            JasperExportManager.exportReportToPdf(jp);
            jv.setVisible(true);
            jv.setTitle("Pedidos por proveedor");
            
                eliminaColaPedidoProveedor1((String) jComboBoxProveedor.getSelectedItem());
//                eliminaColaPedidoProveedor2((String) jComboBoxProveedor.getSelectedItem());
            
            cc.desconectar();
        } catch (JRException ex) {
            Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void eliminarProducto()
    {
        //Variable para obtener el nombre del producto.
        String producto;
        int fila = tablaProductosRegistrados.getSelectedRow();
        if(fila>=0){
            producto = tablaProductosRegistrados.getValueAt(fila, 0).toString();
            System.out.println("ID PARA ELIMINAR ACTUAL: " +producto);
            String sql = "DELETE FROM producto WHERE  nombre = '"+producto+"'";
            try {
                PreparedStatement pst = (PreparedStatement) cn.prepareStatement(sql);
                pst.executeUpdate();
                mostrarProductos("");
            } catch (SQLException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
            
        }else
            JOptionPane.showMessageDialog(null, "Debe elegir el elemento de la tabla que desea eliminar.", "No se seleccionó ningún elemento.", JOptionPane.ERROR_MESSAGE);
    }
    
    public void eliminarElementoPedido()
    {
        String productoPedido;
        int fila = tablaPedidos.getSelectedRow();
        
        if(fila>=0){
            productoPedido = tablaPedidos.getValueAt(fila, 0).toString();
            System.out.println("ID PARA ELIMINAR ACTUAL: " +productoPedido);
            String sql = "DELETE FROM pedido WHERE  nombre = '"+productoPedido+"'";
            try {
                PreparedStatement pst = (PreparedStatement) cn.prepareStatement(sql);
                pst.executeUpdate();
                mostrarPedidos();
            } catch (SQLException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
            
        }else
            JOptionPane.showMessageDialog(null, "Debe elegir el elemento de la tabla que desea remover de la lista de pedidos.", "No se seleccionó ningún elemento.", JOptionPane.ERROR_MESSAGE);
    }
    
    public void setComboBoxProv1()
    {
        jComboBoxProveedor.removeAllItems();

            String sql = "SELECT distinct proveedor1 FROM pedido";

            Statement st;
            try{
                st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);

                jComboBoxProveedor.addItem("Elija proveedor");
                //Validamos que el resultset contenga datos o esté vacío.
                while (rs != null && rs.next()) 
                { 
                    //codigo para tratar al conjunto de registros o al registro obtenido 
                    System.out.println("Se ha encontrado algo en la base de datos.");
                    //Regresa el puntero al inicio para no perder el primer dato de la tabla.
                    //rs.beforeFirst();
                    jComboBoxProveedor.addItem((String) rs.getObject(1));
                }

            } catch (SQLException ex) {
                //Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, ex);
            }
    }
    
    public void setComboBoxProv2()
    {
        jComboBoxProveedor.removeAllItems();

            String sql = "SELECT distinct proveedor2 FROM pedido";

            Statement st;
            try{
                st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);

                jComboBoxProveedor.addItem("Elija proveedor");
                //Validamos que el resultset contenga datos o esté vacío.
                while (rs != null && rs.next()) 
                { 
                    //codigo para tratar al conjunto de registros o al registro obtenido 
                    System.out.println("Se ha encontrado algo en la base de datos.");
                    //Regresa el puntero al inicio para no perder el primer dato de la tabla.
                    //rs.beforeFirst();
                    jComboBoxProveedor.addItem((String) rs.getObject(1));
                }

            } catch (SQLException ex) {
                //Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, ex);
            }
    }
    
    public void llenadoComboBox()
    {
        if(optProv1.isSelected())
        {
            setComboBoxProv1();
            
        }else if(optProv2.isSelected())
        {
            setComboBoxProv2();
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

        MenuTabla = new javax.swing.JPopupMenu();
        Eliminar = new javax.swing.JMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
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
        btnHome = new javax.swing.JButton();
        btnPedido = new javax.swing.JButton();
        btnImprimePedido = new javax.swing.JButton();
        btnPedidoRemove = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jComboBoxProveedor = new javax.swing.JComboBox<>();
        optProv1 = new javax.swing.JRadioButton();
        optProv2 = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        btnGeneraPDF = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        btnNuevoPedido = new javax.swing.JButton();

        Eliminar.setText("Eliminar producto");
        Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EliminarActionPerformed(evt);
            }
        });
        MenuTabla.add(Eliminar);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getResource("/views/store.png")).getImage());
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
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
        Busqueda_producto.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(191, 54, 12), 2, true), "Ingrese producto a buscar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 14), new java.awt.Color(191, 54, 12))); // NOI18N

        txtBusqueda.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
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

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/busqueda/research.png"))); // NOI18N

        javax.swing.GroupLayout Busqueda_productoLayout = new javax.swing.GroupLayout(Busqueda_producto);
        Busqueda_producto.setLayout(Busqueda_productoLayout);
        Busqueda_productoLayout.setHorizontalGroup(
            Busqueda_productoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Busqueda_productoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(txtBusqueda)
                .addContainerGap())
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
        Panel_pedido.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(191, 54, 12), 2, true), "Lista de pedidos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 14), new java.awt.Color(191, 54, 12))); // NOI18N

        tablaPedidos.setFont(new java.awt.Font("Lucida Grande", 2, 14)); // NOI18N
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
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
        );
        Panel_pedidoLayout.setVerticalGroup(
            Panel_pedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
        );

        Panel_productos.setBackground(new java.awt.Color(255, 255, 255));
        Panel_productos.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(191, 54, 12), 2, true), "Productos registrados", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 14), new java.awt.Color(191, 54, 12))); // NOI18N

        tablaProductosRegistrados.setAutoCreateRowSorter(true);
        tablaProductosRegistrados.setFont(new java.awt.Font("Lucida Grande", 2, 14)); // NOI18N
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
        tablaProductosRegistrados.setComponentPopupMenu(MenuTabla);
        tablaProductosRegistrados.setSelectionBackground(new java.awt.Color(239, 108, 0));
        tablaProductosRegistrados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tablaProductosRegistradosMousePressed(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaProductosRegistradosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaProductosRegistrados);

        javax.swing.GroupLayout Panel_productosLayout = new javax.swing.GroupLayout(Panel_productos);
        Panel_productos.setLayout(Panel_productosLayout);
        Panel_productosLayout.setHorizontalGroup(
            Panel_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 461, Short.MAX_VALUE)
            .addGroup(Panel_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE))
        );
        Panel_productosLayout.setVerticalGroup(
            Panel_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(Panel_productosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE))
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

        btnPedido.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        btnPedido.setForeground(new java.awt.Color(85, 139, 47));
        btnPedido.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/greater/cart+.png"))); // NOI18N
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

        btnPedidoRemove.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        btnPedidoRemove.setForeground(new java.awt.Color(153, 0, 0));
        btnPedidoRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/smaller/cart-.png"))); // NOI18N
        btnPedidoRemove.setText("Quitar");
        btnPedidoRemove.setBorderPainted(false);
        btnPedidoRemove.setContentAreaFilled(false);
        btnPedidoRemove.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPedidoRemove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPedidoRemove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPedidoRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedidoRemoveActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(191, 54, 12), 2, true), "Pedido por proveedor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 14), new java.awt.Color(191, 54, 12))); // NOI18N

        jButton1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jButton1.setText("Hacer pedido");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jComboBoxProveedor.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N

        buttonGroup1.add(optProv1);
        optProv1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        optProv1.setText("Proveedor 1");
        optProv1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                optProv1MouseClicked(evt);
            }
        });

        buttonGroup1.add(optProv2);
        optProv2.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        optProv2.setText("Proveedor 2");
        optProv2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                optProv2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(optProv1)
                    .addComponent(optProv2))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                    .addComponent(jComboBoxProveedor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(optProv1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(optProv2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(191, 54, 12), 2, true), "Pedido general", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 14), new java.awt.Color(191, 54, 12))); // NOI18N

        btnGeneraPDF.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        btnGeneraPDF.setForeground(new java.awt.Color(183, 28, 28));
        btnGeneraPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/pdf/pdf.png"))); // NOI18N
        btnGeneraPDF.setText("Imprimir/Guardar");
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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnGeneraPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(btnGeneraPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(191, 54, 12), 2, true), "Generar pedido", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 14), new java.awt.Color(191, 54, 12))); // NOI18N

        btnNuevoPedido.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
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

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnNuevoPedido, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(btnNuevoPedido)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout Panel_generalLayout = new javax.swing.GroupLayout(Panel_general);
        Panel_general.setLayout(Panel_generalLayout);
        Panel_generalLayout.setHorizontalGroup(
            Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Panel_busqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Panel_listado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(Panel_generalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Busqueda_producto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(Panel_generalLayout.createSequentialGroup()
                .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel_generalLayout.createSequentialGroup()
                        .addComponent(btnHome)
                        .addGap(18, 18, 18)
                        .addComponent(btnImprimePedido))
                    .addComponent(Panel_productos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPedido, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPedidoRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Panel_pedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        Panel_generalLayout.setVerticalGroup(
            Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel_generalLayout.createSequentialGroup()
                .addComponent(Panel_busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Busqueda_producto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Panel_listado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel_generalLayout.createSequentialGroup()
                        .addGap(118, 118, 118)
                        .addComponent(btnPedido)
                        .addGap(18, 18, 18)
                        .addComponent(btnPedidoRemove))
                    .addGroup(Panel_generalLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Panel_pedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Panel_productos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(Panel_generalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnHome, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnImprimePedido, javax.swing.GroupLayout.Alignment.TRAILING)))
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

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        // TODO add your handling code here:
        new Principal().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnHomeActionPerformed

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
        //try {
          //  generateJasperReport();
        //} catch (FileNotFoundException ex) {
          //  Logger.getLogger(HacerPedido.class.getName()).log(Level.SEVERE, null, ex);
        //}
        printPedido();
    }//GEN-LAST:event_btnGeneraPDFActionPerformed

    private void btnImprimePedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimePedidoActionPerformed
        // TODO add your handling code here:
        printPedido();
    }//GEN-LAST:event_btnImprimePedidoActionPerformed

    private void btnPedidoRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedidoRemoveActionPerformed
        // TODO add your handling code here:
        //Método de confirmación para eliminar un pedido seleccionado.
        confirmacion2();
        mostrarPedidos();
    }//GEN-LAST:event_btnPedidoRemoveActionPerformed

    private void EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarActionPerformed
        // TODO add your handling code here:
        //Método de confirmación para eliminar un producto seleccionado.
        confirmacion();
    }//GEN-LAST:event_EliminarActionPerformed

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseReleased

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_formMousePressed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseClicked

    private void tablaProductosRegistradosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaProductosRegistradosMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tablaProductosRegistradosMouseClicked

    private void tablaProductosRegistradosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaProductosRegistradosMousePressed
        // TODO add your handling code here:
//        int fila = tablaProductosRegistrados.getSelectedRow();
//        if(fila>=0){
//            btnPedido.setEnabled(true);
//        }else
//          btnPedido.setEnabled(false);
    }//GEN-LAST:event_tablaProductosRegistradosMousePressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        generateJasperReportParameter();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void optProv1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_optProv1MouseClicked
        // TODO add your handling code here:
        llenadoComboBox();
    }//GEN-LAST:event_optProv1MouseClicked

    private void optProv2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_optProv2MouseClicked
        // TODO add your handling code here:
        llenadoComboBox();
    }//GEN-LAST:event_optProv2MouseClicked

    
    class TableRowTransferHandler extends TransferHandler {
    private final DataFlavor localObjectFlavor;
    private int[] indices;
    private int addIndex = -1; //Location where items were added
    private int addCount; //Number of items added.
    private JComponent source;

    protected TableRowTransferHandler() {
        super();
        localObjectFlavor = new ActivationDataFlavor(Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
    }
    @Override protected Transferable createTransferable(JComponent c) {
        source = c;
        JTable table = (JTable) c;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        List<Object> list = new ArrayList<>();
        indices = table.getSelectedRows();
        for (int i: indices) {
            list.add(model.getDataVector().get(i));
        }
        Object[] transferedObjects = list.toArray();
        return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
    }
    @Override public boolean canImport(TransferHandler.TransferSupport info) {
        JTable table = (JTable) info.getComponent();
        boolean isDropable = info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
        //XXX bug?
        table.setCursor(isDropable ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
        return isDropable;
    }
    @Override public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE; //TransferHandler.COPY_OR_MOVE;
    }
    @Override public boolean importData(TransferHandler.TransferSupport info) {
        if (!canImport(info)) {
            return false;
        }
        TransferHandler.DropLocation tdl = info.getDropLocation();
        if (!(tdl instanceof JTable.DropLocation)) {
            return false;
        }
        JTable.DropLocation dl = (JTable.DropLocation) tdl;
        JTable tablaPedidos = (JTable) info.getComponent();
        DefaultTableModel model = (DefaultTableModel) tablaPedidos.getModel();
        int index = dl.getRow();
        //boolean insert = dl.isInsert();
        int max = model.getRowCount();
        if (index < 0 || index > max) {
            index = max;
        }
        addIndex = index;
        tablaPedidos.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        try {
            Object[] values = (Object[]) info.getTransferable().getTransferData(localObjectFlavor);
            if (Objects.equals(source, tablaPedidos)) {
                addCount = values.length;
            }
            for (int i = 0; i < values.length; i++) {
                int idx = index++;
                model.insertRow(idx, (Vector) values[i]);
                tablaPedidos.getSelectionModel().addSelectionInterval(idx, idx);
            }
            return true;
        } catch (UnsupportedFlavorException | IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    @Override protected void exportDone(JComponent c, Transferable data, int action) {
        cleanup(c, action == TransferHandler.MOVE);
    }
    private void cleanup(JComponent c, boolean remove) {
        if (remove && indices != null) {
            c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            DefaultTableModel model = (DefaultTableModel) ((JTable) c).getModel();
            if (addCount > 0) {
                for (int i = 0; i < indices.length; i++) {
                    if (indices[i] >= addIndex) {
                        indices[i] += addCount;
                    }
                }
            }
            for (int i = indices.length - 1; i >= 0; i--) {
                model.removeRow(indices[i]);
            }
        }
        indices  = null;
        addCount = 0;
        addIndex = -1;
    }
}
    
    

class TS extends TransferHandler {

    public TS() {
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent source) {

        return new StringSelection((String) ((JTable) tablaProductosRegistrados).getModel().getValueAt(((JTable) tablaProductosRegistrados).getSelectedRow(), ((JTable) tablaProductosRegistrados).getSelectedColumn()));
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {

        ((JTable) tablaProductosRegistrados).getModel().setValueAt("", ((JTable) tablaProductosRegistrados).getSelectedRow(), ((JTable) tablaProductosRegistrados).getSelectedColumn());

    }

    @Override
    public boolean canImport(TransferSupport support) {
        return true;
    }

    @Override
    public boolean importData(TransferSupport support) {
//        JTable tablaPedidos = (JTable) support.getComponent();
        tablaPedidos = (JTable) support.getComponent();
        try {
            tablaPedidos.setValueAt(support.getTransferable().getTransferData(DataFlavor.stringFlavor), tablaPedidos.getSelectedRow(), tablaPedidos.getSelectedColumn());
        } catch (UnsupportedFlavorException ex) {
            Logger.getLogger(TS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return super.importData(support);
    }
}
    
    
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
    private javax.swing.JMenuItem Eliminar;
    private javax.swing.JPopupMenu MenuTabla;
    private javax.swing.JPanel Panel_busqueda;
    private javax.swing.JPanel Panel_general;
    private javax.swing.JPanel Panel_listado;
    private javax.swing.JPanel Panel_pedido;
    private javax.swing.JPanel Panel_productos;
    private javax.swing.JButton btnGeneraPDF;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnImprimePedido;
    private javax.swing.JButton btnNuevoPedido;
    private javax.swing.JButton btnPedido;
    private javax.swing.JButton btnPedidoRemove;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBoxProveedor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JRadioButton optProv1;
    private javax.swing.JRadioButton optProv2;
    private javax.swing.JTable tablaPedidos;
    private javax.swing.JTable tablaProductosRegistrados;
    private javax.swing.JTextField txtBusqueda;
    // End of variables declaration//GEN-END:variables
}
