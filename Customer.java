import java.util.Random;
import java.util.concurrent.Semaphore;

public class Customer implements Runnable {

    public static final int EATING_TIME_MIN = 200;
    public static final int EATING_TIME_MAX = 1000;
    public static final int PAYMENT_TIME = 100;

    private int customerId;
    private Table firstChoice;
    private Table secondChoice;

    private boolean foodServed;

    private Restaurant restaurant;

    private static Semaphore waiterSem = new Semaphore(1, true);
    private static Semaphore tableSem = new Semaphore(1, true);

    public int getCustomerId() {
        return customerId;
    }

    public Table getFirstChoice() {
        return firstChoice;
    }

    public void setFirstChoice(Table firstChoice) {
        this.firstChoice = firstChoice;
    }

    public Table getSecondChoice() {
        return secondChoice;
    }

    public void setSecondChoice(Table secondChoice) {
        this.secondChoice = secondChoice;
    }

    public Customer(int customerId, Restaurant restaurant)
    {
        this.customerId = customerId;
        this.restaurant = restaurant;
    }

    public boolean isFoodServed()
    {
        return foodServed;
    }

    public void setFoodServed(boolean foodServed)
    {
        System.out.println("Food served for customer " + getCustomerId());
        this.foodServed = foodServed;
    }

    public void eatFood() throws InterruptedException
    {
        int eatingTime = new Random().nextInt(EATING_TIME_MAX-EATING_TIME_MIN+1) + EATING_TIME_MIN;
        Thread.sleep(eatingTime);
    }

    @Override
    public void run() {

        try 
        {
   
            // pick door to enter
            int door = new Random().nextInt(Restaurant.NUM_DOORS);
            if(door == 0)
            {
                System.out.println("Customer " + customerId + " entering restaurant from door 1");
                restaurant.enterResaturantD1();
            }
            else
            {
                System.out.println("Customer " + customerId + " entering restaurant from door 2");
                restaurant.enterResaturantD2();    
            }
            
            boolean waiting = true;
            Table table = null;

            // pick table
                // randomly select table
                // if table is available, seat at table
                // wait in queue if table is not available
                // if table has more than 7, select second choice if this is available

        
            int tableNum = new Random().nextInt(Restaurant.MAX_TABLES);
            if(tableNum == 0)
                System.out.println("Customer " + getCustomerId() + " wants to eat seafood.");
            else if(tableNum == 1)
                System.out.println("Customer " + getCustomerId() + " wants to eat steak.");
            else if(tableNum == 2)
                System.out.println("Customer " + getCustomerId() + " wants to eat pasta.");

                   
         
            tableSem.acquire();
      
            firstChoice = restaurant.getTable(tableNum, this);
            int chairNo = 0;
            if(firstChoice != null)
            {
                if(firstChoice.isVacant())
                {
                    chairNo = firstChoice.assignTable(this);
                    System.out.println("Customer " + getCustomerId() + " is assigned to table " + tableNum + " chair no " + chairNo);
                    //  firstChoice.removeFromWaitList(this);
                    waiting = false;
                    table = firstChoice;
                }
                else
                {
                    System.out.println("Customer " + getCustomerId() + " is standing in line for table " + tableNum);
                    firstChoice.addtoWaitList(this);
                    table = firstChoice;
                }

                if(firstChoice.moreThanSeven())
                {
                    int firstChoiceSize = firstChoice.getWaitListSize();
                    int secondTableNum = tableNum;
                    while(secondTableNum == tableNum)
                        secondTableNum = new Random().nextInt(Restaurant.MAX_TABLES);
                    System.out.println("Customer " + getCustomerId() + " backup choice is table " + secondTableNum);
                    secondChoice = restaurant.getTable(secondTableNum, this);
                    int secondChoiceSize = secondChoice.getWaitListSize();

                    if(secondChoice.isVacant() || secondChoiceSize < firstChoiceSize)
                    {
                        if(secondChoice.isVacant())
                        {
                            chairNo = secondChoice.assignTable(this);
                            System.out.println("Customer " + getCustomerId() + " is seated in backup table  " + secondTableNum + " chair no " + chairNo);
                            firstChoice.removeFromWaitList(this);
                            waiting = false;
                             //   table = secondChoice;
                        }
                        else
                        {
                            System.out.println("Customer " + getCustomerId() + " is standing in line for backup table  " + secondTableNum);
                            secondChoice.addtoWaitList(this);
                           //     table = secondChoice;
                        }
                            //waiting = false;
                        table = secondChoice;
                    }               
                }

            }
            
            tableSem.release();
               
            
            
            while(waiting)
            {
                tableSem.acquire();
                if(table.isVacant())
                {
                    chairNo = table.assignTable(this);
                    table.removeFromWaitList(this);
                    System.out.println("Customer " + getCustomerId() + " is seated in  table  " + table.getTableId() + " chair no " + chairNo);
                    waiting = false;
                }
                tableSem.release();
            }

        
            // call waiter
            Waiter waiter = table.getWaiter();
            System.out.println("Waiter " + waiter.getWaiterId() + " assigned to customer " + getCustomerId());

            // get waiter
            waiterSem.acquire();
            System.out.println("Customer " + getCustomerId() + " contacting waiter " + waiter.getWaiterId());
            waiter.callWaiter(this);
            waiterSem.release();
 
            while(!foodServed)
            {
                Thread.sleep(100);
                continue;
            }
            
            // eat food
            System.out.println("Customer " + getCustomerId() + " is dining." );
            eatFood();

            tableSem.acquire();
            // vacate table
            System.out.println("Customer " + getCustomerId() + " is vacating table " + table.getTableId() + " chair no " + chairNo );
            table.vacateTable(chairNo);
            tableSem.release();

            // make payment
            System.out.println("Customer " + getCustomerId() + " paid bill." );

            // leave restaurant
               // pick door to leave
            door = new Random().nextInt(Restaurant.NUM_DOORS);
            if(door == 0)
            {
                System.out.println("Customer " + customerId + " leaving restaurant from door 1");
                restaurant.exitRestaurantD1();
            }
            else
            {
                System.out.println("Customer " + customerId + " leaving restaurant from door 2");
                restaurant.exitRestaurantD2();
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        
    }
    
}
