package dto.dtoServer.graphAction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WhatIfDetails {
    private String[] targetsName;
    private int logSize;

    public WhatIfDetails (Set<String> targetsName) {
        this.targetsName = new String[targetsName.size()];
        this.logSize = 0;

        for (String name : targetsName) {
            this.targetsName[logSize++] = name;
        }
    }

    public Set<String > getPath () {
        Set<String> result = new HashSet<>();
        for ( String name : targetsName) {
            result.add(name);
        }
        return result;
    }
}
