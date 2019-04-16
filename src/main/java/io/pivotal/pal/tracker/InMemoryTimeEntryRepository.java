package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTimeEntryRepository implements TimeEntryRepository{

    public InMemoryTimeEntryRepository(){}

    private HashMap<Long, TimeEntry> timeEntryHashMap = new HashMap<>();

    private long index = 0;

    public TimeEntry create(TimeEntry timeEntry) {
        index = index + 1L;
        TimeEntry createdTimeEntry = new TimeEntry(index, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
        timeEntryHashMap.put(index, createdTimeEntry);
        return createdTimeEntry;
    }

    public TimeEntry find(long index) {
        return timeEntryHashMap.get(index);
    }

    public List<TimeEntry> list() {
        List<TimeEntry> timeEntries = new ArrayList<>();

        for(int i = 1; i <= timeEntryHashMap.size(); i++)
            timeEntries.add(timeEntryHashMap.get((long) i));
        return timeEntries;
    }

    public TimeEntry update(long index, TimeEntry timeEntry) {
        TimeEntry updatedTimeEntry = new TimeEntry(index, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());
        timeEntryHashMap.replace(index, updatedTimeEntry);
        return timeEntryHashMap.get(index);
    }

    public void delete(long index) {
        timeEntryHashMap.remove(index);

    }
}
