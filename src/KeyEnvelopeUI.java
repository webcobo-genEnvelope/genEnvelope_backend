package view;

import controller.KeyController;

import javax.swing.*;
import java.awt.*;

public class KeyEnvelopeUI extends JFrame {

    private JPanel rightPanel;
    private final KeyController keyController = new KeyController();

    public KeyEnvelopeUI() {
        setTitle("전자 봉투 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLayout(new GridLayout(1, 2, 20, 0));

        // 왼쪽 메뉴
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.LIGHT_GRAY);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(60, 20, 60, 20));

        JButton btnGetKey = new JButton("키 발급받기");
        JButton btnGetEnvelope = new JButton("전자 봉투 받기");

        styleButton(btnGetKey);
        styleButton(btnGetEnvelope);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(btnGetKey);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(btnGetEnvelope);
        leftPanel.add(Box.createVerticalGlue());

        // 오른쪽 패널
        rightPanel = new JPanel();
        rightPanel.setBackground(Color.LIGHT_GRAY);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        rightPanel.setVisible(false);

        add(leftPanel);
        add(rightPanel);
        setLocationRelativeTo(null);
        setVisible(true);

        // 이벤트 연결
        btnGetKey.addActionListener(e -> showKeyPanel());
        btnGetEnvelope.addActionListener(e -> showEnvelopePanel());
    }

    private void showKeyPanel() {
        rightPanel.removeAll();

        JLabel titleLabel = new JLabel("키 생성");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JTextField nameField = new JTextField();
        JTextField privateKeyField = new JTextField();
        JTextField publicKeyField = new JTextField();

        JButton generateBtn = new JButton("생성");
        styleButton(generateBtn);
        generateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightPanel.add(titleLabel);
        rightPanel.add(makeLabeledField("이름:", nameField));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(makeLabeledField("비밀키 파일 이름:", privateKeyField));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(makeLabeledField("공개키 파일 이름:", publicKeyField));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(generateBtn);

        generateBtn.addActionListener(e -> {
            String privatePath = privateKeyField.getText().trim();
            String publicPath = publicKeyField.getText().trim();

            if (privatePath.isEmpty() || publicPath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 입력 칸을 채워주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = keyController.handleGenerateKeyAndSave(publicPath, privatePath);
            if (success) {
                JOptionPane.showMessageDialog(this, "🔐 키 생성 및 저장 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "❌ 키 생성/저장 실패!", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        rightPanel.setVisible(true);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void showEnvelopePanel() {
        rightPanel.removeAll();

        JLabel title = new JLabel("전자 봉투 받기");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton checkResultBtn = new JButton("검사 결과");
        styleButton(checkResultBtn);

        JTextField statusField = new JTextField();
        statusField.setEditable(false);
        JTextArea contentArea = new JTextArea(3, 20);
        contentArea.setEditable(false);

        JButton downloadBtn = new JButton("다운로드");
        styleButton(downloadBtn);

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(title);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(checkResultBtn);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(makeLabeledField("진위 여부:", statusField));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(makeLabeledField("내용:", contentArea)); // ⬅ JScrollPane 제거됨
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(downloadBtn);
        rightPanel.add(Box.createVerticalGlue());

        checkResultBtn.addActionListener(e -> {
            statusField.setText("진본 확인됨");
            contentArea.setText("DNA 검사 결과 이상 없음");
        });

        downloadBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "📥 다운로드 완료!");
        });

        rightPanel.setVisible(true);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(173, 242, 239));
        button.setFocusPainted(false);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        button.setMaximumSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JPanel makeLabeledField(String labelText, Component field) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));
        panel.setMaximumSize(new Dimension(400, 40)); // <-- 높이 줄임
        panel.setBackground(Color.LIGHT_GRAY);

        if (field instanceof JComponent) {
            ((JComponent) field).setBackground(new Color(220, 240, 240));
            ((JComponent) field).setPreferredSize(new Dimension(200, 25)); // <-- 고정 높이 적용
        }

        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(KeyEnvelopeUI::new);
    }
}
