package sewakost016;

import connection016.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Pembayaran {
    private JTextField txtID;
    private JButton btnCari;
    private JButton btnBayar;
    private JTable tbTransaksi;
    private JButton btnHapus;
    private JButton btnCancel;
    private JPanel Pembayaran;
    private DefaultTableModel model;

    DBConnect connection = new DBConnect();
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String tglkeluar;
    String pnyid;
    String kostid;
    int statuspny;
    int statuskost;
    int statustrs;

    public void apk(){
        JFrame frame = new JFrame("MENU PEMBAYARAN");
        frame.setContentPane(new Pembayaran().Pembayaran);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800,500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MENU PEMBAYARAN");
        frame.setContentPane(new Pembayaran().Pembayaran);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800,500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public Pembayaran() {
        model = new DefaultTableModel();
        tbTransaksi.setModel(model);
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
                    String query = "SELECT TOP (1) * FROM tbTransaksiSewa WHERE trs_id ="+ txtID.getText()+" AND trs_statusbayar= 0";
                    connection.result = connection.stat.executeQuery(query);
                    //lakukan perbaris data
                    while(connection.result.next()){
                        Object[] obj = new Object[7];
                        obj[0] = connection.result.getString(1);
                        obj[1] = connection.result.getString(3);
                        obj[2] = connection.result.getString(4);
                        obj[3] = connection.result.getString(5);
                        obj[4] = calculatedate(connection.result.getString(5),connection.result.getInt(6));
                        obj[5] = String.valueOf(connection.result.getInt(7));

                        if(connection.result.getInt(8)==0){
                            obj[6] = "Belum Lunas";
                        }else{
                            obj[6] = "Lunas";
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
        btnBayar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DBConnect connection = new DBConnect();
                    connection.stat = connection.conn.createStatement();
                    String query = "SELECT pny_id,kost_id FROM tbTransaksiSewa WHERE trs_id="+ txtID.getText();
                    connection.result = connection.stat.executeQuery(query);

                    while(connection.result.next()) {
                        pnyid = connection.result.getString(1);
                        kostid = connection.result.getString(2);
                        statuspny = 1;
                        statuskost = 1;
                        statustrs = 1;
                    }

                    connection.stat.close();
                    connection.result.close();
                }catch (Exception ex){
                    System.out.println("Terjadi error saat cari harga kost"+ex);
                }

                try{
                    String query1 = "UPDATE tbPenyewa SET pny_status=? WHERE pny_id=?";
                    connection.pstat = connection.conn.prepareStatement(query1);
                    connection.pstat.setInt(1,statuspny);
                    connection.pstat.setString(2,pnyid);
                    connection.pstat.executeUpdate();
                    connection.pstat.close();

                    String query2 = "UPDATE tbUnitKost SET kost_status=? WHERE kost_id=?";
                    connection.pstat = connection.conn.prepareStatement(query2);
                    connection.pstat.setInt(1,statuskost);
                    connection.pstat.setString(2,kostid);
                    connection.pstat.executeUpdate();
                    connection.pstat.close();

                    String query3 = "UPDATE tbTransaksiSewa SET trs_statusbayar=? WHERE trs_id=?";
                    connection.pstat = connection.conn.prepareStatement(query3);
                    connection.pstat.setInt(1,statustrs);
                    connection.pstat.setString(2, txtID.getText());
                    connection.pstat.executeUpdate();
                    connection.pstat.close();
                }
                catch(Exception ex) {
                    System.out.println("Terjadi error pada saat update data"+ex);
                }
                JOptionPane.showMessageDialog(null,"Update Data Berhasil");
                clear();
            }
        });
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String query = "DELETE FROM tbTransaksiSewa WHERE trs_id=?";
                    connection.pstat = connection.conn.prepareStatement(query);
                    connection.pstat.setString(1, txtID.getText());

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
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        tbTransaksi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbTransaksi.getSelectedRow();
                txtID.setText((String) model.getValueAt(i,0));
            }
        });
    }public void addColomn(){
        model.addColumn("Trs ID");
        model.addColumn("Pny ID");
        model.addColumn("Kost ID");
        model.addColumn("Tgl Masuk");
        model.addColumn("Tgl Keluar");
        model.addColumn("Total Bayar");
        model.addColumn("Status");
    }

    public void loaddata(){
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try{
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM tbTransaksiSewa WHERE trs_statusbayar=0";
            connection.result = connection.stat.executeQuery(query);
            while(connection.result.next()){
                Object[] obj = new Object[7];
                obj[0] = connection.result.getString(1);
                obj[1] = connection.result.getString(3);
                obj[2] = connection.result.getString(4);
                obj[3] = connection.result.getString(5);
                obj[4] = calculatedate(connection.result.getString(5),connection.result.getInt(6));
                obj[5] = String.valueOf(connection.result.getInt(7));

                if(connection.result.getInt(8)==0){
                    obj[6] = "Belum Lunas";
                }else{
                    obj[6] = "Lunas";
                }
                model.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        }catch(Exception ex){
            System.out.println("Terjadi error saat load data transaksi"+ex);
        }
    }

    public String calculatedate(String tglmasuk,int lamasewa){
        calendar.setTime(java.sql.Date.valueOf(tglmasuk));
        calendar.add(Calendar.MONTH,lamasewa);
        tglkeluar = formatter.format(calendar.getTime());
        return tglkeluar;
    }

    public void clear(){
        txtID.setText(null);
        loaddata();
    }
}
