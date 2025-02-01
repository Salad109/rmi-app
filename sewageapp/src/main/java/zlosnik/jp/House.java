package zlosnik.jp;

import interfaces.IHouse;
import interfaces.IOffice;

import javax.swing.*;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicInteger;

public class House extends UnicastRemoteObject implements IHouse {
    private final AtomicInteger sewageCounter = new AtomicInteger(0);
    private JLabel sewageCounterLabel;
    private JTextArea textArea;

    protected House() throws RemoteException {
    }

    @Override
    public int getPumpOut(int max) throws RemoteException {
        return 0;
    }

    public static void main(String[] args) {
        try {
            House house = new House();
            house.createAndShowGUI();
            house.startSewageIncrementer();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("House");

        sewageCounterLabel = new JLabel(getSewageCounterText());
        textArea = new JTextArea(10, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton sendOrderButton = new JButton("Send Order to Office");
        sendOrderButton.addActionListener(e -> sendOrderToOffice());

        JPanel inputPanel = new JPanel(new GridLayout(0, 1));
        inputPanel.add(sendOrderButton);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(sewageCounterLabel, BorderLayout.SOUTH);
        frame.add(inputPanel, BorderLayout.NORTH);

        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    private String getSewageCounterText() {
        return "Sewage counter: " + getSewage();
    }

    protected int getSewage() {
        return sewageCounter.get();
    }

    private void sendOrderToOffice() {
        try {
            Registry r = LocateRegistry.getRegistry(2000);
            IOffice io = (IOffice) r.lookup("Office");
            int orderStatus = io.order(this, "House1");
            logMessage("Order sent");

            if (orderStatus == 0) {
                logMessage("Order denied");
            } else {
                logMessage("Order accepted");
            }
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void startSewageIncrementer() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    sewageCounter.incrementAndGet();
                    SwingUtilities.invokeLater(() -> sewageCounterLabel.setText(getSewageCounterText()));
                } catch (InterruptedException e) {
                    logMessage("Sewage incrementer interrupted: " + e.getMessage());
                }
            }
        }).start();
    }

    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
    }
}
