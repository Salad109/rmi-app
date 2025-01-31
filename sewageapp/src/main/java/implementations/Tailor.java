package implementations;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Tailor {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(2000);
            while(true){}
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }
}
