package jpf;

public interface BoundedBuffer<Item> {

    void put(Item item) throws InterruptedException;
    
    Item poll();
    
}
