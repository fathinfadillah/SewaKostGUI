package sewakost016;

import com.toedter.calendar.JDateChooser;
import connection016.DBConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SewaKost {
    private JPanel SewaKost;
    private JTextField txtTrsID;
    private JTextField txtPnyID;
    private JTextField txtKostID;
    private JPanel JCalendar;
    private JTextField txtLamaSewa;
    private JButton btnSimpan;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnCancel;
    private JTextField txtTotal;
    private JTextField bxtCariKost;
    private JButton btnCariKost;
    private JTable tbKost;
    private JTextField bxtCariID;
    private JButton btnCariPenyewa;
    private JTable tbPenyewa;
    private JTable tbTransaksi;
    private DefaultTableModel modelpenyewa;
    private DefaultTableModel modelkost;
    private DefaultTableModel modeltransaksi;
    private DefaultTableModel modelreset;

    DBConnect connection = new DBConnect();
    JDateChooser datechoos = new JDateChooser();
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    int autoid;
    int total;
    int lamasewa;
    int status;
    int trsid;
    String pnyid;
    String kostid;
    String tglmasuk;
    String tglkeluar;
    String tgltransaksi;

    public void apk(){
        JFrame frame = new JFrame("MENU KELOLA SEWA");
        frame.setContentPane(new SewaKost().SewaKost);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1000,500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MENU KELOLA SEWA");
        frame.setContentPane(new SewaKost().SewaKost);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1000,500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public SewaKost() {
        modelreset = new DefaultTableModel();

        modelpenyewa = new DefaultTableModel();
        tbPenyewa.setModel(modelpenyewa);

        modelkost = new DefaultTableModel();
        tbKost.setModel(modelkost);

        modeltransaksi = new DefaultTableModel();
        tbTransaksi.setModel(modeltransaksi);

        JCalendar.add(datechoos);
        addColomn();
        clear();

        tbPenyewa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbPenyewa.getSelectedRow();
                txtPnyID.setText((String) modelpenyewa.getValueAt(i,0));
            }
        });
        tbKost.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbKost.getSelectedRow();
                txtKostID.setText((String) modelkost.getValueAt(i,0));
            }
        });
        tbTransaksi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int i = tbTransaksi.getSelectedRow();
                Date a = java.sql.Date.valueOf(modeltransaksi.getValueAt(i,4).toString());
                Date b = java.sql.Date.valueOf(modeltransaksi.getValueAt(i,3).toString());
                Date c = new Date(a.getTime() - b.getTime());
                txtTrsID.setText((String) modeltransaksi.getValueAt(i,0));
                txtPnyID.setText((String) modeltransaksi.getValueAt(i,1));
                txtKostID.setText((String) modeltransaksi.getValueAt(i,2));
                datechoos.setDate(java.sql.Date.valueOf(modeltransaksi.getValueAt(i,3).toString()));
                lamasewa = c.getMonth();
                txtLamaSewa.setText(String.valueOf(lamasewa));
                txtTotal.setText((String) modeltransaksi.getValueAt(i,5));
                if(modeltransaksi.getValueAt(i,6).equals("Paid")){
                    datechoos.setEnabled(false);
                    txtLamaSewa.setEnabled(false);
                    txtTotal.setEnabled(false);
                }
            }
        });
        txtLamaSewa.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                txtLamaSewa.setEditable(true);
            }
        });
        txtLamaSewa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if(txtLamaSewa.getText().length() == 0 && e.getKeyChar() == '0'){
                    e.consume();
                }
                if(txtLamaSewa.getText().length() >= 10){
                    e.consume();
                }
            }
        });
        txtLamaSewa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if ((e.getKeyChar() >= '0' && e.getKeyChar() <= '9') || e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                    txtLamaSewa.setEditable(true);
                }else {
                    txtLamaSewa.setEditable(false);
                }
            }
        });
        txtTotal.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if(txtTotal.getText().length() == 0 && e.getKeyChar() == '0'){
                    e.consume();
                }
                if(txtTotal.getText().length() >= 10){
                    e.consume();
                }
            }
        });
        txtTotal.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if ((e.getKeyChar() >= '0' && e.getKeyChar() <= '9') || e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                    txtTotal.setEditable(true);
                }else {
                    txtTotal.setEditable(false);
                }
            }
        });
        txtTotal.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                total = 0;
                if(txtLamaSewa.getText().isEmpty() || txtKostID.getText().isEmpty()){
                    txtTotal.setText("0");
                }else{
                    try {
                        DBConnect connection2 = new DBConnect();
                        connection2.stat = connection2.conn.createStatement();
                        String query2 = "SELECT kost_hargasewa FROM tbUnitKost WHERE kost_id= '"+ txtKostID.getText()+"'";;
                        connection2.result = connection2.stat.executeQuery(query2);

                        while(connection2.result.next()) {
                            total = connection2.result.getInt(1);
                        }

                        connection2.stat.close();
                        connection2.result.close();
                    }catch (Exception ex){
                        System.out.println("Terjadi error saat cari harga kost"+ex);
                    }
                    total = total * Integer.parseInt(txtLamaSewa.getText());
                    txtTotal.setText(String.valueOf(total));
                }
            }
        });
        txtTotal.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                txtTotal.setEditable(true);
            }
        });
        btnCariPenyewa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelpenyewa.getDataVector().removeAllElements(); //menghapus semua data ditamp
                modelpenyewa.fireTableDataChanged(); //memberitahu data telah kosong

                try{
                    DBConnect connection = new DBConnect();
                    connection.stat = connection.conn.createStatement();
                    String query = "SELECT TOP (1) * FROM tbPenyewa WHERE pny_id = '" + bxtCariID.getText()+"' AND pny_status=0";
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
                        modelpenyewa.addRow(obj);
                    }
                    //jika di tabel tidak ada data yang dicari
                    if (modelpenyewa.getRowCount() == 0){
                        JOptionPane.showMessageDialog(null, "Data tidak ditemukan");
                    }
                    connection.stat.close();
                    connection.result.close();
                } catch (Exception ex){
                    System.out.println("Terjadi error saat cari data" + e);
                }
            }
        });
        btnCariKost.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelkost.getDataVector().removeAllElements(); //menghapus semua data ditamp
                modelkost.fireTableDataChanged(); //memberitahu data telah kosong

                try{
                    DBConnect connection = new DBConnect();
                    connection.stat = connection.conn.createStatement();
                    String query = "SELECT TOP (1) * FROM tbUnitKost WHERE kost_id = '" + bxtCariID.getText()+"' AND kost_status=0";
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
                        modelkost.addRow(obj);
                    }
                    //jika di tabel tidak ada data yang dicari
                    if (modelkost.getRowCount() == 0){
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
                trsid = Integer.parseInt(txtTrsID.getText());
                pnyid = txtPnyID.getText();
                kostid = txtKostID.getText();
                tgltransaksi = formatter.format(new Date());

                if(datechoos.getDate() == null){
                    tglmasuk = null;
                }else{
                    tglmasuk = formatter.format(datechoos.getDate());
                }

                if(!(txtLamaSewa.getText().isEmpty())){
                    lamasewa = Integer.parseInt(txtLamaSewa.getText());
                }

                if(!(txtTotal.getText().isEmpty())){
                    total = Integer.parseInt(txtTotal.getText());
                }
                status = 0;

                try{
                    if(validasinull()){
                        throw new Exception("Harap ISI Semua !!!!");
                    }else{
                        try{
                            String query = "INSERT INTO tbTransaksiSewa VALUES(?,?,?,?,?,?,?,?)";
                            connection.pstat = connection.conn.prepareStatement(query);
                            connection.pstat.setInt(1,trsid);
                            connection.pstat.setString(2,tgltransaksi);
                            connection.pstat.setString(3,pnyid);
                            connection.pstat.setString(4,kostid);
                            connection.pstat.setString(5,tglmasuk);
                            connection.pstat.setInt(6,lamasewa);
                            connection.pstat.setInt(7,total);
                            connection.pstat.setInt(8,status);
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
                trsid = Integer.parseInt(txtTrsID.getText());
                pnyid = txtPnyID.getText();
                kostid = txtKostID.getText();
                tgltransaksi = formatter.format(new Date());

                if(datechoos.getDate() == null){
                    tglmasuk = null;
                }else{
                    tglmasuk = formatter.format(datechoos.getDate());
                }

                if(!(txtLamaSewa.getText().isEmpty())){
                    lamasewa = Integer.parseInt(txtLamaSewa.getText());
                }

                if(!(txtTotal.getText().isEmpty())){
                    total = Integer.parseInt(txtTotal.getText());
                }
                status = 0;

                try{
                    if(validasinull()){
                        throw new Exception("Harap ISI Semua !!!!");
                    }else{

                        try {
                            DBConnect connection2 = new DBConnect();
                            connection2.stat = connection2.conn.createStatement();
                            String query2 = "SELECT trs_status FROM tbTransaksiSewa WHERE trs_id='"+trsid+"'";
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
                            String query = "UPDATE tbTransaksiSewa SET pny_id=?,kost_id=?,trs_tglmasuk=?,trs_lamasewa=?,trs_totalbayar=?,trs_statusbayar=? WHERE trs_id=?";
                            connection.pstat = connection.conn.prepareStatement(query);
                            connection.pstat.setString(1,pnyid);
                            connection.pstat.setString(2,kostid);
                            connection.pstat.setString(3,tglmasuk);
                            connection.pstat.setInt(4,lamasewa);
                            connection.pstat.setInt(5,total);
                            connection.pstat.setInt(6,status);
                            connection.pstat.setInt(7,trsid);
                            connection.pstat.executeUpdate();
                            connection.pstat.close();
                        }
                        catch(Exception ex) {
                            System.out.println("Terjadi error pada saat update data"+ex);
                        }
                        JOptionPane.showMessageDialog(null,"Insert Data Berhasil");
                        clear();
                    }
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null,ex.getMessage());
                }
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trsid = Integer.parseInt(txtTrsID.getText());
                try {
                    String query = "DELETE FROM tbTransaksiSewa WHERE trs_id=?";
                    connection.pstat = connection.conn.prepareStatement(query);
                    connection.pstat.setInt(1, trsid);

                    connection.pstat.executeUpdate();
                    connection.pstat.close();
                } catch (Exception ex) {
                    System.out.println("Terjadi error pada saat delete data" + ex);
                }
                JOptionPane.showMessageDialog(null, "Delete Data Berhasil");
                clear();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
    }

    public void autoreset(){
        modelreset.getDataVector().removeAllElements();
        modelreset.fireTableDataChanged();

        modelreset.addColumn("PNY ID");
        modelreset.addColumn("KOST ID");
        status = 0;
        try {
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT pny_id,kost_id FROM tbTransaksiSewa WHERE trs_statusbayar=1 AND GETDATE() >= DATEADD(MONTH,trs_lamasewa,trs_tglmasuk)";
            connection.result = connection.stat.executeQuery(query);

            while(connection.result.next()) {
                Object[] obj = new Object[3];
                obj[0] = connection.result.getString(1);
                obj[1] = connection.result.getString(2);
                modelreset.addRow(obj);
            }

            connection.stat.close();
            connection.result.close();
        }catch (Exception ex){
            System.out.println("Terjadi error saat cari reset data"+ex);
        }

        int resetrow = modelreset.getRowCount();

//        JOptionPane.showMessageDialog(null,resetrow);
//        JOptionPane.showMessageDialog(null,modelreset.getDataVector().get(0).get(0));
        for(int i=0;i<resetrow;i++){
            pnyid = (String) modelreset.getDataVector().get(i).get(0);
            kostid = (String) modelreset.getDataVector().get(i).get(1);

            try{
                String query1 = "UPDATE tbPenyewa SET pny_status=? WHERE pny_id=?";
                connection.pstat = connection.conn.prepareStatement(query1);
                connection.pstat.setInt(1,status);
                connection.pstat.setString(2,pnyid);
                connection.pstat.executeUpdate();
                connection.pstat.close();

                String query2 = "UPDATE tbUnitKost SET kost_status=? WHERE kost_id=?";
                connection.pstat = connection.conn.prepareStatement(query2);
                connection.pstat.setInt(1,status);
                connection.pstat.setString(2,kostid);
                connection.pstat.executeUpdate();
                connection.pstat.close();
            }
            catch(Exception ex) {
                System.out.println("Terjadi error pada saat reset data"+ex);
            }

        }

    }

    public void autoid(){
        try{
            String query = "SELECT TOP (1) MAX(trs_id) + 1 FROM tbTransaksiSewa";
            connection.pstat = connection.conn.prepareStatement(query);
            connection.result = connection.pstat.executeQuery();
            while(connection.result.next()){
                if(connection.result.getString(1)==null){
                    autoid = 1;
                }else{
                    autoid =Integer.parseInt(connection.result.getString(1));
                }
            }
            txtTrsID.setText(String.valueOf(autoid));
            connection.pstat.close();
            connection.result.close();
        }
        catch(Exception ex) {
            System.out.println("Terjadi error pada saat generate id data "+ex);
        }
    }

    public void addColomn(){
        modelpenyewa.addColumn("ID");
        modelpenyewa.addColumn("Nama");
        modelpenyewa.addColumn("Tanggal Lahir");
        modelpenyewa.addColumn("NO Telephone");
        modelpenyewa.addColumn("Status");

        modelkost.addColumn("ID");
        modelkost.addColumn("Deskripsi");
        modelkost.addColumn("Fasilitas");
        modelkost.addColumn("Harga");
        modelkost.addColumn("Status");

        modeltransaksi.addColumn("Trs ID");
        modeltransaksi.addColumn("Pny ID");
        modeltransaksi.addColumn("Kost ID");
        modeltransaksi.addColumn("Tgl Masuk");
        modeltransaksi.addColumn("Tgl Keluar");
        modeltransaksi.addColumn("Total Bayar");
        modeltransaksi.addColumn("Status");
    }

    public void loaddata(){
        modelpenyewa.getDataVector().removeAllElements();
        modelpenyewa.fireTableDataChanged();
        try{
            DBConnect connection = new DBConnect();
            connection.stat = connection.conn.createStatement();
            String query = "SELECT * FROM tbPenyewa WHERE pny_status = 0";
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
                modelpenyewa.addRow(obj);
            }
            connection.stat.close();
            connection.result.close();
        }catch(Exception ex){
            System.out.println("Terjadi error saat load data penyewa"+ex);
        }

        modelkost.getDataVector().removeAllElements();
        modelkost.fireTableDataChanged();
        try{
            DBConnect connection2 = new DBConnect();
            connection2.stat = connection2.conn.createStatement();
            String query2 = "SELECT * FROM tbUnitKost WHERE kost_status = 0";
            connection2.result = connection.stat.executeQuery(query2);
            while(connection2.result.next()){
                Object[] obj = new Object[5];
                obj[0] = connection2.result.getString(1);
                obj[1] = connection2.result.getString(2);
                obj[2] = connection2.result.getString(3);
                obj[3] = String.valueOf(connection2.result.getInt(4));

                if(connection2.result.getInt(5)==0){
                    obj[4] = "Tersedia";
                }else{
                    obj[4] = "Tidak Tersedia";
                }
                modelkost.addRow(obj);
            }
            connection2.stat.close();
            connection2.result.close();
        }catch(Exception ex){
            System.out.println("Terjadi error saat load data kost"+ex);
        }

        modeltransaksi.getDataVector().removeAllElements();
        modeltransaksi.fireTableDataChanged();
        try{
            DBConnect connection3 = new DBConnect();
            connection3.stat = connection3.conn.createStatement();
            String query3 = "SELECT * FROM tbTransaksiSewa";
            connection3.result = connection.stat.executeQuery(query3);
            while(connection3.result.next()){
                Object[] obj = new Object[7];
                obj[0] = connection3.result.getString(1);
                obj[1] = connection3.result.getString(3);
                obj[2] = connection3.result.getString(4);
                obj[3] = connection3.result.getString(5);
                obj[4] = calculatedate(connection3.result.getString(5),connection3.result.getInt(6));
                obj[5] = String.valueOf(connection3.result.getInt(7));

                if(connection3.result.getInt(8)==0){
                    obj[6] = "Belum Lunas";
                }else{
                    obj[6] = "Lunas";
                }
                modeltransaksi.addRow(obj);
            }
            connection3.stat.close();
            connection3.result.close();
        }catch(Exception ex){
            System.out.println("Terjadi error saat load data transaksi"+ex);
        }
    }

    public void clear(){
        datechoos.setEnabled(true);
        txtLamaSewa.setEnabled(true);
        txtTotal.setEnabled(true);
        txtTrsID.setText(null);
        txtPnyID.setText(null);
        txtKostID.setText(null);
        datechoos.setDate(null);
        txtLamaSewa.setText(null);
        txtTotal.setText(null);
        bxtCariID.setText(null);
        bxtCariKost.setText(null);
        autoreset();
        autoid();
        loaddata();
    }

    public boolean validasinull(){
        if(pnyid.isEmpty() || kostid.isEmpty() || datechoos.getDate() == null || lamasewa == 0 || total == 0){
            return true;
        }else{
            return false;
        }
    }

    public String calculatedate(String tglmasuk,int lamasewa){
        calendar.setTime(java.sql.Date.valueOf(tglmasuk));
        calendar.add(Calendar.MONTH,lamasewa);
        tglkeluar = formatter.format(calendar.getTime());
        return tglkeluar;
    }

}
