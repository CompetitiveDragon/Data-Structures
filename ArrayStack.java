public class ArrayStack<Item> {
    private Item[] array = null;
    private int size = 0;

    private static final int DEFAULT_CAPACITY = 128;

    @SuppressWarnings("unchecked")
    public ArrayStack(int capacity) {
        this.array = (Item[]) new Object[capacity];
    }

    @SuppressWarnings("unchecked")
    public ArrayStack() {
        this.array = (Item[]) new Object[DEFAULT_CAPACITY];
    }

    public void push(Item item) {
        if (size == array.length) {
            extendArray();
        }
        array[size++] = item;
    }

    public Item pop() {
        if (size == 0)
            return null;
        return array[--size];
    }

    public Item peek() {
        return array[size - 1];
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
            array[i] = temp[i];

    }
}
