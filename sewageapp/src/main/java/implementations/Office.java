package implementations;

import interfaces.IHouse;
import interfaces.IOffice;
import interfaces.ITanker;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Office implements IOffice {
    private static int idgen = 1;
    private Map<Integer, ITanker> tankers = new HashMap<>();
    @Override
    public int register(ITanker t, String name) throws RemoteException {
        /*
        // informację na temat tego na jakim hoście i porcie rzeczywiście działa namiastka
        // chyba najłatwiej uzyskać parsując wynik metody toString()
        // metoda ta zwraca ciąg znaków podobny do poniższego:
        // Proxy[IControlCenter,RemoteObjectInvocationHandler[UnicastRef [liveRef: [endpoint:[192.168.1.153:3000](remote),objID:[-50fb9f25:1945c684c6d:-7fff, 7487353482432237380]]]]]
        // wystarczy więc wyciągnąć z niego podciąg korzystając z regexp
        Pattern pattern = Pattern.compile(".*endpoint:\\[(.*)\\]\\(remote.*");
        Matcher matcher = pattern.matcher(r.toString());
        if (matcher.find())
        {
            System.out.println(matcher.group(1)); //
        }
        czyli zamiast name można byłoby użyć wyciągnięty ciąg znaków host:port
        ale to byłoby mało czytelne
        */

        int id = idgen++;
        tankers.put(id, t);
        System.out.println("Registered tanker " + id + " for " + name);
        t.setJob(null);
        return id;
    }

    @Override
    public int order(IHouse house, String name) throws RemoteException {
        return 0;
    }

    @Override
    public void setReadyToServe(int number) throws RemoteException {
    }

    public static void main(String [] args) throws RemoteException {
        try {
            Office office = new Office();
            IOffice io = (IOffice) UnicastRemoteObject.exportObject(office,0);
            Registry registry = LocateRegistry.getRegistry("localhost",2000);
            registry.rebind("Office", io);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
