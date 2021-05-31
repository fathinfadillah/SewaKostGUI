package sewakost016;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu {
    private JPanel MainMenu;
    private JMenu MenuKelolaPenyewa;
    private JMenuItem MasterPenyewa;
    private JMenu MenuKelolaKost;
    private JMenuItem MasterKost;
    private JMenu MenuTransaksi;
    private JMenuItem MenuItemSewa;
    private JMenuItem MenuItemPembayaran;

    public MainMenu() {
        MasterKost.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MasterUnitKost a = new MasterUnitKost();
                a.apk();
            }
        });
        MasterPenyewa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sewakost016.MasterPenyewa a = new MasterPenyewa();
                a.apk();
            }
        });
        MenuItemSewa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SewaKost a = new SewaKost();
                a.apk();
            }
        });
        MenuItemPembayaran.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pembayaran a = new Pembayaran();
                a.apk();
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("APLIKASI SEWA KOST");
        frame.setContentPane(new MainMenu().MainMenu);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800,500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
