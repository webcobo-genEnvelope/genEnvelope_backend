package view;

import controller.KeyController;
import controller.RealLabController;
import controller.ReceiveController;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class KeyEnvelopeUI extends JFrame {

    private JPanel rightPanel;
    private final KeyController keyController = new KeyController();
    private final ReceiveController receiveController = new ReceiveController();

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
        btnGetEnvelope.addActionListener(e -> showReceiveEnvelopePanel());
    }

    private void showKeyPanel() {
        rightPanel.removeAll();
        JLabel titleLabel = new JLabel("키 생성");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

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
                JOptionPane.showMessageDialog(this, "이름을 입력해주세요.");
                return;
            }
            boolean success = keyController.handleGenerateKeyAndSave("data/" + name + "public", "data/" + name + "private");
            JOptionPane.showMessageDialog(this,
                    success ? "🔐 키 생성 성공!" : "❌ 실패",
                    success ? "성공" : "오류",
                    success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        });

        refreshRightPanel();
    }

    private void showSendEnvelopePanel() {
        rightPanel.removeAll();

        JLabel titleLabel = new JLabel("전자봉투 보내기");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField receiverField = new JTextField();
        JTextField markField = new JTextField();
        JTextArea resultArea = new JTextArea(4, 20);
        resultArea.setLineWrap(true);

        JButton sendBtn = new JButton("보내기");
        styleButton(sendBtn);

        rightPanel.add(titleLabel);
        rightPanel.add(makeLabeledField("받는 사람 :", receiverField));
        rightPanel.add(makeLabeledField("인증 마크:", markField));
        rightPanel.add(makeLabeledField("결과 :", new JScrollPane(resultArea)));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(sendBtn);

        sendBtn.addActionListener(e -> {
            String receiver = receiverField.getText().trim();
            String markPath = markField.getText().trim();
            String resultContent = resultArea.getText().trim();
            if (receiver.isEmpty() || markPath.isEmpty() || resultContent.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력해야 합니다!");
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/result.txt"))) {
                writer.write(resultContent);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "결과 파일 저장 실패!");
                return;
            }

            new RealLabController().create(receiver, "data/result.txt", markPath);
            JOptionPane.showMessageDialog(this, "📦 전자봉투 전송 완료!");
        });

        refreshRightPanel();
    }

    private void showReceiveEnvelopePanel() {
        rightPanel.removeAll();

        JLabel titleLabel = new JLabel("전자 봉투 받기");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField inputName = new JTextField();
        JTextField nameField = new JTextField();
        JTextField statusField = new JTextField();
        JTextArea contentArea = new JTextArea();
        JScrollPane contentScroll = new JScrollPane(contentArea);

        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentScroll.setPreferredSize(new Dimension(200, 80));
        nameField.setEditable(false);
        statusField.setEditable(false);
        contentArea.setEditable(false);

        Color bg = new Color(220, 240, 240);
        inputName.setBackground(bg);
        nameField.setBackground(bg);
        statusField.setBackground(bg);
        contentArea.setBackground(bg);

        JButton checkBtn = new JButton("검사 결과");
        JButton downloadBtn = new JButton("다운로드");
        styleButton(checkBtn);
        styleButton(downloadBtn);

        rightPanel.add(makeLabeledField("이름 입력:", inputName));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(checkBtn);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(makeLabeledField("진위 여부:", statusField));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.LIGHT_GRAY);
        contentPanel.setMaximumSize(new Dimension(400, 100));
        contentPanel.add(new JLabel("내용:"), BorderLayout.NORTH);
        contentPanel.add(contentScroll, BorderLayout.CENTER);
        rightPanel.add(contentPanel);

        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(downloadBtn);

        checkBtn.addActionListener(e -> {
            String user = inputName.getText().trim();
            if (user.isEmpty()) {
                JOptionPane.showMessageDialog(this, "이름을 입력해주세요.");
                return;
            }
            String[] result = receiveController.verifyEnvelope(user);
            nameField.setText(user);
            statusField.setText(result[0]);
            contentArea.setText(result[1]);
        });

        downloadBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "📥 다운로드 완료!"));

        refreshRightPanel();
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
        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void refreshRightPanel() {
        rightPanel.setVisible(true);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(KeyEnvelopeUI::new);
    }
}
