package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IHouse extends Remote {
    int getPumpOut(int max) throws RemoteException;
}
