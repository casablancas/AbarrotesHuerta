/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import com.lowagie.text.pdf.PdfName;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author alejandro
 */
public class ExportRestore extends javax.swing.JFrame {

    /**
     * Creates new form ExportRestore
     */
    public ExportRestore() {
        initComponents();
        this.setTitle("Exportar - Importar base de datos.");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        toolTips();
    }
    
    String texto;
    String dbUserName = "";
    String dbPassword = "";
    
    public void toolTips()
    {
        btnExport2.setToolTipText("Exporte la base de datos actual.");
        btnImport.setToolTipText("Eliga una base de datos para importar.");
        btnHome.setToolTipText("Regresar al Menú Principal.");
    }
    
    private static void backup() {
    
        //Código para exportar la BD completa.
            
        String username = System.getProperty("user.name");
//            
        String filepath = "/Users/"+username+"/Desktop/prueba_backup.sql";

            Process p = null;
            try {
                Runtime runtime = Runtime.getRuntime();
                p = runtime.exec("/Applications/XAMPP/xamppfiles/bin/mysqldump -h db4free.net -uoswaldo -poswaldohuerta --add-drop-database -B abarrotera -r " + filepath);
                //change the dbpass and dbname with your dbpass and dbname
                int processComplete = p.waitFor();
 
                if (processComplete == 0) {
 
                    System.out.println("Backup created successfully!");
 
                } else {
                    System.out.println("Could not create the backup");
                }
 
 
            } catch (Exception e) {
                e.printStackTrace();
            }
        
        //Código para exportar solamente las tablas de la BD
        
//        try {
//           Process p = Runtime
//                 .getRuntime()
//                   .exec("/Applications/XAMPP/xamppfiles/bin/mysqldump -h db4free.net -u oswaldo -poswaldohuerta abarrotera");
//                 //.exec("/Applications/XAMPP/xamppfiles/bin/mysqldump -h db4free.net -u oswaldo -poswaldohuerta abarrotera");
//                 //.exec("C:/Aplicaciones/wamp/bin/mysql/mysql5.1.36/bin/mysqldump -u oswaldo -poswaldohuerta abarrotera");
//
//            String username = System.getProperty("user.name");
//            
//            String filepath = "/Users/"+username+"/Desktop/prueba_backup.sql";
//                 
//           InputStream is = p.getInputStream();
//           FileOutputStream fos = new FileOutputStream(filepath);
//           byte[] buffer = new byte[1000];
//
//           int leido = is.read(buffer);
//           while (leido > 0) {
//              fos.write(buffer, 0, leido);
//              leido = is.read(buffer);
//               System.out.println(leido);
//           }
//            System.out.println("¡Exportación exitosa!");
//
//           fos.close();
//
//        } catch (Exception e) {
//           e.printStackTrace();
//        }
    }
    
    
    private static void restore() {
        
        //Código para importar la BD completa.
        
//        String[] restoreCmd = new String[]{"mysql ", "--user=" + dbUserName, "--password=" + dbPassword, "-e", "source " + source};
// 
//        Process runtimeProcess;
//        try {
// 
//            runtimeProcess = Runtime.getRuntime().exec(restoreCmd);
//            int processComplete = runtimeProcess.waitFor();
// 
//            if (processComplete == 0) {
//                System.out.println("Restored successfully!");
//            } else {
//                System.out.println("Could not restore the backup!");
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        
        //Código para importar solamente las tablas a la BD.
        
        try {
           Process p = Runtime
                 .getRuntime()
                   .exec("/Applications/XAMPP/xamppfiles/bin/mysql -h db4free.net -u oswaldo -poswaldohuerta abarrotera");
                 //.exec("C:/Aplicaciones/wamp/bin/mysql/mysql5.1.36/bin/mysql -u root -ppassword database");

           String username = System.getProperty("user.name");
            
           String filepath = "/Users/"+username+"/Desktop/prueba_backup.sql";
                 
           OutputStream os = p.getOutputStream();
           FileInputStream fis = new FileInputStream(filepath);
           byte[] buffer = new byte[1000];

           int leido = fis.read(buffer);
           while (leido > 0) {
              os.write(buffer, 0, leido);
              leido = fis.read(buffer);
           }
            System.out.println("¡Importación exitosa!");

           os.flush();
           os.close();
           fis.close();

        } catch (Exception e) {
           e.printStackTrace();
        }
    }
    
    
    private String abrirArchivo() {
        String aux="";   
        String txt="";
        try
        {
         /**llamamos el metodo que permite cargar la ventana*/
         JFileChooser file=new JFileChooser();
         file.showOpenDialog(this);
         /**abrimos el archivo seleccionado*/
         File abre=file.getSelectedFile();

         /**recorremos el archivo, lo leemos para plasmarlo
         *en el area de texto*/
         if(abre!=null)
         {     
            FileReader archivos=new FileReader(abre);
            BufferedReader lee=new BufferedReader(archivos);
            while((aux=lee.readLine())!=null)
            {
               texto+= aux+ "\n";
            }
               lee.close();
          }    
         }
         catch(IOException ex)
         {
           JOptionPane.showMessageDialog(null,ex+"" +
                 "\nNo se ha encontrado el archivo",
                       "ADVERTENCIA!!!",JOptionPane.WARNING_MESSAGE);
          }
        return texto;//El texto se almacena en el JTextArea
    }
    
    
    private void exportDB() {
        
        String nombre="";
        JFileChooser file=new JFileChooser();
        file.setDialogTitle("Elija nombre de archivo para exportar la base de datos");
        file.setFileSelectionMode(JFileChooser.APPROVE_OPTION);
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.SQL", "sql");
        file.setFileFilter(filtro);
        file.showSaveDialog(this);
        File guarda = file.getSelectedFile();
        String pathArchivo = file.getSelectedFile().getPath();
//        String nombreArchivo = (file.getSelectedFile().getName())+".sql";
//        System.out.println("1: "+pathArchivo+" "+nombreArchivo);
        Process p = null;
        
        String host = "";
        String user = "root";
        String pass = "";
        String db = "abarrotera";
        
        String executeCmd = "/Applications/XAMPP/xamppfiles/bin/mysqldump -u" + user +  " --add-drop-database -B " + db + " -r " + pathArchivo.replace(" ", "")+".sql";
        
        try {
            Runtime runtime = Runtime.getRuntime();
            //Exportación de BD mysql remota en db4free
//            p = runtime.exec("/Applications/XAMPP/xamppfiles/bin/mysqldump -h db4free.net -uoswaldo -poswaldohuerta --add-drop-database -B abarrotera -r " + pathArchivo+".sql");
            //FALTA SEGUIR MODIFICANDO PARA QUE FUNCIONE -.-
//            p = runtime.exec("/Applications/XAMPP/xamppfiles/bin/mysqldump -h localhost -uroot --add-drop-database -B abarrotera -r " + pathArchivo+".sql");
//            p = runtime.exec("/Applications/XAMPP/xamppfiles/bin/mysqldump abarrotera -h localhost -uroot -p "+pathArchivo+".sql");
            p = runtime.exec(executeCmd);
//            System.out.println("2: "+pathArchivo+" "+nombreArchivo+" "+nombreArchivo1);

            int processComplete = p.waitFor();
            
            if (processComplete == 0) {
                
//                System.out.println("Backup created successfully!");
                JOptionPane.showMessageDialog(null, "¡Se ha exportado la base de datos exitosamente!",
                "Exportación exitosa", JOptionPane.INFORMATION_MESSAGE);
                
            } else {
//                System.out.println("Could not create the backup");
                JOptionPane.showMessageDialog(null, "Algo salió mal, no se ha podido exportar la base de datos.",
                "Exportación errónea", JOptionPane.ERROR_MESSAGE);
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void importDB()
    {
        //Creamos el objeto JFileChooser
        JFileChooser elegir = new JFileChooser();
        elegir.setDialogTitle("Seleccione el archivo .SQL para importar");
        //Indicamos lo que podemos seleccionar
        elegir.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //Creamos el filtro
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.SQL", "sql");
        //Le indicamos el filtro
        elegir.setFileFilter(filtro);
        
        int opcion = elegir.showOpenDialog(btnImport);
               
                    //Si presionamos el boton ABRIR en pathArchivo obtenemos el path y nombre del archivo.
                    if (opcion == JFileChooser.APPROVE_OPTION) {
                        //Obtiene path del archivo.
                        String pathArchivo = elegir.getSelectedFile().getPath(); 
                        //Obtiene nombre del archivo.
                        String nombre = elegir.getSelectedFile().getName(); 
                       
                        System.out.println("El nombre del archivo es: "+ nombre);
                        System.out.println("El path del archivo es: "+ pathArchivo);
                        
                        try {
                            Process p = Runtime
                                  .getRuntime()
                                    //Importación de BD desde mysql remoto en db4free
//                                    .exec("/Applications/XAMPP/xamppfiles/bin/mysql -h db4free.net -u oswaldo -poswaldohuerta abarrotera");
                                    .exec("/Applications/XAMPP/xamppfiles/bin/mysql -h localhost -u root");

                                  //.exec("C:/Aplicaciones/wamp/bin/mysql/mysql5.1.36/bin/mysql -u root -ppassword database");

                            String username = System.getProperty("user.name");

                            String filepath = "/Users/"+username+"/Desktop/prueba_backup.sql";

                            OutputStream os = p.getOutputStream();
                            FileInputStream fis = new FileInputStream(pathArchivo);
                            byte[] buffer = new byte[1000];

                            int leido = fis.read(buffer);
                            while (leido > 0) {
                               os.write(buffer, 0, leido);
                               leido = fis.read(buffer);
                            }
                            
                            
                            
//                             System.out.println("¡Importación exitosa!");
                               JOptionPane.showMessageDialog(null, "¡Se ha importado la base de datos exitosamente!",
                               "Importación exitosa", JOptionPane.INFORMATION_MESSAGE);
                               
                               os.flush();
                               os.close();
                               fis.close();
                            

                         } catch (Exception e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Algo salió mal, no se ha podido importar la base de datos.",
                            "Importación errónea", JOptionPane.ERROR_MESSAGE);
                         }
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

        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnExport2 = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        btnHome = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel6.setBackground(new java.awt.Color(191, 54, 12));

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Exportar/Importar base de datos.");

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
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(191, 54, 12), 2, true), "Elija una opción", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 14), new java.awt.Color(191, 54, 12))); // NOI18N

