package manager;

import task.Task;

public class Node {

    private Node nextNode;
    private Node prevNode;
    private final Task task;


    public Node(Node prevNode, Node nextNode, Task task) {
        this.prevNode = prevNode;
        this.nextNode = nextNode;
        this.task = task;
    }


    public Node getNext() {
        return nextNode;
    }


    public void setNext(Node nextNode) {
        this.nextNode = nextNode;
    }


    public Node getPrev() {
        return prevNode;
    }


    public void setPrev(Node prevNode) {
        this.prevNode = prevNode;
    }


    public Task getNodeTask() {
        return task;
    }
}