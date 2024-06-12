import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Restaurant
{
    public static final int MAX_WAITERS = 3;
    public static final int MAX_CUSTOMERS = 40;
    public static final int MAX_TABLES = 3;
    public static final int NUM_DOORS = 2;

    private static int numWaiters;
    private static int numCustomers;

    List<Table> tables;
    List<Waiter> waiters;  
    List<Customer> customers;
    
    Semaphore d1;
    Semaphore d2;

    public static int getNumWaiters() {
        return numWaiters;
    }

    public static void setNumWaiters(int numWaiters) {
        Restaurant.numWaiters = numWaiters;
    }

    public void addWaiter(Waiter waiter) {

        if(numWaiters >= MAX_WAITERS)
            return;

        Table table = tables.get(waiter.getWaiterId());
        table.assignWaiter(waiter);

        waiters.add(waiter);
        numWaiters++;
    }

    public void removeWaiter(Waiter waiter) {

        waiters.remove(waiter);
        numWaiters--;
    }
    
    public static int getNumCustomers() {
        return numCustomers;
    }

    public static void setNumCustomers(int numCustomers) {
        Restaurant.numCustomers = numCustomers;
    }

    public void enterResaturantD1() throws InterruptedException {
        d1.acquire();
        numCustomers++;
        d1.release();
    }
 
    public void exitRestaurantD1() throws InterruptedException {
        d1.acquire();
        numCustomers--;
        d1.release();
    }

    public void enterResaturantD2() throws InterruptedException {
        d2.acquire();
        numCustomers++;
        d2.release();
    }
 
    public void exitRestaurantD2() throws InterruptedException {
        d2.acquire();
        numCustomers--;
        d2.release();
    }

    public void close()
    {
        for(int i=0;i<MAX_WAITERS;i++)
        {
            Waiter waiter = waiters.get(i);
            waiter.relieveWaiter();
        }
    }
    public Restaurant()
    {
        d1 = new Semaphore(1, true);
        d2 = new Semaphore(1,true);

        waiters = new ArrayList<Waiter>();
        customers = new ArrayList<Customer>();

        tables = new ArrayList<Table>();
        for(int i=0;i<MAX_TABLES;i++) {
            Table table = new Table(i); 
            tables.add(table);
        } 


    }

    public Table getTable(int tableNum, Customer customer)
    {
        Table table = tables.get(tableNum);
        return table;
    }

    public static void main(String[] args) { 
        
        Restaurant restaurant = new Restaurant();

        // create and assign waiters to tables
        for(int i=0;i<MAX_WAITERS;i++)
        {
            Waiter waiter = new Waiter(i);
            restaurant.addWaiter(waiter);
            Thread waiterThread = new Thread(waiter);
            waiterThread.start();
        }

        List<Thread> customerThreads = new LinkedList<Thread>();

        // serve customers
        for(int i=0;i<MAX_CUSTOMERS;i++)
        {
            Customer customer = new Customer(i, restaurant);
            Thread customerThread = new Thread(customer);
            customerThreads.add(customerThread);
            customerThread.start();
        }

        try 
        {
            // wait for all the customers to exit
            for(int i=0;i<MAX_CUSTOMERS;i++)
            {
                Thread customerThread = customerThreads.get(i);
                customerThread.join();
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        // close the restaurant
        restaurant.close();   
     }
}