        btnExport2.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        btnExport2.setForeground(new java.awt.Color(255, 87, 34));
        btnExport2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/db/export_db.png"))); // NOI18N
        btnExport2.setText("Exportar BD");
        btnExport2.setBorderPainted(false);
        btnExport2.setContentAreaFilled(false);
        btnExport2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExport2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExport2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnExport2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExport2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExport2btnExportActionPerformed(evt);
            }
        });

        btnImport.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        btnImport.setForeground(new java.awt.Color(255, 87, 34));
        btnImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/db/import_db.png"))); // NOI18N
        btnImport.setText("Importar BD");
        btnImport.setBorderPainted(false);
        btnImport.setContentAreaFilled(false);
        btnImport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnImport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImport.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnImport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnExport2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                .addComponent(btnImport)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnImport)
                    .addComponent(btnExport2))
                .addContainerGap(14, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(btnHome)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnHome))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExport2btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExport2btnExportActionPerformed
        // TODO add your handling code here:
        exportDB();
    }//GEN-LAST:event_btnExport2btnExportActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        // TODO add your handling code here:
        importDB();
    }//GEN-LAST:event_btnImportActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        new Principal().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        // TODO add your handling code here:
        new Principal().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnHomeActionPerformed

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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ExportRestore.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ExportRestore.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ExportRestore.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ExportRestore.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ExportRestore().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExport2;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnImport;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    // End of variables declaration//GEN-END:variables
}
