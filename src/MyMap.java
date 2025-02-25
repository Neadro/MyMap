import java.util.*;

public class MyMap<K, V> implements Book<K, V> {
    private Node<K, V>[] hashTable;
    private int size;
    private float loadFactor;

    public MyMap() {
        hashTable = new Node[16];
        loadFactor = hashTable.length * 0.75f;
    }

    public boolean insert(final K key, final V value) {
        if (size + 1 >= loadFactor) {
            loadFactor *= 2;
            doubleArray();
        }

        Node<K, V> newNode = new Node<>(key, value);
        int index = bucketIndex(key);
        if (hashTable[index] == null) {
            return simpleAdd(index, newNode);
        }

        List<Node<K, V>> listOfNode = hashTable[index].getNodes();

        for (Node<K, V> node : listOfNode) {
            if (updateKey(node, newNode, value) || collisionKey(node, newNode, listOfNode)) {
                return true;
            }
        }
        return false;
    }

    private boolean simpleAdd(int index, Node<K, V> newNode) {
        hashTable[index] = new Node<>(null, null);
        hashTable[index].getNodes().add(newNode);
        size++;
        return true;
    }

    private boolean updateKey(final Node<K, V> nodeFromList, final Node<K, V> newNode, final V value) {
        if(newNode.getKey() == null) {
            if(nodeFromList.getKey() == null) {
                nodeFromList.setValue(value);
                return true;
            }
        }
        if (newNode.getKey().equals(nodeFromList.getKey()) && !newNode.getValue().equals(nodeFromList.getValue())) {
            nodeFromList.setValue(value);
            return true;
        }
        return false;
    }

    private boolean collisionKey(final Node<K, V> nodeFromList, final Node<K, V> newNode, List<Node<K, V>> nodes) {
        if (newNode.hashCode() == nodeFromList.hashCode() &&
                !Objects.equals(newNode.key, nodeFromList.key) &&
                !Objects.equals(newNode.value, nodeFromList.value)) {

            nodes.add(newNode);
            size++;
            return true;
        }
        return false;
    }

    private void doubleArray() {
        Node<K, V>[] oldTable = hashTable;
        hashTable = new Node[oldTable.length * 2];
        size = 0;

        for (Node<K, V> node : oldTable) {
            if (node != null) {
                for (Node<K, V> n : node.getNodes()) {
                    insert(n.key, n.value);
                }
            }
        }
    }

    public boolean delete(final K key) {
        int index = bucketIndex(key);
        if (hashTable[index] == null) {
            return false;
        }
        List<Node<K, V>> nodeList = hashTable[index].getNodes();
        Iterator<Node<K, V>> iterator = nodeList.iterator();
        while (iterator.hasNext()) {
            Node<K, V> node = iterator.next();
            if (key.equals(node.getKey())) {
                iterator.remove();
                size--;
                if (nodeList.isEmpty()) {
                    hashTable[index] = null;
                }
                return true;
            }
        }
        return false;
    }

    public Optional<V> get(final K key) {
        int index = bucketIndex(key);
        if (index < hashTable.length && hashTable[index] != null) {
            List<Node<K, V>> list = hashTable[index].getNodes();
            for (Node<K, V> node : list) {
                if (key.equals(node.getKey())) {
                    return Optional.of(node.getValue());
                }
            }
        }
        return Optional.empty();
    }

    public int size() {
        return size;
    }

    private int bucketIndex(final K key) {
        if (key == null) {
            return 0;
        }
        Node node = new Node<>(key, null);
        int hashCodeBySergey = node.hashCode();
        return hashCodeBySergey < 0
                ? ((hashCodeBySergey % (hashTable.length / 2)) * (-2)) - 1
                : (hashCodeBySergey % (hashTable.length / 2)) * 2;
    }

    public Iterator<V> iterator() {
        return new Iterator<V>() {
            int counterArray = 0;
            int valuesCounter = 0;
            Iterator<Node<K, V>> subIterator = null;

            public boolean hasNext() {
                if (valuesCounter == size) {
                    return false;
                }
                while ((subIterator == null || !subIterator.hasNext()) && counterArray < hashTable.length) {
                    counterArray++;
                    while (counterArray < hashTable.length && hashTable[counterArray] == null) {
                        counterArray++;
                    }
                    if (counterArray < hashTable.length) {
                        subIterator = hashTable[counterArray].getNodes().iterator();
                    }
                }
                return subIterator != null && subIterator.hasNext();
            }

            public V next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("В итераторе больше нет элементов");
                }
                Node<K, V> nextNode = subIterator.next();
                valuesCounter++;
                return nextNode.getValue();
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node<K, V> node : hashTable) {
            if (node != null) {
                sb.append(node.toString());
            }
        }
        sb.append("\n");
        sb.append("size = " + size + "\n");
        sb.append("loadFactor = " + loadFactor + "\n");
        return sb.toString();
    }

    private class Node<K, V> {
        private List<Node<K, V>> nodes;
        private int hash;
        private K key;
        private V value;

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
            nodes = new LinkedList<Node<K, V>>();
        }

        private List<Node<K, V>> getNodes() {
            return nodes;
        }

        private int hash() {
            return hashCode() % hashTable.length;
        }

        private K getKey() {
            return key;
        }

        private V getValue() {
            return value;
        }

        private void setValue(V value) {
            this.value = value;
        }

        public int hashCode() {

            hash = 31;
            if (key == null) {
                return 0;
            }
            hash = hash * 17 + key.hashCode();
            if (value != null) {
                hash = hash * 17 + value.hashCode();
            }

            return hash;
        }
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Node<?, ?> node = (Node<?, ?>) o;
            return hash == node.hash && Objects.equals(getNodes(), node.getNodes()) && Objects.equals(getKey(), node.getKey()) && Objects.equals(getValue(), node.getValue());
        }
        @Override
        public String toString() {
            return "Node " +
                    "nodes=" + nodes +
                    ", hash=" + hash +
                    ", key=" + key +
                    ", value=" + value +
                    "} \n";
        }
    }
}