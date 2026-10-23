package com.rescuegrid;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Interactive Java Swing Desktop UI for RescueGrid using the Flood SOS Color Palette:
 * - Primary (Deep Navy): #0B3C5D (Text: White)
 * - Secondary (Flood Blue): #328CC1 (Text: White)
 * - Background (Soft Ice White): #F4F8FB (Text: Charcoal #1B2733)
 * - SOS / Critical (Alert Red): #D62828 (Text: White)
 * - Safe / Resolved (Green): #2E9E5B (Text: White)
 * - Warnings / Medium (Amber): #F4A100 / #FCBF49 (Text: Charcoal #1B2733)
 * - High Urgency (Orange): #F77F00
 */
public class RescueGridGUI extends JFrame {
    public static final Color DEEP_NAVY   = Color.decode("#0B3C5D");
    public static final Color FLOOD_BLUE  = Color.decode("#328CC1");
    public static final Color ICE_WHITE   = Color.decode("#F4F8FB");
    public static final Color ALERT_RED   = Color.decode("#D62828");
    public static final Color SAFE_GREEN  = Color.decode("#2E9E5B");
    public static final Color WARN_AMBER  = Color.decode("#F4A100");
    public static final Color MED_AMBER   = Color.decode("#FCBF49");
    public static final Color HIGH_ORANGE = Color.decode("#F77F00");
    public static final Color CHARCOAL    = Color.decode("#1B2733");

    private final RescueService service;
    private int nextId;
    private DefaultTableModel requestModel;
    private JPanel mapCanvas;
    private JTextArea alertLogArea;

