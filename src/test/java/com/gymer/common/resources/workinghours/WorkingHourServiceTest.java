package com.gymer.common.resources.workinghours;

import com.gymer.common.resources.workinghours.entity.Day;
import com.gymer.common.resources.workinghours.entity.WorkingHour;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Time;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class WorkingHourServiceTest {

    @Mock
    private final Pageable pageable = PageRequest.of(0, 10);

    @Mock
    private final Page<WorkingHour> page = getTestPageData();

    @Mock
    private WorkingHourRepository workingHourRepository;

    @InjectMocks
    private WorkingHourService workingHourService;

    @Test
    public void should_returnPageOfWorkingHours_when_findAllWithValidData() {
        given(workingHourRepository.findAll(pageable)).willReturn(page);

        Page<WorkingHour> obtainedPage = workingHourService.getAllElements(pageable);

        assertArrayEquals(page.getContent().toArray(new WorkingHour[0]), obtainedPage.getContent().toArray(new WorkingHour[0]));
    }

    @Test
    public void should_returnPageOfWorkingHours_when_findAllContainingWithValidData() {
        given(workingHourRepository.findAllByStartHourContainsOrEndHourContains(Time.valueOf("9:00:00"), Time.valueOf("9:00:00"), pageable)).willReturn(page);

        Page<WorkingHour> obtainedPage = workingHourService.findAllContaining(pageable, "9:00:00");

        assertArrayEquals(page.getContent().toArray(new WorkingHour[0]), obtainedPage.getContent().toArray(new WorkingHour[0]));
    }

    @Test
    public void should_returnValidWorkingHour_when_getElementByIdWithValidId() {
        WorkingHour workingHour = new WorkingHour(Day.SUNDAY, Time.valueOf("9:00:00"), Time.valueOf("10:00:00"));
        workingHour.setId(1L);
        given(workingHourRepository.findById(1L)).willReturn(Optional.of(workingHour));

        WorkingHour obtainedWorkingHour = workingHourService.getElementById(1L);

        assertEquals(workingHour, obtainedWorkingHour);
    }

    @Test
    public void should_returnNotFoundError_when_getElementByIdWithNonExistingId() {
        given(workingHourRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> workingHourService.getElementById(1L));
    }

    @Test
    public void should_returnTrue_when_isElementExistByIdWithValidData() {
        given(workingHourRepository.existsById(1L)).willReturn(true);

        boolean obtainedStatus = workingHourService.isElementExistById(1L);

        assertTrue(obtainedStatus);
    }

    @Test
    public void should_returnFalse_when_isElementExistByIdWithNonExistingAddress() {
        given(workingHourRepository.existsById(1L)).willReturn(false);

        boolean obtainedStatus = workingHourService.isElementExistById(1L);

        assertFalse(obtainedStatus);
    }

    private Page<WorkingHour> getTestPageData() {
        List<WorkingHour> workingHours = new LinkedList<>();
        WorkingHour workingHour1 = new WorkingHour(Day.MONDAY, Time.valueOf("10:00:00"), Time.valueOf("12:00:00"));
        workingHour1.setId(1L);
        workingHours.add(workingHour1);
        WorkingHour workingHour2 = new WorkingHour(Day.THURSDAY, Time.valueOf("9:00:00"), Time.valueOf("10:00:00"));
        workingHour2.setId(2L);
        workingHours.add(workingHour2);
        WorkingHour workingHour3 = new WorkingHour(Day.WEDNESDAY, Time.valueOf("11:00:00"), Time.valueOf("12:00:00"));
        workingHour3.setId(3L);
        workingHours.add(workingHour3);
        return new PageImpl<>(workingHours);
    }

}
