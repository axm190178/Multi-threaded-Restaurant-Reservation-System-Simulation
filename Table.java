import java.util.ArrayList;
import java.util.List;

public class Table
{
    private int tableId;

    boolean chair1Occupied;
    boolean chair2Occupied;
    boolean chair3Occupied;
    boolean chair4Occupied;

    Customer customer1;
    Customer customer2;
    Customer customer3;
    Customer customer4;

    Waiter waiter;

    public List<Customer> waitList;

    public int getTableId()
    {
        return tableId;
    }

    public boolean isFull()
    {
        if(chair1Occupied && chair2Occupied && chair3Occupied && chair4Occupied)
            return true;
        return false;
    }

    public boolean isVacant()
    {
        if(!isFull())
            return true;
        else
            return false;
    }

    public boolean isWaiterAssigned()
    {
        if(waiter == null)
            return false;
        return true;
    }

    public void assignWaiter(Waiter waiter)
    {
        this.waiter = waiter;
        System.out.println("Waiter " + waiter.getWaiterId() + " assigned to table: " + tableId);
    }

    public Waiter getWaiter()
    {
        return waiter;
    }

    public void removeWaiter()
    {
        waiter = null;
    }

    public int assignTable(Customer customer)
    {
        if(isFull())
            return 0;
        int chairNo = getVacantChair();
        occupyChair(chairNo, customer);
        return chairNo;
    }

    public void vacateTable(int chairNo)
    {
        vacateChair(chairNo);
    }

    private int getVacantChair()
    {
        if(!chair1Occupied)
            return 1;
        else if(!chair2Occupied)
            return 2;
        else if(!chair3Occupied)
            return 3;
        else if(!chair4Occupied)
            return 4;

        return 0;
    }


    private void occupyChair(int chairNum, Customer customer)
    {
        switch (chairNum)
        {
            case 1:
                chair1Occupied = true;
                customer1 = customer;
                break;
            case 2:
                chair2Occupied = true;
                customer2 = customer;
                break;
            case 3:
                chair3Occupied = true;
                customer3 = customer;
                break;
            case 4:
            chair4Occupied = true;
                customer4 = customer;
                break;
        }
    }

    private void vacateChair(int chairNum)
    {
        switch (chairNum)
        {
            case 1:
                chair1Occupied = false;
                customer1 = null;
                break;
            case 2:
                chair2Occupied = false;
                customer2 = null;
                break;
            case 3:
                chair3Occupied = false;
                customer3 = null;
                break;
            case 4:
                chair4Occupied = false;
                customer4 = null;
                break;
        }
    }

    public void addtoWaitList(Customer customer)
    {
        waitList.add(customer);
    }

    public void removeFromWaitList(Customer customer)
    {
        waitList.remove(customer);
    }

    public int getWaitListSize()
    {
        if(waitList == null || waitList.isEmpty())
            return 0;
        return waitList.size();
    }
    
    public boolean moreThanSeven()
    {
        if(waitList.size() > 7)
            return true;
        else 
            return false;
    }

    public Table(int tableId)
    {
        this.tableId = tableId;

        waitList = new ArrayList<Customer>();
        chair1Occupied = false;
        chair2Occupied = false;
        chair3Occupied = false;
        chair4Occupied = false;
    }
}
