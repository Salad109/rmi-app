package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITanker extends Remote {
    void setJob(IHouse house) throws RemoteException;
}
