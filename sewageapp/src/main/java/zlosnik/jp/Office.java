package zlosnik.jp;

import interfaces.IHouse;
import interfaces.IOffice;
import interfaces.ITanker;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Office implements IOffice {
    private Map<Integer, ITanker> tankers = new HashMap<>();
    private Map<Integer, ITanker> busyTankers = new HashMap<>();
    private JTextArea textArea;
    private JTextArea tankersListTextArea;
    private JTextArea busyTankersListTextArea;

    @Override
    public int register(ITanker t, String name) throws RemoteException {
        int id = t.hashCode();
        tankers.put(id, t);
        logMessage("Registered tanker " + id + " named " + name);
        updateTankersList(); // Update the tankers list whenever a new tanker is registered
        return id;
    }

    @Override
    public int order(IHouse house, String name) throws RemoteException {
        if (tankers.isEmpty()) {
            logMessage("No tankers available to serve house " + name);
            return 0;
        } else {
            ITanker tanker = tankers.values().iterator().next();
            tankers.remove(tanker.hashCode());
            busyTankers.put(tanker.hashCode(), tanker);
            tanker.setJob(house);
            logMessage("Ordered tanker " + tanker.hashCode() + " to serve house " + name);
            updateTankersList(); // Update the tankers list whenever a tanker is ordered
            updateBusyTankersList(); // Update the busy tankers list whenever a tanker is ordered
            return tanker.hashCode();
        }
    }

    @Override
    public void setReadyToServe(int number) throws RemoteException {
        ITanker tanker = busyTankers.remove(number);
        if (tanker != null) {
            tankers.put(number, tanker);
            logMessage("Tanker " + number + " is now ready to serve again.");
            updateTankersList(); // Update the tankers list whenever a tanker is ready to serve
            updateBusyTankersList(); // Update the busy tankers list whenever a tanker is ready to serve
        } else {
            logMessage("Tanker " + number + " not found in busy tankers.");
        }
    }

    private void updateTankersList() {
        SwingUtilities.invokeLater(() -> {
            tankersListTextArea.setText(""); // Clear existing text
            tankers.forEach((id, tanker) -> tankersListTextArea.append("Tanker ID: " + id + "\n"));
        });
    }

    private void updateBusyTankersList() {
        SwingUtilities.invokeLater(() -> {
            busyTankersListTextArea.setText(""); // Clear existing text
            busyTankers.forEach((id, tanker) -> busyTankersListTextArea.append("Busy Tanker ID: " + id + "\n"));
        });
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Office");

        textArea = new JTextArea(10, 20);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        tankersListTextArea = new JTextArea(10, 20);
        tankersListTextArea.setEditable(false);
        JScrollPane tankersScrollPane = new JScrollPane(tankersListTextArea);

        busyTankersListTextArea = new JTextArea(10, 20);
        busyTankersListTextArea.setEditable(false);
        JScrollPane busyTankersScrollPane = new JScrollPane(busyTankersListTextArea);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.add(new JLabel("Log"), BorderLayout.NORTH);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel tankersPanel = new JPanel(new BorderLayout());
        tankersPanel.add(new JLabel("Available Tankers"), BorderLayout.NORTH);
        tankersPanel.add(tankersScrollPane, BorderLayout.CENTER);

        JPanel busyTankersPanel = new JPanel(new BorderLayout());
        busyTankersPanel.add(new JLabel("Busy Tankers"), BorderLayout.NORTH);
        busyTankersPanel.add(busyTankersScrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridLayout(1, 3));
        panel.add(logPanel);
        panel.add(tankersPanel);
        panel.add(busyTankersPanel);

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);

        frame.setSize(1200, 300); // Adjusted size to accommodate all text areas
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
    }

    public static void main(String[] args) throws RemoteException {
        try {
            Office office = new Office();
            office.createAndShowGUI();
            IOffice io = (IOffice) UnicastRemoteObject.exportObject(office, 0);
            Registry registry = LocateRegistry.getRegistry("localhost", 2000);
            registry.rebind("Office", io);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}