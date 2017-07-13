package joeltio.pulse;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FixedQueueTest {
    @Test
    public void add_pushes_element() {
        FixedQueue<Integer> fq = new FixedQueue<>(5);
        fq.add(1);
        assertEquals((Integer) 1, fq.last());
        fq.add(2);
        assertEquals((Integer) 1, fq.last());
        fq.add(3);
        assertEquals((Integer) 1, fq.last());
    }

    @Test
    public void remove_pops_element() {
        FixedQueue<Integer> fq = new FixedQueue<>(5);
        fq.add(1);
        fq.add(2);
        fq.add(3);
        assertEquals((Integer) 1, fq.remove());
        assertEquals((Integer) 2, fq.remove());
        assertEquals((Integer) 3, fq.remove());
    }

    @Test
    public void pushing_over_maximum_removes_last() {
        FixedQueue<Integer> fq = new FixedQueue<>(3);
        fq.add(1);
        fq.add(2);
        fq.add(3);
        fq.add(4);
        assertEquals((Integer) 2, fq.last());
        fq.add(5);
        assertEquals((Integer) 3, fq.last());
        fq.add(6);
        assertEquals((Integer) 4, fq.last());
    }

    @Test
    public void get_array() {
        FixedQueue<Integer> fq = new FixedQueue<>(3);
        fq.add(1);
        fq.add(2);
        fq.add(3);

        Integer[] i = new Integer[3];
        fq.copyToArray(i);
        assertEquals((Integer) 3, i[0]);
        assertEquals((Integer) 2, i[1]);
        assertEquals((Integer) 1, i[2]);
    }
}