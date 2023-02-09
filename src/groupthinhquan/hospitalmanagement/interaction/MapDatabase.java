/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupthinhquan.hospitalmanagement.interaction;

/**
 *
 * @author Thinh
 * @param <K>: Key used for mapping
 * @param <O>: Objects stored in the database
 */

public interface MapDatabase<K, O> {
    public boolean add(K key, O obj);
    
    public int remove(K key);
    
    public boolean has(K key);
    
    public O get(K Key);
}
