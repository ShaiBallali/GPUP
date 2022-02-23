package engine.graph;

import dto.enums.Position;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PositionCounter {
    private Map<Position, Integer> counter = new HashMap<>();

    public PositionCounter () {
        counter.put(Position.LEAF, 0 );
        counter.put(Position.MIDDLE, 0 );
        counter.put(Position.INDEPENDENT, 0 );
        counter.put(Position.ROOT, 0 );
    }

     public void increment (Position position) {
        int count = counter.get(position);
        counter.remove(position);
        counter.put(position, ++count);
     }

    public Map<Position, Integer> getPositionCounter () {
        return counter;
     }
}
