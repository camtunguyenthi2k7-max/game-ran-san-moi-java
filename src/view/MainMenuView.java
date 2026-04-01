package view;

import controller.FileStorageService;
import controller.SnakeController;
import model.PlayerRecord;
import model.SnakeModel;
import utils.GameException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainMenuView extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> cbDifficulty;
    private JTextField txtName;
    private FileStorageService storageService;

    public MainMenuView() {
        storageService = new FileStorageService();
        setTitle("Quản Lý Game Rắn Săn Mồi - Nhóm 8");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10)); // Nền trắng mặc định

        initComponents();
        loadDataToTable();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        
        topPanel.add(new JLabel("Tên:"));
        txtName = new JTextField(12);
        topPanel.add(txtName);

        topPanel.add(new JLabel("Độ khó:"));
        cbDifficulty = new JComboBox<>(new String[]{"Dễ", "Trung Bình", "Khó"});
        topPanel.add(cbDifficulty);

        JButton btnPlay = new JButton("Chơi Ngay");
        btnPlay.setBackground(new Color(46, 204, 113));
        btnPlay.setForeground(Color.WHITE);
        btnPlay.addActionListener(e -> startGame());
        topPanel.add(btnPlay);

        JButton btnEdit = new JButton("Sửa Tên");
        btnEdit.setBackground(new Color(52, 152, 219));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.addActionListener(e -> updateRecord());
        topPanel.add(btnEdit);

        JButton btnDelete = new JButton("Xóa Kỷ Lục");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(e -> deleteRecord());
        topPanel.add(btnDelete);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Người Chơi", "Điểm Số", "Độ Khó"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadDataToTable() {
        tableModel.setRowCount(0);
        try {
            List<PlayerRecord> records = storageService.readAllRecords();
            records.sort((r1, r2) -> Integer.compare(r2.getScore(), r1.getScore()));
            for (PlayerRecord rec : records) {
                tableModel.addRow(new Object[]{rec.getPlayerName(), rec.getScore(), rec.getDifficulty()});
            }
        } catch (GameException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startGame() {
        String name = txtName.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên để bắt đầu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!name.matches("^[\\p{L}\\s]+$")) {
            JOptionPane.showMessageDialog(this, "Tên gì kỳ vậy? Chỉ được nhập chữ cái thôi, không dùng số hay ký tự đặc biệt nhé!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return; 
        }

        String difficulty = cbDifficulty.getSelectedItem().toString();
        this.setVisible(false); 

        JFrame gameFrame = new JFrame("Rắn Săn Mồi - Đang chơi: " + name);
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameFrame.setResizable(false);

        SnakeModel snakeModel = new SnakeModel();
        SnakeView snakeView = new SnakeView(snakeModel);
        
        int delay = difficulty.equals("Khó") ? 60 : (difficulty.equals("Trung Bình") ? 100 : 150);
        
        int levelNumber = 1; 
        if (difficulty.equals("Trung Bình")) {
            levelNumber = 2;
        } else if (difficulty.equals("Khó")) {
            levelNumber = 3;
        }

        new SnakeController(snakeModel, snakeView, delay, levelNumber);

        gameFrame.add(snakeView);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);

        gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    storageService.addRecord(new PlayerRecord(name, snakeModel.getScore(), difficulty));
                    loadDataToTable(); 
                } catch (GameException ex) {
                    JOptionPane.showMessageDialog(MainMenuView.this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                MainMenuView.this.setVisible(true);
            }
        });
    }

    private void updateRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một kỷ lục để sửa tên!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentName = (String) tableModel.getValueAt(selectedRow, 0);
        String newName = JOptionPane.showInputDialog(this, "Nhập tên người chơi mới:", currentName);
        
        if (newName != null && !newName.trim().isEmpty()) {
            if (!newName.matches("^[\\p{L}\\s]+$")) {
                JOptionPane.showMessageDialog(this, "Tên mới cũng chỉ được nhập chữ cái thôi nhé!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int score = (int) tableModel.getValueAt(selectedRow, 1);
                String diff = (String) tableModel.getValueAt(selectedRow, 2);
                storageService.updateRecord(selectedRow, new PlayerRecord(newName.trim(), score, diff));
                loadDataToTable();
            } catch (GameException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa kỷ lục này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                storageService.deleteRecord(selectedRow);
                loadDataToTable();
            } catch (GameException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}