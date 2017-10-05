package ru.track.list;

import java.util.NoSuchElementException;

/**
 * Должен наследовать List
 * <p>
 * Должен иметь 2 конструктора
 * - без аргументов - создает внутренний массив дефолтного размера на ваш выбор
 * - с аргументом - начальный размер массива
 */
public class MyArrayList extends List
{
    int[] array;
    int count;
    int minLength=16;
    double mult=1.5;
    public MyArrayList()
    {
        count=0;
        array=new int[minLength];
    }

    public MyArrayList(int capacity)
    {
        count=0;
        capacity=Math.max(capacity, minLength);
        array=new int[capacity];
    }

    @Override
    void add(int item)
    {
        array[count]=item;
        ++count;
        resize();
    }

    @Override
    int remove(int idx) throws NoSuchElementException
    {
        if(idx<0 || idx>=count) throw  new NoSuchElementException();
        int val=array[idx];
        for(int i=idx;i<count-1;++i) array[i]=array[i+1];
        --count;
        resize();

        return val;
    }

    @Override
    int get(int idx) throws NoSuchElementException
    {
        if(idx<0 || idx>=count) throw  new NoSuchElementException();
        return array[idx];
    }

    @Override
    int size()
    {
        return count;
    }

    private void resize()
    {
        int newLength = array.length;
        //check if container is filled
        if (count == array.length - 1) newLength = (int)Math.ceil(newLength*mult);
        else
        {
            //if not, check if there is too much free space
            int decreasedSize = (int)(array.length / mult);
            if (minLength <= decreasedSize && count < decreasedSize / mult) newLength = decreasedSize;
        }
        if (newLength != array.length)
        {
            int[] newarray=new int[newLength];
            System.arraycopy(array, 0,newarray,0, count);
            array=newarray;
        }
    }
}
