
public class ArrayQueue<Item> {
    private Item[] array = null;
    private int start = 0;
    private int size = 0;

    private static final int DEFAULT_CAPACITY = 128;

    @SuppressWarnings("unchecked")
    public ArrayQueue(int capacity) {
        this.array = (Item[]) new Object[capacity];
    }

    @SuppressWarnings("unchecked")
    public ArrayQueue() {
        this.array = (Item[]) new Object[DEFAULT_CAPACITY];
    }

    public void push(Item item) {
        if (size == array.length) {
            extendArray();
        }
        array[(start + size) % array.length] = item;
        size++;
    }

    public Item pop() {
        if (size == 0)
            return null;
        size--;
        Item res = array[start];
        start = (start + 1) % array.length;
        return res;
    }

    public Item peek() {
        return array[start];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return array.length;
    }

    @SuppressWarnings("unchecked")
    private void extendArray() {
        Item[] temp = array;
        array = (Item[]) new Object[temp.length * 2];
        for (int i = 0; i < temp.length; i++)
            array[i] = temp[(start + i) % temp.length];
        start = 0;
    }

}
