/*
 * SynchronizedQueue.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.unifiedsearchdemo;

import java.util.*;


/**
 * A synchronized blocking queue of limited capacity. The dequeue operations
 * will block if the queue is empty and return as elements are added to the
 * queue. The enqueue operations will block if the queue size reached its
 * maximum capacity. It is safe to interrupt blocked calls. 
 */
public class SynchronizedQueue
{    
    private Vector _vector;    
    private final int _capacity;    
    private boolean _shutDown;


    /**
     * Creates a new SynchronizedQueue object
     * 
     * @param capacity The max number of objects to hold in the queue
     * @throws IllegalArgumentException if capacity is not greater than zero
     */
    public SynchronizedQueue(int capacity)
    {
        if(capacity <= 0)
        {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
        _capacity = capacity;
        _vector = new Vector(_capacity);
    }


    /**
     * Reads and removes the queue's head element. The call to this method
     * blocks if the queue is empty.
     * 
     * @return The queue head element or null if the queue has been shut down
     * 
     */
    public synchronized Object dequeue() 
    {        
        if(_shutDown)
        {
            return null;
        }
        
        while(_vector.isEmpty())
        {
            try
            {
                wait();
            }
            catch(InterruptedException e)
            {                
            }
            
            if(_shutDown)
            {
                return null;
            }
        }
        Object result = _vector.elementAt(_vector.size() - 1);
        _vector.removeElementAt(_vector.size() - 1);
        if(_vector.size() < _capacity && _vector.size() > 0)
        {
            notify();
        }
        
        return result;
    }


    /**
     * Adds an element to the queue tail. The call to this method blocks if the
     * queue's maximum capacity is reached.
     * 
     * @param obj The element to add to the queue
     * @throws IllegalArgumentException if Object parameter is null 
     */
    public synchronized void enqueue(Object obj) 
    {
        if(obj == null)
        {
            throw new IllegalArgumentException("Nulls are not allowed in the queue.");
        }   
             
        if(_shutDown)
        {
            return;
        }
        
        while(_vector.size() >= _capacity)
        {
            try
            {
                wait();
            }
            catch(InterruptedException e)
            {                
            }
            
            if(_shutDown)
            {
                return;
            }
        }
        
        _vector.insertElementAt(obj, 0);
        notify();
    }


    /**
     * Shuts down the queue. The queue can not be reused.
     */
    public synchronized void shutdown()
    {
        
        _shutDown = true;
        notifyAll();
    }
}
