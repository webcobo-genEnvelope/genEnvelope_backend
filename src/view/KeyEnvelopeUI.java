package view;

import controller.KeyController;
import controller.RealLabController;
import controller.ReceiveController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;

class KeyEnvelopeUI extends JFrame {
    private static final String RESULT_FILE = "data/result.txt";
    private static final String SENDER_FILE = "data/sender.txt";

    private JPanel rightPanel;
    private final KeyController keyController = new KeyController();
    private final ReceiveController receiveController = new ReceiveController();
    private final JButton btnSendToCourt = new JButton("제3기관으로 전송");

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
        JButton btnSendFakeEnvelope = new JButton("위조기관 전자봉투 보내기");
        JButton btnGetEnvelope = new JButton("전자 봉투 받기");
        JButton btnCourtReceive = new JButton("제3기관(법원) 전자봉투 받기");

        styleButton(btnGetKey);
        styleButton(btnMakeEnvelope);
        styleButton(btnSendFakeEnvelope);
        styleButton(btnGetEnvelope);
        styleButton(btnCourtReceive);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(btnGetKey);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(btnMakeEnvelope);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(btnSendFakeEnvelope);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(btnGetEnvelope);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(btnCourtReceive);
        leftPanel.add(Box.createVerticalGlue());

        rightPanel = new JPanel();
        rightPanel.setBackground(Color.LIGHT_GRAY);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        add(leftPanel);
        add(rightPanel);
        setLocationRelativeTo(null);
        setVisible(true);

        btnGetKey.addActionListener(e -> showKeyPanel());
        btnMakeEnvelope.addActionListener(e -> showSendEnvelopePanel("data/certified_mark.png", "data/envelope.zip", false));
        btnSendFakeEnvelope.addActionListener(e -> showSendEnvelopePanel("data/fake_mark.png", "data/fake_envelope.zip", true));
        btnGetEnvelope.addActionListener(e -> showReceiveEnvelopePanel());
        btnCourtReceive.addActionListener(e -> showCourtReceivePanel());
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

