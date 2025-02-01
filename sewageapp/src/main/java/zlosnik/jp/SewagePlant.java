package zlosnik.jp;

import interfaces.ISewagePlant;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class SewagePlant extends UnicastRemoteObject implements ISewagePlant {
    private Map<Integer, Integer> pumpLogs = new HashMap<>();
    private JTextArea textArea;
    private JTextArea pumpLogsTextArea;

    protected SewagePlant() throws RemoteException {
    }

    @Override
    public void setPumpIn(int number, int volume) throws RemoteException {
        pumpLogs.merge(number, volume, Integer::sum);
        logMessage("Tanker " + number + " dumped " + volume + " sewage");
        updatePumpLogsDisplay();
    }

    @Override
    public int getStatus(int number) throws RemoteException {
        return pumpLogs.getOrDefault(number, 0);
    }

    @Override
    public void setPayoff(int number) throws RemoteException {
        pumpLogs.put(number, 0);
        logMessage("Tanker " + number + " has been paid off");
        updatePumpLogsDisplay();
    }


    private void createAndShowGUI() {
        JFrame frame = new JFrame("Sewage Plant");

        textArea = new JTextArea(10, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        pumpLogsTextArea = new JTextArea(10, 30);
        pumpLogsTextArea.setEditable(false);
        JScrollPane pumpLogsScrollPane = new JScrollPane(pumpLogsTextArea);

        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.add(new JLabel("Log"), BorderLayout.NORTH);
        textAreaPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel pumpLogsPanel = new JPanel(new BorderLayout());
        pumpLogsPanel.add(new JLabel("Pump Logs"), BorderLayout.NORTH);
        pumpLogsPanel.add(pumpLogsScrollPane, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(textAreaPanel);
        mainPanel.add(pumpLogsPanel);

        frame.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);

        frame.setSize(800, 300);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    private void updatePumpLogsDisplay() {
        StringBuilder logs = new StringBuilder();
        pumpLogs.forEach((number, volume) -> logs.append("Tanker ").append(number).append(": ").append(volume).append("\n"));
        pumpLogsTextArea.setText(logs.toString());
    }

    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        try {
            SewagePlant sewagePlant = new SewagePlant();
            sewagePlant.createAndShowGUI();
            Registry registry = LocateRegistry.getRegistry("localhost", 2000);
            registry.rebind("Plant", sewagePlant);
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to the RMI registry: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}