package pcd.multi_threaded_version.util;

public interface BoundedBuffer<Item> {

    void put(Item item) throws InterruptedException;
    
    Item poll();
    
}
