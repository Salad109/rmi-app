package implementations;

import interfaces.IHouse;
import interfaces.IOffice;
import interfaces.ITanker;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Tanker extends UnicastRemoteObject implements ITanker {
    protected Tanker() throws RemoteException {
    }

    @Override
    public void setJob(IHouse house) throws RemoteException {
        if(house == null)
            System.out.println("got null");
        else
            System.out.println(house.toString());
    }

    public static void main(String[] args) {
        try {
            Tanker tanker = new Tanker();
            Registry r = LocateRegistry.getRegistry(2000);
            IOffice io = (IOffice) r.lookup("Office");
            io.register(tanker, "Tanker1");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
}
