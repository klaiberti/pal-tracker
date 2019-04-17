package io.pivotal.pal.tracker;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {


    private JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);

    }


    @Override
    public TimeEntry create(TimeEntry timeEntry) {

        String sqlStatement = "INSERT INTO time_entries (project_id, user_id, date, hours) VALUES (? ,? ,? ,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sqlStatement, RETURN_GENERATED_KEYS);
            ps.setLong(1, timeEntry.getProjectId());
            ps.setLong(2, timeEntry.getUserId());
            ps.setDate(3, Date.valueOf(timeEntry.getDate().toString()));
            ps.setInt(4, timeEntry.getHours());
            return ps;
        }, keyHolder);

        long index = keyHolder.getKey().longValue();

        TimeEntry createdTimeEntry = new TimeEntry(index, timeEntry.getProjectId(), timeEntry.getUserId(), timeEntry.getDate(), timeEntry.getHours());

        return createdTimeEntry;
    }

    @Override
    public TimeEntry find(long timeEntryId) {

        try {
            Map<String, Object> foundEntryMap = jdbcTemplate.queryForMap("Select * from time_entries where id = ?", timeEntryId);
            long id = (long) foundEntryMap.get("id");
            long project_id = (long) foundEntryMap.get("project_id");
            long user_id = (long) foundEntryMap.get("user_id");
            LocalDate localDate = LocalDate.parse(foundEntryMap.get("date").toString());
            int hours = (int) foundEntryMap.get("hours");

            TimeEntry foundTimeEntry = new TimeEntry(id, project_id, user_id, localDate, hours);

            return foundTimeEntry;

        } catch (EmptyResultDataAccessException e)
        {
            return null;
        }
    }

    @Override
    public List<TimeEntry> list() {

        try {
            List<TimeEntry> timeEntries = new ArrayList<>();

            List<Map<String, Object>> foundEntries = jdbcTemplate.queryForList("Select * from time_entries");


            foundEntries.forEach(entry -> {
                long id = (long) entry.get("id");
                long project_id = (long) entry.get("project_id");
                long user_id = (long) entry.get("user_id");
                LocalDate localDate = LocalDate.parse(entry.get("date").toString());
                int hours = (int) entry.get("hours");

                TimeEntry foundTimeEntry = new TimeEntry(id, project_id, user_id, localDate, hours);
                timeEntries.add(foundTimeEntry);
            });

            return timeEntries;

        } catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public TimeEntry update(long id, TimeEntry updateTimeEntry) {

        String sqlStatement = "UPDATE time_entries SET project_id = ?, user_id = ?, date = ?, hours = ? WHERE id = ?";
        jdbcTemplate.update(sqlStatement, updateTimeEntry.getProjectId(), updateTimeEntry.getUserId(), Date.valueOf(updateTimeEntry.getDate().toString()), updateTimeEntry.getHours(), id);

        return find(id);
    }

    @Override
    public void delete(long timeEntryId) {

        String sqlStatement = "DELETE FROM time_entries WHERE id = " + timeEntryId;
        jdbcTemplate.execute(sqlStatement);

    }
}
