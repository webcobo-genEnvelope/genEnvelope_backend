package view;

import controller.KeyController;
import controller.RealLabController;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class KeyEnvelopeUI extends JFrame {

    private JPanel rightPanel;
    private final KeyController keyController = new KeyController();

    public KeyEnvelopeUI() {
        setTitle("전자 봉투 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLayout(new GridLayout(1, 2, 20, 0));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.LIGHT_GRAY);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(60, 20, 60, 20));

        JButton btnGetKey = new JButton("키 발급받기");
        JButton btnMakeEnvelope = new JButton("검사기관 전자봉투 보내기");
        JButton btnGetEnvelope = new JButton("전자 봉투 받기");

        styleButton(btnGetKey);
        styleButton(btnMakeEnvelope);
        styleButton(btnGetEnvelope);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(btnGetKey);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(btnMakeEnvelope);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(btnGetEnvelope);
        leftPanel.add(Box.createVerticalGlue());

        rightPanel = new JPanel();
        rightPanel.setBackground(Color.LIGHT_GRAY);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        rightPanel.setVisible(false);

        add(leftPanel);
        add(rightPanel);
        setLocationRelativeTo(null);
        setVisible(true);

        btnGetKey.addActionListener(e -> showKeyPanel());
        btnMakeEnvelope.addActionListener(e -> showSendEnvelopePanel());
    }

    private void showKeyPanel() {
        rightPanel.removeAll();

        JLabel titleLabel = new JLabel("키 생성");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JTextField nameField = new JTextField();
        JButton generateBtn = new JButton("생성");
        styleButton(generateBtn);
        generateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightPanel.add(titleLabel);
        rightPanel.add(makeLabeledField("이름:", nameField));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(generateBtn);

        generateBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "이름을 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String privatePath = "data/" + name + "private";
            String publicPath = "data/" + name + "public";

            boolean success = keyController.handleGenerateKeyAndSave(publicPath, privatePath);
            if (success) {
                JOptionPane.showMessageDialog(this, "🔐 키 생성 및 저장 성공!\n\n" +
                                "📁 공개키: " + publicPath + "\n📁 비밀키: " + privatePath,
                        "성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "❌ 키 생성/저장 실패!", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        rightPanel.setVisible(true);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void showSendEnvelopePanel() {
        rightPanel.removeAll();

        JLabel titleLabel = new JLabel("전자봉투 보내기");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JTextField receiverField = new JTextField();
        JTextField markField = new JTextField();
        JTextArea resultArea = new JTextArea(4, 20);

        JButton sendBtn = new JButton("보내기");
        styleButton(sendBtn);

        rightPanel.add(titleLabel);
        rightPanel.add(makeLabeledField("받는 사람 :", receiverField));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(makeLabeledField("인증 마크:", markField));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(Color.LIGHT_GRAY);
        resultArea.setBackground(new Color(220, 240, 240));
        resultPanel.setMaximumSize(new Dimension(400, 100));
        resultPanel.add(new JLabel("결과 :"), BorderLayout.NORTH);
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        rightPanel.add(resultPanel);

        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(sendBtn);

        sendBtn.addActionListener(e -> {
            String receiver = receiverField.getText().trim();
            String markPath = markField.getText().trim();
            String resultContent = resultArea.getText().trim();

            if (receiver.isEmpty() || markPath.isEmpty() || resultContent.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력해야 합니다!", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 결과 텍스트 -> result.txt 저장
            String resultPath = "data/result.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultPath))) {
                writer.write(resultContent);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "결과 파일 저장 실패!", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            RealLabController controller = new RealLabController();
            controller.create(receiver, resultPath, markPath); // 사용자 이름을 전달하여 공개키 사용

            JOptionPane.showMessageDialog(this, "📦 전자봉투 생성 및 전송 완료!");
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

    private JPanel makeLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));
        panel.setMaximumSize(new Dimension(400, 40));
        panel.setBackground(Color.LIGHT_GRAY);
        field.setBackground(new Color(220, 240, 240));
        field.setPreferredSize(new Dimension(200, 25));
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(KeyEnvelopeUI::new);
    }
}
