import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.io.*;
import javax.sound.sampled.*;

public class SmartDigitalClockPro extends JFrame {

    private JLabel timeLabel, dateLabel, alarmStatusLabel, quoteLabel, stopwatchLabel;
    private javax.swing.Timer timer;
    private boolean glow = true, is24Hour = false;
    private String alarmTime = "";
    private boolean darkMode = false;

    private long stopwatchStart = 0;
    private boolean stopwatchRunning = false;

    private Color gradientStart = new Color(25, 25, 112);
    private Color gradientEnd = new Color(123, 31, 162);

    private static final List<String> QUOTES = Arrays.asList(
            "Time is what we want most, but use worst.",
            "Lost time is never found again.",
            "The key is in not spending time, but investing it.",
            "Your time is limited, don’t waste it living someone else’s life.",
            "Every second is of infinite value."
    );

    public SmartDigitalClockPro() {
        setTitle("⏰ Smart Digital Clock Pro");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, gradientStart, getWidth(), getHeight(), gradientEnd);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Labels
        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
        timeLabel.setForeground(Color.CYAN);

        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        dateLabel.setForeground(Color.WHITE);

        quoteLabel = new JLabel(randomQuote(), SwingConstants.CENTER);
        quoteLabel.setFont(new Font("Candara", Font.ITALIC, 20));
        quoteLabel.setForeground(Color.LIGHT_GRAY);

        stopwatchLabel = new JLabel("", SwingConstants.CENTER);
        stopwatchLabel.setFont(new Font("Consolas", Font.BOLD, 24));
        stopwatchLabel.setForeground(Color.ORANGE);

        alarmStatusLabel = new JLabel("No Alarm Set", SwingConstants.CENTER);
        alarmStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        alarmStatusLabel.setForeground(Color.LIGHT_GRAY);

        // Buttons
        JButton toggleFormatBtn = new JButton("12/24 Hr");
        JButton themeBtn = new JButton("Theme");
        JButton alarmBtn = new JButton("Set Alarm");
        JButton calendarBtn = new JButton("📅 Calendar");
        JButton stopwatchBtn = new JButton("⏱ Stopwatch");

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(toggleFormatBtn);
        btnPanel.add(themeBtn);
        btnPanel.add(alarmBtn);
        btnPanel.add(calendarBtn);
        btnPanel.add(stopwatchBtn);

        panel.add(dateLabel, BorderLayout.NORTH);
        panel.add(timeLabel, BorderLayout.CENTER);
        panel.add(quoteLabel, BorderLayout.WEST);
        panel.add(alarmStatusLabel, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        // Timer to update clock
        timer = new javax.swing.Timer(1000, e -> updateClock());
        timer.start();

        // Button actions
        toggleFormatBtn.addActionListener(e -> is24Hour = !is24Hour);
        themeBtn.addActionListener(e -> toggleTheme());
        alarmBtn.addActionListener(e -> setAlarm());
        calendarBtn.addActionListener(e -> showCalendar());
        stopwatchBtn.addActionListener(e -> toggleStopwatch());

        setVisible(true);
    }

    private void updateClock() {
        Date now = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat(is24Hour ? "HH:mm:ss" : "hh:mm:ss a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");

        String currentTime = timeFormat.format(now);
        timeLabel.setText(currentTime);
        dateLabel.setText(dateFormat.format(now));
        glow = !glow;
        timeLabel.setForeground(glow ? Color.CYAN : Color.WHITE);

        if (!alarmTime.isEmpty() && currentTime.equals(alarmTime)) {
            playBeep();
            JOptionPane.showMessageDialog(this, "⏰ Alarm! Time: " + alarmTime, "Alarm", JOptionPane.INFORMATION_MESSAGE);
            alarmTime = "";
            alarmStatusLabel.setText("No Alarm Set");
        }

        animateGradient();
        updateStopwatch();
    }

    private void toggleTheme() {
        darkMode = !darkMode;
        if (darkMode) {
            gradientStart = Color.BLACK;
            gradientEnd = new Color(60, 60, 60);
            timeLabel.setForeground(Color.GREEN);
        } else {
            gradientStart = new Color(25, 25, 112);
            gradientEnd = new Color(123, 31, 162);
            timeLabel.setForeground(Color.CYAN);
        }
        repaint();
    }

    private void setAlarm() {
        String input = JOptionPane.showInputDialog(this,
                "Enter alarm time (hh:mm:ss a or HH:mm:ss):", "Set Alarm",
                JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            alarmTime = input.trim();
            alarmStatusLabel.setText("Alarm Set for: " + alarmTime);
        }
    }

