package implementations;

import interfaces.ISewagePlant;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SewagePlant extends UnicastRemoteObject implements ISewagePlant {
    protected SewagePlant() throws RemoteException {
    }


    @Override
    public void setPumpIn(int number, int volume) throws RemoteException {
    }

    @Override
    public int getStatus(int number) throws RemoteException {
        return 0;
    }

    @Override
    public void setPayoff(int number) throws RemoteException {
    }
}
