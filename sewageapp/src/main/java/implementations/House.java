package implementations;

import interfaces.IHouse;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class House extends UnicastRemoteObject implements IHouse {
    protected House() throws RemoteException {
    }

    @Override
    public int getPumpOut(int max) throws RemoteException {
        return 0;
    }
}
