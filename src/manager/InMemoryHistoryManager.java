package manager;

import task.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            throw new IllegalArgumentException("Задача не должна быть нулевой.");
        }

        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }

        Node newNode = new Node(null, null, task);

        if (historyMap.isEmpty()) {
            firstNode = newNode;
        } else {
            lastNode.setNext(newNode);
            newNode.setPrev(lastNode);
        }
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

        if (taskNode == firstNode && taskNode == lastNode) {
            firstNode = null;
            lastNode = null;
        } else if (taskNode == firstNode) {
            firstNode = nextNode;
            if (firstNode != null) {
                firstNode.setPrev(null);
            }
        } else if (taskNode == lastNode) {
            lastNode = prevNode;
            if (lastNode != null) {
                lastNode.setNext(null);
            }
        } else {
            if (prevNode != null) {
                prevNode.setNext(nextNode);
            }
            if (nextNode != null) {
                nextNode.setPrev(prevNode);
            }
        }

        historyMap.remove(taskId);
    }

    public List<Task> getHistory() {
        List<Task> historyOrdered = new ArrayList<>();
        Node currentNode = firstNode;
        while (currentNode != null) {
            historyOrdered.add(currentNode.getNodeTask());
            currentNode = currentNode.getNext();
        }
        return historyOrdered;
    }
}