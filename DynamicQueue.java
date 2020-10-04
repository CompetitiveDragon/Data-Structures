
public class DynamicQueue<Item> {
    private LinkedListItem<Item> front;
    private LinkedListItem<Item> rear;
    private int size = 0;

    public DynamicQueue() {
        size = 0;
        front = null;
        rear = null;
    }

    public void push(Item item) {
        LinkedListItem<Item> newNode = new LinkedListItem<>(item, null);
        if (this.isEmpty()) {
            rear = front = newNode;
        }
        else {
            rear.setNext(newNode);
            rear = newNode;
        }
        size++;
    }

    public Item pop() {
        if (this.isEmpty()) {
            return null;
        }
        Item res = front.getValue();
        front = front.getNext();
        size--;
        return res;
    }

    public Item peek() {
        return front.getValue();
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
}
