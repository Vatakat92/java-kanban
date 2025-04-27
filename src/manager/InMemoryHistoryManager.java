package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> historyMap;
    private Node firstNode;
    private Node lastNode;


    public InMemoryHistoryManager() {
        historyMap = new HashMap<>();
        firstNode = null;
        lastNode = null;
    }

    @Override
    public void add(Task task) {

        if (task == null) {
            throw new IllegalArgumentException("Task must not be null.");
        }

        Node newNode = new Node(null, null, new Task(task));

        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }

        if (historyMap.isEmpty()) {
            firstNode = newNode;
        }
        if (lastNode != null) {
            lastNode.setNext(newNode);
        }

        newNode.setPrev(lastNode);
        lastNode = newNode;
        historyMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(int taskId) {

        if (!historyMap.containsKey(taskId)) {
            return;
        }

        Node taskNode = historyMap.get(taskId);
        Node prevNode = taskNode.getPrev();
        Node nextNode = taskNode.getNext();

        if (taskNode.equals(lastNode)) {
            if (prevNode != null) {
                prevNode.setNext(null);
                lastNode = prevNode;
            }
        } else if (taskNode.equals(firstNode)) {
            if (nextNode != null) {
                nextNode.setPrev(null);
                firstNode = nextNode;
            }
        } else {
            prevNode.setNext(nextNode);
            nextNode.setPrev(prevNode);
        }

        historyMap.remove(taskId);
    }


    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyOrdered = new ArrayList<>();

        if (historyMap.isEmpty()) {
            return historyOrdered;
        }

        Node currentNode = firstNode;

        do {
            historyOrdered.add(currentNode.getNodeTask());
            currentNode = currentNode.getNext();
        } while (currentNode != null);

        return historyOrdered;
    }
}


