package joeltio.pulse;

public class FixedQueue<T> {
    private int maxSize;
    private int size;
    private Object[] array;

    public FixedQueue(int maxSize) {
        this.maxSize = maxSize;
        this.size = 0;
        this.array = new Object[maxSize];
    }

    public boolean add(T o) {
        System.out.println(this.size);
        if (this.size != 0) {
            Object[] tmp = this.array;

            if (this.size == this.maxSize) {
                System.arraycopy(tmp, 0, this.array, 1, this.size-1);
            } else {
                System.arraycopy(tmp, 0, this.array, 1, this.size);
            }
        }

        this.array[0] = o;

        if (this.size != this.maxSize) {
            this.size += 1;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public T remove() {
        if (this.size > 0) {
            T o = (T) this.array[this.size-1];
            this.array[this.size-1] = null;

            this.size -= 1;
            return o;
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public T last() {
        return (T) this.array[this.size-1];
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        return (T) this.array[index];
    }

    public void copyToArray(T[] dest) {
        System.arraycopy(this.array, 0, dest, 0, this.size);
    }

    public int size() {
        return this.size;
    }

    public int maxSize() {
        return this.maxSize;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void clear() {
        this.array = new Object[maxSize];
    }
}
