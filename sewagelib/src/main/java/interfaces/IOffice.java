package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IOffice extends Remote {
    int register(ITanker r, String name) throws RemoteException;
    int order(IHouse house, String name) throws RemoteException;
    void setReadyToServe(int number) throws RemoteException;
}