    private void showCalendar() {
    // Create a modal dialog to hold the calendar
    JDialog calendarDialog = new JDialog(this, "📅 Calendar", true);
    calendarDialog.setSize(360, 300);
    calendarDialog.setLayout(new BorderLayout());

    // Header with month and year
    Calendar cal = Calendar.getInstance();
    String monthYear = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());
    JLabel monthLabel = new JLabel(monthYear, SwingConstants.CENTER);
    monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    monthLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    // Create calendar grid panel (7 columns)
    JPanel calendarPanel = new JPanel(new GridLayout(0, 7, 4, 4));
    calendarPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    calendarPanel.setBackground(new Color(0,0,0,0)); // transparent-ish, uses dialog bg

    // Day headings
    String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    for (String d : days) {
        JLabel lbl = new JLabel(d, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(30, 30, 90));
        calendarPanel.add(lbl);
    }

    // Setup month start and number of days
    cal.set(Calendar.DAY_OF_MONTH, 1);
    int startDay = cal.get(Calendar.DAY_OF_WEEK); // 1=Sun .. 7=Sat
    int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

    // Fill blanks before day 1
    for (int i = 1; i < startDay; i++) {
        calendarPanel.add(new JLabel("")); // empty placeholder
    }

    // Add day labels
    int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    int thisMonth = Calendar.getInstance().get(Calendar.MONTH);
    int displayMonth = cal.get(Calendar.MONTH);

    for (int day = 1; day <= daysInMonth; day++) {
        JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
        dayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dayLabel.setOpaque(true);
        dayLabel.setBackground(new Color(255,255,255,0)); // default transparent

        // Highlight today's date if month/year match
        if (displayMonth == thisMonth && day == today) {
            dayLabel.setBackground(new Color(135, 206, 250)); // light cyan
            dayLabel.setBorder(BorderFactory.createLineBorder(new Color(30, 144, 255)));
        }
        calendarPanel.add(dayLabel);
    }

    // Wrap the calendar panel in a JScrollPane (valid: calendarPanel is a Component)
    JScrollPane scrollPane = new JScrollPane(calendarPanel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());

    // Put header + scrollpane into dialog
    calendarDialog.add(monthLabel, BorderLayout.NORTH);
    calendarDialog.add(scrollPane, BorderLayout.CENTER);

    // Center and show
    calendarDialog.setLocationRelativeTo(this);
    calendarDialog.setVisible(true);
}

    private void toggleStopwatch() {
        if (!stopwatchRunning) {
            stopwatchStart = System.currentTimeMillis();
            stopwatchRunning = true;
            stopwatchLabel.setText("Stopwatch Started...");
            add(stopwatchLabel, BorderLayout.EAST);
            repaint();
        } else {
            stopwatchRunning = false;
            stopwatchLabel.setText("Stopped");
        }
    }

    private void updateStopwatch() {
        if (stopwatchRunning) {
            long elapsed = System.currentTimeMillis() - stopwatchStart;
            long seconds = (elapsed / 1000) % 60;
            long minutes = (elapsed / (1000 * 60)) % 60;
            long hours = (elapsed / (1000 * 60 * 60)) % 24;
            stopwatchLabel.setText(String.format("⏱ %02d:%02d:%02d", hours, minutes, seconds));
        }
    }

    private void playBeep() {
        try {
            Tone(1000, 300);
        } catch (Exception ignored) {
        }
    }

    public static void Tone(int hz, int msecs) throws LineUnavailableException {
        byte[] buf = new byte[1];
        AudioFormat af = new AudioFormat((float) 44100, 8, 1, true, false);
        try (SourceDataLine sdl = AudioSystem.getSourceDataLine(af)) {
            sdl.open(af);
            sdl.start();
            for (int i = 0; i < msecs * 8; i++) {
                double angle = i / (44100f / hz) * 2.0 * Math.PI;
                buf[0] = (byte) (Math.sin(angle) * 100);
                sdl.write(buf, 0, 1);
            }
            sdl.drain();
            sdl.stop();
        }
    }

    private void animateGradient() {
        gradientStart = new Color((gradientStart.getRed() + 1) % 255,
                (gradientStart.getGreen() + 2) % 255,
                (gradientStart.getBlue() + 3) % 255);
        gradientEnd = new Color((gradientEnd.getRed() + 2) % 255,
                (gradientEnd.getGreen() + 1) % 255,
                (gradientEnd.getBlue() + 4) % 255);
        repaint();
    }

    private String randomQuote() {
        Random r = new Random();
        return QUOTES.get(r.nextInt(QUOTES.size()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SmartDigitalClockPro::new);
    }
}