        rightPanel.add(titleLabel);
        rightPanel.add(makeLabeledField("이름:", nameField));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(generateBtn);
        refreshRightPanel();
    }

    private void showSendEnvelopePanel(String markFile, String zipPath, boolean isFake) {
        rightPanel.removeAll();
        JLabel titleLabel = new JLabel("전자봉투 보내기");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField receiverField = new JTextField();
        JTextArea resultArea = new JTextArea(4, 20);
        resultArea.setLineWrap(true);

        JButton sendBtn = new JButton("보내기");
        styleButton(sendBtn);

        sendBtn.addActionListener(e -> {
            String receiver = receiverField.getText().trim();
            String resultContent = resultArea.getText().trim();
            if (receiver.isEmpty() || resultContent.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력해야 합니다!");
                return;
            }
            File pub = new File("data/" + receiver + "public");
            if (!pub.exists()) {
                JOptionPane.showMessageDialog(this, "⚠️ '" + receiver + "' 키가 없습니다. 먼저 키를 발급해주세요.");
                return;
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESULT_FILE))) {
                writer.write(resultContent);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "결과 파일 저장 실패!");
                return;
            }
            new RealLabController().create(receiver, RESULT_FILE, markFile, zipPath, isFake);
            JOptionPane.showMessageDialog(this, "📦 전자봉투 전송 완료!");
        });

        rightPanel.add(titleLabel);
        rightPanel.add(makeLabeledField("받는 사람:", receiverField));
        rightPanel.add(makeLabeledField("결과:", new JScrollPane(resultArea)));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(sendBtn);
        refreshRightPanel();
    }

    private void showReceiveEnvelopePanel() {
        rightPanel.removeAll();
        JLabel titleLabel = new JLabel("전자 봉투 받기");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));

        JTextField inputName = new JTextField();
        JTextField statusField = new JTextField();
        JTextArea contentArea = new JTextArea();
        JScrollPane contentScroll = new JScrollPane(contentArea);

        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        statusField.setEditable(false);
        contentArea.setEditable(false);

        JButton checkBtn = new JButton("검사 결과");
        JButton checkFakeBtn = new JButton("검사 결과(위조)");
        styleButton(checkBtn);
        styleButton(checkFakeBtn);
        styleButton(btnSendToCourt);
        btnSendToCourt.setVisible(false);

        checkBtn.addActionListener(e -> {
            String user = inputName.getText().trim();
            if (user.isEmpty()) {
                JOptionPane.showMessageDialog(this, "이름을 입력해주세요.");
                return;
            }
            String[] result = receiveController.verifyEnvelope(user);
            statusField.setText(result[0]);
            contentArea.setText(result[1]);
            btnSendToCourt.setVisible("진본 확인됨".equals(result[0]));

            for (ActionListener al : btnSendToCourt.getActionListeners()) {
                btnSendToCourt.removeActionListener(al);
            }

            btnSendToCourt.addActionListener(ev -> {
                try {
                    File pub = new File("data/courtpublic");
                    File pri = new File("data/courtprivate");
                    if (!pub.exists() || !pri.exists()) {
                        keyController.handleGenerateKeyAndSave("data/courtpublic", "data/courtprivate");
                    }
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESULT_FILE))) {
                        writer.write(contentArea.getText().trim());
                    }
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(SENDER_FILE))) {
                        writer.write(user);
                    }
                    new RealLabController().create("court", RESULT_FILE, "data/certified_mark.png", "data/court_envelope.zip", false);
                    JOptionPane.showMessageDialog(this, "⚖️ 법원 전송 완료!");
                    showCourtReceivePanel();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "❌ 전송 실패: " + ex.getMessage());
                }
            });

            refreshRightPanel();
        });

        checkFakeBtn.addActionListener(e -> {
            String user = inputName.getText().trim();
            if (user.isEmpty()) {
                JOptionPane.showMessageDialog(this, "이름을 입력해주세요.");
                return;
            }
            String[] result = receiveController.verifyFakeEnvelope(user);
            statusField.setText(result[0]);
            contentArea.setText(result[1]);
            btnSendToCourt.setVisible(false);
            refreshRightPanel();
        });

        rightPanel.add(titleLabel);
        rightPanel.add(makeLabeledField("이름 입력:", inputName));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(checkBtn);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(checkFakeBtn);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(makeLabeledField("진위 여부:", statusField));
        rightPanel.add(new JLabel("내용:"));
        rightPanel.add(contentScroll);
        rightPanel.add(btnSendToCourt);
        refreshRightPanel();
    }

    private void showCourtReceivePanel() {
        rightPanel.removeAll();
        JLabel titleLabel = new JLabel("제3기관 전자봉투 받기");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField senderField = new JTextField();
        senderField.setEditable(false);

        JTextField statusField = new JTextField();
        JTextArea contentArea = new JTextArea();
        JScrollPane contentScroll = new JScrollPane(contentArea);

        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        statusField.setEditable(false);
        contentArea.setEditable(false);

        File courtFile = new File("data/court_envelope.zip");
        if (!courtFile.exists()) {
            JLabel noFile = new JLabel("❌ 아직 전송된 전자봉투가 없습니다.");
            noFile.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            rightPanel.add(noFile);
            refreshRightPanel();
            return;
        }

        String[] result = receiveController.verify("court");
        statusField.setText(result[0]);
        contentArea.setText(result[1]);

        String sender = "(알 수 없음)";
        File senderInfo = new File(SENDER_FILE);
        if (senderInfo.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(senderInfo))) {
                sender = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        senderField.setText(sender);

        rightPanel.add(titleLabel);
        rightPanel.add(makeLabeledField("보낸 이:", senderField));
        rightPanel.add(makeLabeledField("진위 여부:", statusField));
        rightPanel.add(new JLabel("내용:"));
        rightPanel.add(contentScroll);
        refreshRightPanel();
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(173, 242, 239));
        button.setFocusPainted(false);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        button.setMaximumSize(new Dimension(260, 40));
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