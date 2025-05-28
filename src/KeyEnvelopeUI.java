import javax.swing.*;
import java.awt.*;

public class KeyEnvelopeUI extends JFrame {

    private JPanel rightPanel;

    public KeyEnvelopeUI() {
        setTitle("전자 봉투 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLayout(new GridLayout(1, 2, 20, 0)); // 좌우 분할

        // === 왼쪽 패널 ===
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

        // === 오른쪽 패널 ===
        rightPanel = new JPanel();
        rightPanel.setBackground(Color.LIGHT_GRAY);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        rightPanel.setVisible(false); // 초기에는 숨김

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

        // === 버튼 클릭 시 오른쪽 패널 표시 ===
        btnGetKey.addActionListener(e -> rightPanel.setVisible(true));

        // === 프레임에 추가 ===
        add(leftPanel);
        add(rightPanel);
        setLocationRelativeTo(null); // 중앙 정렬
        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(173, 242, 239));
        button.setFocusPainted(false);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        button.setMaximumSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JPanel makeLabeledField(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));
        panel.setMaximumSize(new Dimension(400, 30));
        panel.setBackground(Color.LIGHT_GRAY);
        textField.setBackground(new Color(220, 240, 240));
        panel.add(label, BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(KeyEnvelopeUI::new);
    }
}
