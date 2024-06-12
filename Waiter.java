import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Waiter implements Runnable {

    public static final int PLACE_ORDER_MIN_TIME = 100;
    public static final int PLACE_ORDER_MAX_TIME = 500;

    public static final int PREP_ORDER_MIN_TIME = 300;
    public static final int PREP_ORDER_MAX_TIME = 1000;

    public static final int GET_ORDER_MIN_TIME = 100;
    public static final int GET_ORDER_MAX_TIME = 500;

    private int waiterId;
    public boolean waiterCalled;

    private List<Customer> customers;
    
 //   private Customer customer;

    private static Semaphore kitchen = new Semaphore(1,true);
    private static Semaphore waiterSem = new Semaphore(1, true);

    boolean waiterRelieved;

    public int getWaiterId() {
        return waiterId;
    }

    public Waiter(int waiterID)
    {
        this.waiterId = waiterID;
        customers = new ArrayList<>();
    }

    public void placeOrder(int customerId) throws InterruptedException
    {
        placeOrderWithKitchen(customerId);
        waitForOrder(customerId);
        pickupOrder(customerId);
    }

    private void placeOrderWithKitchen(int customerId) throws InterruptedException
    {
        int orderTime = new Random().nextInt(PLACE_ORDER_MAX_TIME - PLACE_ORDER_MIN_TIME + 1) + PLACE_ORDER_MIN_TIME;
        kitchen.acquire();
        System.out.println(" Waiter " + waiterId + " placing order with kitchen for customer " + customerId);
        Thread.sleep(orderTime);
        kitchen.release();
    }
    private void waitForOrder(int customerId) throws InterruptedException
    {
        int orderTime = new Random().nextInt(PREP_ORDER_MAX_TIME - PREP_ORDER_MIN_TIME + 1) + PREP_ORDER_MIN_TIME;
        kitchen.acquire();
        System.out.println(" Waiter " + waiterId + " waiting for order to be prepared for customer " + customerId);
        Thread.sleep(orderTime);
        kitchen.release();
    }

    private void pickupOrder(int customerId) throws InterruptedException
    {
        int orderTime = new Random().nextInt(GET_ORDER_MAX_TIME - GET_ORDER_MIN_TIME + 1) + GET_ORDER_MIN_TIME;
        kitchen.acquire();
        System.out.println(" Waiter " + waiterId + " picking up order for customer " + customerId);
        Thread.sleep(orderTime);
        kitchen.release();
    }

    public boolean isWaiterCalled()
    {
        return waiterCalled;
    }

    public void callWaiter(Customer customer) throws InterruptedException
    {
        waiterSem.acquire();
        customers.add(customer);
        waiterSem.release();
    }

    public void dismissWaiter()
    {
        waiterCalled = false;
    }

    public void relieveWaiter()
    {
        this.waiterRelieved = true;
        System.out.println("Waiter " + waiterId + " cleans table and leaves");
    }

    @Override
    public void run() {
        
        while(!waiterRelieved)
        {
            try
            {
               waiterSem.acquire();
                if(customers != null && customers.size() > 0){

                    Customer customer = customers.get(0);
                    System.out.println("Waiter " + waiterId + " takes Customer's " + customer.getCustomerId() + " order.");
                    placeOrder(customer.getCustomerId());
                    System.out.println("Waiter " + waiterId + " served Customer's " + customer.getCustomerId() + " order.");
                   
                    customer.setFoodServed(true);

                    customers.remove(0);

                    Thread.sleep(100);
                }
              waiterSem.release();
            }
            catch(Exception ex)
            {
                System.out.println(ex.toString());
                ex.printStackTrace();
            }
        }
    }
    
}
