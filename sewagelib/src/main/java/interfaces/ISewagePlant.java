package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISewagePlant extends Remote {
    void setPumpIn(int number, int volume) throws RemoteException;
    int getStatus(int number) throws RemoteException;
    void setPayoff(int number) throws RemoteException;
}
