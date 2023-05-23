
package com.mycompany.proyectosisi;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author carlossalcidoa
 */
public class Cclientes {
    
    String nom_usuario;
    String password_usuario;
    String email;
    String tarjeta;
    
    public String getNom_usuario() {
        return nom_usuario;
    }

    public void setNom_usuario(String nom_usuario) {
        this.nom_usuario = nom_usuario;
    }

    public String getPassword_usuario() {
        return password_usuario;
    }

    public void setPassword_usuario(String password_usuario) {
        this.password_usuario = password_usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTarjeta() {
        return tarjeta;
    }

    public void setTarjeta(String tarjeta) {
        this.tarjeta = tarjeta;
    }
        
    public void insertarCliente(JTextField paraNomUs, JTextField paraPassw, JTextField paraEmail, JTextField paraTarjeta){
        
        setNom_usuario(paraNomUs.getText());
        setPassword_usuario(paraPassw.getText());
        setEmail(paraEmail.getText());
        setTarjeta(paraTarjeta.getText());
        
        CconexionBD objetoConexion = new CconexionBD();
        String consulta = "insert into clientes (nom_usuario, password_usuario, email, tarjeta)	values(?, ?, ?, ?);";
        try{
            CallableStatement cs= objetoConexion.establecerConexion().prepareCall(consulta);
            cs.setString(1, getNom_usuario());
            cs.setString(2, getPassword_usuario());
            cs.setString(3, getEmail());
            cs.setString(4, getTarjeta());
            
            cs.execute();
            
            JOptionPane.showMessageDialog(null, "Usuario agregado exitosamente");
            
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Error:"+ e.toString());

        }
    }
    
    public boolean login(JTextField txtInicioNom, JTextField txtInicioPassw){
        String nom_us= txtInicioNom.getText();
        String passw_us= txtInicioPassw.getText();
        CconexionBD objetoConexion = new CconexionBD();
        //String consulta = "insert into clientes (nom_usuario, password_usuario, email, tarjeta)	values(?, ?, ?, ?);";
        int band=0;
        
        String sql;
        sql="select * from clientes";
        
        //String [] datos = new String[4];
        Statement st;
        try{
            st = objetoConexion.establecerConexion().createStatement();
            
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                if(rs.getString(1).equals(nom_us)){
                    if(rs.getString(2).equals(passw_us)){
                        band=1;
                        JOptionPane.showMessageDialog(null, "Bienvenido "+rs.getString(1));
                        return true;
                    }    
                }
            }
            
            if(band!=1){
                JOptionPane.showMessageDialog(null, "Usuario o contraseña inválidos");
                return false;
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error:"+ e.toString());
        }
    return true;
    }
    
    public boolean modificarCupo(JTextField txtOfertaReg, JTextField txtOfertaEst, JTextField txtComprarDestino, JTextField txtComprarFecha, JTextField txtComprarHora){
        String streg = txtOfertaReg.getText();
        String stest = txtOfertaEst.getText();
        String destino = txtComprarDestino.getText();
        String fecha = txtComprarFecha.getText();
        String hora = txtComprarHora.getText();
        int inreg= Integer.parseInt(streg);
        int inest= Integer.parseInt(stest);
        int cupo_total = inreg+inest;
        int cupo_final=0;
        String aux="";
        CconexionBD objetoConexion = new CconexionBD();
        
        //OBTENER EL CUPO FINAL
        String sql;
        sql="select * from viajes";
        Statement st;
        try{
            st = objetoConexion.establecerConexion().createStatement();
            
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                if(rs.getString(1).equals(destino)){
                    if(rs.getString(2).equals(fecha)){
                        if(rs.getString(3).equals(hora)){
                            cupo_final = Integer.parseInt(rs.getString(5)) - cupo_total;
                            aux= rs.getString(5);
                        }
                    }    
                }
            }
        
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error:"+ e.toString());
        }
        
        //ACTUALIZAR BASE DE DATOS
        if(cupo_final >= 0){
            String consulta = "UPDATE viajes SET disponibilidad= ? WHERE viajes.destino = ? and viajes.fecha = ? and viajes.hora = ?;";
            try{
                CallableStatement cs= objetoConexion.establecerConexion().prepareCall(consulta);
                cs.setInt(1, cupo_final);
                cs.setString(2, destino);
                cs.setString(3, fecha);
                cs.setString(4, hora);

                cs.execute();
                return true;
            }catch (Exception e){
                JOptionPane.showMessageDialog(null, "Error:"+ e.toString());

            }
        }
        else{
            JOptionPane.showMessageDialog(null, "El cupo máximo es: " + aux);
            return false;
        }
        return false;
    }
    
