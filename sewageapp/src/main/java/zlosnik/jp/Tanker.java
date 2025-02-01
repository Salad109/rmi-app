package zlosnik.jp;

import interfaces.IHouse;
import interfaces.IOffice;
import interfaces.ITanker;

import javax.swing.*;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Tanker extends UnicastRemoteObject implements ITanker {
    private static final int TOTAL_CAPACITY = 20;
    private int currentSewageAmount = 0;
    private JTextArea textArea;
    private JLabel sewageLabel;
    private IHouse house;
    private IOffice io;

    protected Tanker() throws RemoteException {
    }

    @Override
    public void setJob(IHouse house) throws RemoteException {
        logMessage("Received job from house " + house.hashCode());
        this.house = house;
    }

    public static void main(String[] args) {
        try {
            Tanker tanker = new Tanker();
            tanker.createAndShowGUI();
            Registry r = LocateRegistry.getRegistry(2000);
            tanker.io = (IOffice) r.lookup("Office");
            tanker.io.register(tanker, "Tanker1");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Tanker " + this.hashCode());

        sewageLabel = new JLabel(getSewageLabelText());

        textArea = new JTextArea(10, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton sewageGetButton = new JButton("Get Sewage");
        sewageGetButton.addActionListener(e -> fetchSewage());
        JButton sewageDumpButton = new JButton("Dump Sewage");
        sewageDumpButton.addActionListener(e -> dumpSewage());

        JPanel inputPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(sewageGetButton);
        inputPanel.add(sewageDumpButton);

        frame.setLayout(new BorderLayout());
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(sewageLabel, BorderLayout.SOUTH);

        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    private void fetchSewage() {
        if (house == null) {
            logMessage("No job assigned");
            return;
        }
        try {
            int remainingCapacity = TOTAL_CAPACITY - currentSewageAmount;
            int sewageAmount = house.getPumpOut(remainingCapacity);
            house = null;
            currentSewageAmount += sewageAmount;
            sewageLabel.setText(getSewageLabelText());
            io.setReadyToServe(this.hashCode());
            logMessage("Fetched " + sewageAmount + " sewage");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void dumpSewage() {
        currentSewageAmount = 0;
        sewageLabel.setText(getSewageLabelText());
        logMessage("Dumped sewage");
    }

    private String getSewageLabelText() {
        return "Current sewage amount: " + currentSewageAmount + "/" + TOTAL_CAPACITY;
    }

    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
    }
}
