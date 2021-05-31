package sewakost016;

import connection016.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;

public class MasterUnitKost {
    private JTextField txtCariID;
    private JButton btnCari;
    private JTextField txtID;
    private JTextField txtDeskripsi;
    private JTextField txtHarga;
    private JButton btnSimpan;
    private JButton btnUpdate;
    private JButton btnHapus;
    private JButton btnBatal;
    private JTable tbUnitKost;
    private JPanel MasterUnitKost;
    private JTextArea txtFasilitas;
    private DefaultTableModel model;

    DBConnect connection = new DBConnect();
    int autoid;
    String kode;
    String deskripsi;
    String fasilitas;
    String harga;
    int status;

    public MasterUnitKost() {
        model = new DefaultTableModel();
        tbUnitKost.setModel(model);
        addColomn();
        clear();

        btnCari.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.getDataVector().removeAllElements(); //menghapus semua data ditamp
                model.fireTableDataChanged(); //memberitahu data telah kosong

                try{
                    DBConnect connection = new DBConnect();
                    connection.stat = connection.conn.createStatement();
                    String query = "SELECT TOP (1) * FROM tbUnitKost WHERE kost_id = '" +txtCariID.getText()+"'";
                    connection.result = connection.stat.executeQuery(query);
                    //lakukan perbaris data
                    while(connection.result.next()){
                        Object[] obj = new Object[5];
                        obj[0] = connection.result.getString(1);
                        obj[1] = connection.result.getString(2);
                        obj[2] = connection.result.getString(3);
                        obj[3] = connection.result.getString(4);

                        if(connection.result.getInt(5)==0){
                            obj[4] = "Tersedia";
                        }else{
                            obj[4] = "Tidak Tersedia";
                        }
                        model.addRow(obj);
                    }
                    //jika di tabel tidak ada data yang dicari
                    if (model.getRowCount() == 0){
                        JOptionPane.showMessageDialog(null, "Data tidak ditemukan");
                    }
                    connection.stat.close();
                    connection.result.close();
                } catch (Exception ex){
                    System.out.println("Terjadi error saat cari data" + e);
                }
            }
        });
        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                kode = txtID.getText();
                deskripsi = txtDeskripsi.getText();
                fasilitas = txtFasilitas.getText();
                harga = txtHarga.getText();
                status = 0;

                try{
                    if(validasinull()){
                        throw new Exception("Harap ISI Semua !!!!");
                    }else {
                        try{
                            String query = "INSERT INTO tbUnitKost VALUES(?,?,?,?,?)";
                            connection.pstat = connection.conn.prepareStatement(query);
                            connection.pstat.setString(1,kode);
                            connection.pstat.setString(2,deskripsi);
                            connection.pstat.setString(3,fasilitas);
                            connection.pstat.setString(4,harga);
                            connection.pstat.setInt(5,status);
                            connection.pstat.executeUpdate();
                            connection.pstat.close();
                        }
                        catch(Exception ex) {
                            System.out.println("Terjadi error pada saat insert data"+ex);
                        }
                        JOptionPane.showMessageDialog(null,"Insert Data Berhasil");
                        clear();
                    }

                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null,ex.getMessage());
                }
            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                kode = txtID.getText();
                deskripsi = txtDeskripsi.getText();
                fasilitas = txtFasilitas.getText();
                harga = txtHarga.getText();
                status = 0;

                try{
                    if(validasinull()){
                        throw new Exception("Harap ISI Semua !!!!");
                    }else{

                        try {
                            DBConnect connection2 = new DBConnect();
                            connection2.stat = connection2.conn.createStatement();
                            String query2 = "SELECT kost_status FROM tbUnitKost WHERE kost_id='"+kode+"'";
                            connection2.result = connection2.stat.executeQuery(query2);

                            while(connection2.result.next()) {
                                status  = connection2.result.getInt(1);
                            }

                            connection2.stat.close();
                            connection2.result.close();
                        }catch (Exception ex){
                            System.out.println("Terjadi error saat load data2"+ex);
                        }

                        try{
                            String query = "UPDATE tbUnitKost SET kost_deskripsi=?,kost_fasilitas=?,kost_hargasewa=?,kost_status=? WHERE kost_id=?";
                            connection.pstat = connection.conn.prepareStatement(query);
                            connection.pstat.setString(1,deskripsi);
                            connection.pstat.setString(2,fasilitas);
                            connection.pstat.setString(3,harga);
                            connection.pstat.setInt(4,status);
                            connection.pstat.setString(5,kode);
                            connection.pstat.executeUpdate();
                            connection.pstat.close();
                        }
                        catch(Exception ex) {
                            System.out.println("Terjadi error pada saat update data"+ex);
                        }
                        JOptionPane.showMessageDialog(null,"Update Data Berhasil");
                        clear();
                    }

                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null,ex.getMessage());
                }
            }
        });
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                kode = txtID.getText();
                try{
                    String query = "DELETE FROM tbUnitKost WHERE kost_id=?";
                    connection.pstat = connection.conn.prepareStatement(query);
                    connection.pstat.setString(1,kode);

                    connection.pstat.executeUpdate();
                    connection.pstat.close();
                }
                catch(Exception ex) {
                    System.out.println("Terjadi error pada saat delete data"+ex);
                }
                JOptionPane.showMessageDialog(null,"Delete Data Berhasil");
                clear();
            }
        });
        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        tbUnitKost.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbUnitKost.getSelectedRow();
                txtID.setText((String) model.getValueAt(i,0));
                txtDeskripsi.setText((String) model.getValueAt(i,1));
                txtFasilitas.setText((String) model.getValueAt(i,2));
                txtHarga.setText((String) model.getValueAt(i,3));
            }
        });
    }

    public void autoid(){
        try{
            String query = "SELECT TOP (1) MAX(RIGHT (kost_id,3))+1 FROM tbUnitKost";
            connection.pstat = connection.conn.prepareStatement(query);
            connection.result = connection.pstat.executeQuery();
            while(connection.result.next()){
                if(connection.result.getString(1)==null){
                    autoid = 1;
                }else{
                    autoid =Integer.parseInt(connection.result.getString(1));
                }
            }
            if(autoid<10){
                kode = "UK00"+autoid;
            }else if(autoid<100){
                kode = "UK0"+autoid;
            }else{
                kode = "UK"+autoid;
            }
            txtID.setText(kode);
            connection.pstat.close();
            connection.result.close();
        }
        catch(Exception ex) {
            System.out.println("Terjadi error pada saat generate id data "+ex);
        }
    }

    public void addColomn(){
        model.addColumn("ID");
        model.addColumn("Deskripsi");
        model.addColumn("Fasilitas");
        model.addColumn("Harga");
        model.addColumn("Status");
    }


    public void loaddata(){
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try{
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM tbUnitKost";
            connection.result = connection.stat.executeQuery(query);
            while(connection.result.next()){
                Object[] obj = new Object[5];
                obj[0] = connection.result.getString(1);
                obj[1] = connection.result.getString(2);
                obj[2] = connection.result.getString(3);
                obj[3] = String.valueOf(connection.result.getInt(4));

                if(connection.result.getInt(5)==0){
                    obj[4] = "Tersedia";
                }else{
                    obj[4] = "Tidak Tersedia";
                }
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        }catch(Exception ex){
            System.out.println("Terjadi error saat load data"+ex);
        }
    }

    public void clear(){
        txtCariID.setText(null);
        txtID.setText(null);
        txtDeskripsi.setText(null);
        txtFasilitas.setText(null);
        txtHarga.setText(null);
        autoid();
        loaddata();
    }

    public boolean validasinull(){
        if(deskripsi.isEmpty() || fasilitas.isEmpty() || harga.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    public void apk() {
        JFrame frame = new JFrame("MENU UNIT KOST");
        frame.setContentPane(new MasterUnitKost().MasterUnitKost);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800,500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MENU KELOLA UNIT KOST");
        frame.setContentPane(new MasterUnitKost().MasterUnitKost);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800,500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
