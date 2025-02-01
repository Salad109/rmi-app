package implementations;

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
    private static int idgen = 1;
    private Map<Integer, ITanker> tankers = new HashMap<>();
    private JTextArea textArea;
    private JTextArea tankersListTextArea;

    @Override
    public int register(ITanker t, String name) throws RemoteException {
        int id = idgen++;
        tankers.put(id, t);
        logMessage("Registered tanker " + id + " named " + name);
        t.setJob(null);
        updateTankersList(); // Update the tankers list whenever a new tanker is registered
        return id;
    }

    @Override
    public int order(IHouse house, String name) throws RemoteException {
        return 0;
    }

    @Override
    public void setReadyToServe(int number) throws RemoteException {
    }

    private void updateTankersList() {
        SwingUtilities.invokeLater(() -> {
            tankersListTextArea.setText(""); // Clear existing text
            tankers.forEach((id, tanker) -> tankersListTextArea.append("Tanker ID: " + id + "\n"));
        });
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Office");

        textArea = new JTextArea(10, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        tankersListTextArea = new JTextArea(10, 30);
        tankersListTextArea.setEditable(false);
        JScrollPane tankersScrollPane = new JScrollPane(tankersListTextArea);

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(scrollPane);
        panel.add(tankersScrollPane);

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);

        frame.setSize(800, 300); // Adjusted size to accommodate both text areas
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
