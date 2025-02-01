package implementations;

import interfaces.IHouse;
import interfaces.IOffice;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class House extends UnicastRemoteObject implements IHouse {
    protected House() throws RemoteException {
    }

    @Override
    public int getPumpOut(int max) throws RemoteException {
        return 0;
    }

    public static void main(String[] args) {
        try {
            House house = new House();
            Registry r = LocateRegistry.getRegistry(2000);
            IOffice io = (IOffice) r.lookup("Office");
            int orderStatus = io.order(house, "House1");

            if (orderStatus == 0) {
                System.out.println("Order denied");
            } else {
                System.out.println("Order accepted");
            }
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
}
