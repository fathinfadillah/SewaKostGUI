package sewakost016;

import com.toedter.calendar.JDateChooser;
import connection016.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.text.SimpleDateFormat;

public class MasterPenyewa {
    private JPanel MasterPenyewa;
    private JTextField txtCariID;
    private JButton btnCari;
    private JTextField txtID;
    private JTextField txtNama;
    private JTextField txtTelepon;
    private JPanel JPtgl;
    private JButton btnSimpan;
    private JButton btnUpdate;
    private JButton btnHapus;
    private JButton btnBatal;
    private JTable tblPenyewa;
    private DefaultTableModel model;

    DBConnect connection = new DBConnect();
    JDateChooser datechoos = new JDateChooser();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String id;
    String nama;
    String tgl;
    String telepon;
    Integer status=null;

    public MasterPenyewa() {
        JPtgl.add(datechoos);
        model = new DefaultTableModel();
        tblPenyewa.setModel(model);
        addColomn();
        clear();
        btnSimpan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = txtID.getText();
                nama = txtNama.getText();

                if(datechoos.getDate() == null){
                    tgl = null;
                }
                else {
                    tgl = formatter.format(datechoos.getDate());
                }

                telepon = txtTelepon.getText();
                status = 0;

                try{
                    if(validasinull()){
                        throw new Exception("Isi Semua Data !");
                    }
                    else {
                        try{
                            String query = "INSERT INTO tbPenyewa VALUES(?,?,?,?,?)";
                            connection.pstat = connection.conn.prepareStatement(query);
                            connection.pstat.setString(1,id);
                            connection.pstat.setString(2,nama);
                            connection.pstat.setString(3,tgl);
                            connection.pstat.setString(4,telepon);
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
                }
                catch (Exception ex){
                    JOptionPane.showMessageDialog(null,ex.getMessage());
                }
            }
        });
        btnCari.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.getDataVector().removeAllElements(); //menghapus semua data ditamp
                model.fireTableDataChanged(); //memberitahu data telah kosong

                try{
                    DBConnect connection = new DBConnect();
                    connection.stat = connection.conn.createStatement();
                    String query = "SELECT TOP (1) * FROM tbPenyewa WHERE pny_id = '" +txtCariID.getText()+"'";
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
        btnBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = txtID.getText();
                nama = txtNama.getText();
                tgl = formatter.format(datechoos.getDate());
                telepon = txtTelepon.getText();
                try{
                    String query = "UPDATE tbPenyewa SET pny_nama=?,pny_tgllahir=?,pny_telp=? WHERE pny_id=?";
                    connection.pstat = connection.conn.prepareStatement(query);
                    connection.pstat.setString(1,nama);
                    connection.pstat.setString(2,tgl);
                    connection.pstat.setString(3,telepon);
                    connection.pstat.setString(4,id);

                    connection.pstat.executeUpdate();
                    connection.pstat.close();
                }
                catch(Exception ex) {
                    System.out.println("Terjadi error pada saat update data"+ex);
                }
                JOptionPane.showMessageDialog(null,"Update Data Berhasil");
                clear();
                loaddata();
            }
        });
        btnHapus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = txtID.getText();
                try{
                    String query = "DELETE FROM tbPenyewa WHERE pny_id=?";
                    connection.pstat = connection.conn.prepareStatement(query);
                    connection.pstat.setString(1,id);

                    connection.pstat.executeUpdate();
                    connection.pstat.close();

                }
                catch(Exception ex) {
                    System.out.println("Terjadi error pada saat delete data"+ex);
                }
                JOptionPane.showMessageDialog(null,"Delete Data Berhasi;");
                clear();
                loaddata();
            }
        });
        tblPenyewa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tblPenyewa.getSelectedRow();
                txtID.setText((String) model.getValueAt(i,0));
                txtNama.setText((String) model.getValueAt(i,1));
                datechoos.setDateFormatString("yyyy-MM-dd");
                datechoos.setDate(java.sql.Date.valueOf(model.getValueAt(i,2).toString()));
                txtTelepon.setText((String) model.getValueAt(i,3));
            }
        });
        txtTelepon.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if(txtTelepon.getText().length() >= 13){
                    e.consume();
                }
                if ((e.getKeyChar() >= '0' && e.getKeyChar() <= '9') || e.getKeyChar() == '\b') {
                    txtTelepon.setEditable(true);
                }else {
                    txtTelepon.setEditable(false);
                }
            }
        });
    }

    public void addColomn(){
        model.addColumn("ID");
        model.addColumn("Nama");
        model.addColumn("Tanggal Lahir");
        model.addColumn("Telepon");
        model.addColumn("Status");
    }

    public void clear(){
        txtCariID.setText(null);
        txtID.setText(null);
        datechoos.setDate(null);
        txtNama.setText(null);
        txtTelepon.setText(null);
        autoid();
        loaddata();
    }

    public boolean validasinull(){
        if(nama.isEmpty() || tgl.isEmpty() || telepon.isEmpty())
        {
            return true;
        }else{
            return false;
        }
    }

    public void autoid() {
        try {
            String sql = "SELECT * FROM tbPenyewa ORDER BY pny_id desc";
            connection.stat = connection.conn.createStatement();
            connection.result = connection.stat.executeQuery(sql);
            if (connection.result.next()) {
                id = connection.result.getString("pny_id").substring(4);
                String AN = "" + (Integer.parseInt(id) + 1);
                String nol = "";

                if (AN.length() == 1) {
                    nol = "00";
                } else if (AN.length() == 2) {
                    nol = "0";
                } else if (AN.length() == 3) {
                    nol = "";
                }
                txtID.setText("PY" + nol + AN);
                txtNama.requestFocus();
                txtID.setEnabled(false);

            } else {
                txtID.setText("PY001");
                txtNama.requestFocus();
                txtID.setEnabled(false);
            }
            connection.stat.close();
            connection.result.close();
        } catch (Exception e1) {
            System.out.println("Terjadi error pada id penyewa: " + e1);
        }
    }

    public void loaddata() {
        model.getDataVector().removeAllElements();
        model.fireTableDataChanged();
        try{
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM tbPenyewa";
            connection.result = connection.stat.executeQuery(query);
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
            connection.stat.close();
            connection.result.close();
        }catch(Exception ex){
            System.out.println("Terjadi error saat load data"+ex);
        }
    }
    public void apk() {
        JFrame frame = new JFrame("MENU PENYEWA");
        frame.setContentPane(new MasterPenyewa().MasterPenyewa);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800,500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    public static void main (String[]args){
        JFrame frame = new JFrame("MasterPenyewa");
        frame.setContentPane(new MasterPenyewa().MasterPenyewa);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
