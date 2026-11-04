package pcd.executor_version.util;

public interface BoundedBuffer<Item> {

    void put(Item item) throws InterruptedException;
    
    Item poll();
    
}