    public RescueGridGUI(RescueService service, int startId) {
        this.service = service;
        this.nextId = startId;

        setTitle("RescueGrid — Flood SOS Emergency Response System");
        setSize(1180, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(ICE_WHITE);
        setLayout(new BorderLayout(12, 12));

        // Top Header (Deep Navy #0B3C5D with White text)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DEEP_NAVY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("RescueGrid | Flood SOS Command & Citizen Distress Center");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.add(title, BorderLayout.WEST);

        JButton sosBtn = new JButton("SEND CITIZEN SOS (SMS / APP)");
        sosBtn.setBackground(ALERT_RED);
        sosBtn.setForeground(Color.WHITE);
        sosBtn.setFocusPainted(false);
        sosBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        sosBtn.addActionListener(e -> openCitizenSosDialog());
        header.add(sosBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center Split: Left = Live Verified GPS Map, Right = Requests & Nearby Volunteer Alerts
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(520);
        splitPane.setBackground(ICE_WHITE);

        // Live GPS Map Panel
        mapCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                drawLiveMap(g2, getWidth(), getHeight());
            }
        };
        mapCanvas.setBackground(Color.WHITE);
        splitPane.setLeftComponent(mapCanvas);

        // Right Panel: Table + Controls + Alert Log
        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setBackground(ICE_WHITE);
        rightPanel.setBorder(new EmptyBorder(8, 8, 8, 14));

        String[] cols = {"ID", "Type", "Channel", "Location (GPS)", "People", "Urgency", "Verified", "Status"};
        requestModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(requestModel);
        table.setRowHeight(28);
        table.setForeground(CHARCOAL);
        table.getTableHeader().setBackground(DEEP_NAVY);
        table.getTableHeader().setForeground(Color.WHITE);

        // Custom Urgency Badge Renderer honoring exact text contrast rules
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String text = val == null ? "" : val.toString();
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setOpaque(true);
                if (text.contains("CRITICAL")) {
                    lbl.setBackground(ALERT_RED);
                    lbl.setForeground(Color.WHITE);
                } else if (text.contains("HIGH")) {
                    lbl.setBackground(HIGH_ORANGE);
                    lbl.setForeground(Color.WHITE);
                } else if (text.contains("MEDIUM")) {
                    lbl.setBackground(MED_AMBER);
                    lbl.setForeground(CHARCOAL); // Charcoal on Amber for readability
                } else if (text.contains("RESOLVED")) {
                    lbl.setBackground(SAFE_GREEN);
                    lbl.setForeground(Color.WHITE);
                } else {
                    lbl.setBackground(FLOOD_BLUE);
                    lbl.setForeground(Color.WHITE);
                }
                return lbl;
            }
        });

        rightPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Action Bar
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        actions.setBackground(ICE_WHITE);

        JButton verifyBtn = createButton("Verify Selected", FLOOD_BLUE, Color.WHITE);
        verifyBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) requestModel.getValueAt(row, 0);
                service.verifyRequest(id);
                refreshUI();
            }
        });

        JButton alertNearBtn = createButton("Alert Nearby Volunteers (<5km)", WARN_AMBER, CHARCOAL);
        alertNearBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) requestModel.getValueAt(row, 0);
                ArrayList<Volunteer> alerted = service.alertNearbyVolunteers(id, 5.0);
                alertLogArea.append("Broadcasted GPS proximity alert for Request #" + id + " -> " + alerted.size() + " volunteer(s) notified.\n");
                refreshUI();
            }
        });

        JButton assignBtn = createButton("Assign Nearest Volunteer", SAFE_GREEN, Color.WHITE);
        assignBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) requestModel.getValueAt(row, 0);
                service.assignVolunteer(id);
                refreshUI();
            }
        });

        actions.add(verifyBtn);
        actions.add(alertNearBtn);
        actions.add(assignBtn);

        alertLogArea = new JTextArea(5, 40);
        alertLogArea.setEditable(false);
        alertLogArea.setForeground(CHARCOAL);
        alertLogArea.setText("System Ready. Verified incidents appear on the Live GPS Map.\n");

        JPanel southRight = new JPanel(new BorderLayout(4, 4));
        southRight.setBackground(ICE_WHITE);
        southRight.add(actions, BorderLayout.NORTH);
        southRight.add(new JScrollPane(alertLogArea), BorderLayout.CENTER);
        rightPanel.add(southRight, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        refreshUI();
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        return b;
    }

    private void openCitizenSosDialog() {
        String sms = JOptionPane.showInputDialog(this,
                "Enter Citizen SMS format (SOS|FLOOD|Location|Lat,Lng|People|WaterFt|Phone):",
                "SOS|FLOOD|Kothrud|18.5074,73.8077|5|4|9876500001");
        if (sms != null && !sms.trim().isEmpty()) {
            try {
                Request r = SmsDistressGateway.parseDistressSms(nextId++, sms.trim());
                service.addRequest(r);
                alertLogArea.append("Received Citizen SMS SOS #" + r.getId() + " from " + r.getReporterPhone() + " at " + r.getLocation() + "\n");
                refreshUI();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void drawLiveMap(Graphics2D g2, int w, int h) {
        g2.setColor( Color.decode("#E6F2F8") );
        g2.fillRect(0, 0, w, h);
        g2.setColor(DEEP_NAVY);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString("Live GPS Map — Verified Incidents & Shelters (Pune/PCMC)", 14, 24);

        double minLat = 18.45, maxLat = 18.66;
        double minLng = 73.74, maxLng = 73.94;

        // Draw Shelters in Safe Green (#2E9E5B)
        for (Shelter s : service.getShelters()) {
            int x = (int) ((s.getLongitude() - minLng) / (maxLng - minLng) * (w - 60)) + 30;
            int y = h - ((int) ((s.getLatitude() - minLat) / (maxLat - minLat) * (h - 80)) + 40);
            g2.setColor(SAFE_GREEN);
            g2.fillRoundRect(x - 8, y - 8, 16, 16, 4, 4);
            g2.setColor(CHARCOAL);
            g2.drawString("[S] " + s.getName(), x + 10, y + 4);
        }

        // Draw Verified Requests colored by Urgency
        for (Request r : service.getVerifiedIncidentsForMap()) {
            int x = (int) ((r.getLongitude() - minLng) / (maxLng - minLng) * (w - 60)) + 30;
            int y = h - ((int) ((r.getLatitude() - minLat) / (maxLat - minLat) * (h - 80)) + 40);
            Color pinColor = Color.decode(r.getUrgencyHexColor());
            g2.setColor(pinColor);
            g2.fillOval(x - 10, y - 10, 20, 20);
            g2.setColor(CHARCOAL);
            g2.drawString("#" + r.getId() + " " + r.getLocation() + " (" + r.getUrgency() + ")", x + 12, y + 4);
        }
    }

    private void refreshUI() {
        requestModel.setRowCount(0);
        for (Request r : service.getSortedByUrgency()) {
            requestModel.addRow(new Object[]{
                    r.getId(), r.getType(), r.getSourceChannel(),
                    String.format("%s (%.4f, %.4f)", r.getLocation(), r.getLatitude(), r.getLongitude()),
                    r.getPeople(), r.getUrgency() + " [" + r.getUrgencyLevel() + "]",
                    r.isVerified() ? "YES" : "NO", r.getStatus()
            });
        }
        mapCanvas.repaint();
    }
}
