public class Node<T> {
	private final T value;
	private final Node<T> left;
	private final Node<T> right;
	
	Node(T value, Node<T> left, Node<T> right) {
		this.left = left;
		this.right = right;
		this.value = value;
	}
	
	public Node<T> getLeft() {
		return left;
	}
	
	public Node<T> getRight() {
		return right;
	}
	
	public T getValue() {
		return value;
	}
}