    public String verCupo(String destino, String fecha, String hora){
        CconexionBD objetoConexion = new CconexionBD();
        
        //OBTENER EL CUPO FINAL
        String sql;
        sql="select * from viajes";
        Statement st;
        try{
            st = objetoConexion.establecerConexion().createStatement();
            
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                if(rs.getString(1).equals(destino)){
                    if(rs.getString(2).equals(fecha)){
                        if(rs.getString(3).equals(hora)){
                            return rs.getString(5);
                        }
                    }    
                }
            }
        return "";
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error:"+ e.toString());
            return "";
        }
    }
    
    public String verPrecio(JTextField asientosReg, JTextField asientosEst, JTextField precioBase){
        String asReg = asientosReg.getText();
        String asEst = asientosEst.getText();
        String precBas = precioBase.getText();
        float reg = Float.parseFloat(asReg);
        float est = Float.parseFloat(asEst);
        float precio = Float.parseFloat(precBas);
        float precio_reg, precio_est, precio_final;
        
        precio_reg = reg * precio;
        precio_est = (est * precio) / 2;
        
        precio_final = precio_reg + precio_est; 
        
        
        return String.valueOf(precio_final);
    }
    
    public void insertarCompra(JTextField nomUsuario, JTextField nomViajero, JTextField asientosReg, JTextField asientosEst, JTextField precioBase,
            JTextField destino, JTextField fecha, JTextField horario){  
        
        String asReg = asientosReg.getText();
        String asEst = asientosEst.getText();
        String precBas = precioBase.getText();
        float reg = Float.parseFloat(asReg);
        float est = Float.parseFloat(asEst);
        float precio = Float.parseFloat(precBas);
        float precio_reg, precio_est, precio_final;
        //CALCULAR PRECIO FINAL
        precio_reg = reg * precio;
        precio_est = (est * precio) / 2;
        precio_final = precio_reg + precio_est; 
        
        String codigo = String.valueOf(Character.toUpperCase(nomUsuario.getText().charAt(0))) + 
                String.valueOf(Character.toUpperCase(nomViajero.getText().charAt(0))) + 
                String.valueOf(Character.toUpperCase(nomViajero.getText().charAt(1))) + 
                String.valueOf(destino.getText().charAt(4)) + 
                String.valueOf(destino.getText().charAt(5)) + 
                String.valueOf(destino.getText().charAt(6)) + 
                String.valueOf(fecha.getText().charAt(0)) + 
                String.valueOf(fecha.getText().charAt(1)) + 
                String.valueOf(horario.getText().charAt(0)) + 
                String.valueOf(horario.getText().charAt(1));
        CconexionBD objetoConexion = new CconexionBD();
        String consulta = "insert into compras (nom_us, nom_viajero, destino, fecha, hora, asientos_reg, asientos_est, coidgo_viaje, precio_final) \n" +
"	values(?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try{
            CallableStatement cs= objetoConexion.establecerConexion().prepareCall(consulta);
            cs.setString(1, nomUsuario.getText());
            cs.setString(2, nomViajero.getText());
            cs.setString(3, destino.getText());
            cs.setString(4, fecha.getText());
            cs.setString(5, horario.getText());
            cs.setInt(6, Integer.parseInt(asReg));
            cs.setInt(7, Integer.parseInt(asEst));
            cs.setString(8, codigo);
            cs.setFloat(9, precio_final);
            
            cs.execute();
            
            JOptionPane.showMessageDialog(null, "Gracias por su compra :)");
            
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Error:"+ e.toString());

        }
    }
    
    
    void mostrarTabla(JTable tablaBoletos, JTextField nomUsuario){
        CconexionBD objetoConexion = new CconexionBD();
        DefaultTableModel modelo = new DefaultTableModel();
        String sql="";
        
        modelo.addColumn("USUARIO");
        modelo.addColumn("VIAJERO");
        modelo.addColumn("DESTINO");
        modelo.addColumn("FECHA");
        modelo.addColumn("HORARIO");
        modelo.addColumn("PASAJES REGULARES");
        modelo.addColumn("PASAJES ESTUDIANTE");
        modelo.addColumn("CÓDIGO DE VIAJE");
        modelo.addColumn("PRECIO FINAL");
        
        tablaBoletos.setModel(modelo);
        
        sql = "select * from compras";// WHERE compras.nom_us = ? ;";
        
        
        String [] datos = new String [9];
        Statement st;
        
        try{
            //CallableStatement cs= objetoConexion.establecerConexion().prepareCall(sql);
            //cs.setString(1, nomUsuario.getText());
            //cs.execute();
            st = objetoConexion.establecerConexion().createStatement();
            ResultSet rs = st.executeQuery(sql);
            
            while(rs.next()){
                if(rs.getString(1).equals(nomUsuario.getText())){
                    datos[0] = rs.getString(1);
                    datos[1] = rs.getString(2);
                    datos[2] = rs.getString(3);
                    datos[3] = rs.getString(4);
                    datos[4] = rs.getString(5);
                    datos[5] = rs.getString(6);
                    datos[6] = rs.getString(7);
                    datos[7] = rs.getString(8);
                    datos[8] = rs.getString(9);

                    modelo.addRow(datos);
                }
            }
            
            tablaBoletos.setModel(modelo);
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error:"+ e.toString());
        }
    }
    
}
