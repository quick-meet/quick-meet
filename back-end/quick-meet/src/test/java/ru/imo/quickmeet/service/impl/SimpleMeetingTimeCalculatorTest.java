package ru.imo.quickmeet.service.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.imo.quickmeet.database.entity.UnavailableTimeSlot;
import ru.imo.quickmeet.dto.TimeSlot;
import ru.imo.quickmeet.service.TimeSlotMerger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static ru.imo.quickmeet.TestHelpers.*;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@ExtendWith(MockitoExtension.class)
class SimpleMeetingTimeCalculatorTest {

    @Mock
    private TimeSlotMerger merger;
    @InjectMocks
    private SimpleMeetingTimeCalculator calculator;

    @MethodSource("calculatorValidCases")
    @ParameterizedTest
    void testCalculatorValidCases(int meetingTimeMinutes,
                                  LocalDateTime leftBound,
                                  LocalDateTime rightBound,
                                  List<UnavailableTimeSlot> unavailableTimeSlots,
                                  TimeSlot exceptedSlot) {
        when(merger.merge(anyList()))
                .thenAnswer(it -> it.getArgument(0));

        var foundSlot = calculator.calculate(meetingTimeMinutes, leftBound, rightBound, unavailableTimeSlots);
        assertTrue(foundSlot.isPresent());

        var timeSlot = foundSlot.get();
        assertEquals(meetingTimeMinutes * 60L, timeSlot.getDuration().getSeconds());
        assertEquals(exceptedSlot, timeSlot);
    }

    @MethodSource("calculatorFailCases")
    @ParameterizedTest
    void testCalculatorFailCases(int meetingTimeMinutes,
                                 LocalDateTime leftBound,
                                 LocalDateTime rightBound,
                                 List<UnavailableTimeSlot> unavailableTimeSlots) {
        when(merger.merge(anyList()))
                .thenAnswer(it -> it.getArgument(0));

        var foundSlot = calculator.calculate(meetingTimeMinutes, leftBound, rightBound, unavailableTimeSlots);
        assertFalse(foundSlot.isPresent());
    }

    public static Stream<Arguments> calculatorValidCases() {
        return Stream.of(
                Arguments.of(
                        5,
                        today("10:00"),
                        today("11:00"),
                        List.of(
                                todayUnavailableSlot("09:00", "10:00"),
                                todayUnavailableSlot("10:05", "10:15")
                        ),
                        todaySlot("10:00", "10:05")
                ),
                Arguments.of(
                        5,
                        today("10:00"),
                        today("11:00"),
                        List.of(
                                todayUnavailableSlot("09:00", "10:00")
                        ),
                        todaySlot("10:00", "10:05")
                ),
                Arguments.of(
                        5,
                        today("10:00"),
                        today("11:00"),
                        List.of(
                                todayUnavailableSlot("10:00", "10:05")
                        ),
                        todaySlot("10:05", "10:10")
                ),
                Arguments.of(
                        5,
                        today("10:00"),
                        today("11:00"),
                        List.of(
                                todayUnavailableSlot("10:00", "10:55")
                        ),
                        todaySlot("10:55", "11:00")
                ),
                Arguments.of(
                        5,
                        today("10:00"),
                        today("11:00"),
                        List.of(
                                todayUnavailableSlot("10:30", "10:35")
                        ),
                        todaySlot("10:00", "10:05")
                )
        );
    }

    public static Stream<Arguments> calculatorFailCases() {
        return Stream.of(
                Arguments.of(
                        5,
                        today("10:00"),
                        today("11:00"),
                        List.of()
                ),
                Arguments.of(
                        5,
                        today("10:00"),
                        today("11:00"),
                        List.of(
                                todayUnavailableSlot("10:00", "10:30"),
                                todayUnavailableSlot("10:30", "11:15")
                        )
                ),
                Arguments.of(
                        5,
                        today("10:00"),
                        today("12:00"),
                        List.of(
                                todayUnavailableSlot("09:00", "10:05"),
                                todayUnavailableSlot("10:03", "12:00")
                        )
                ),
                Arguments.of(
                        5,
                        today("10:00"),
                        today("12:00"),
                        List.of(
                                todayUnavailableSlot("09:00", "10:05"),
                                todayUnavailableSlot("10:09", "12:00")
                        )
                ),
                Arguments.of(
                        5,
                        today("10:00"),
                        today("11:00"),
                        List.of(
                                todayUnavailableSlot("09:00", "10:56"),
                                todayUnavailableSlot("11:00", "12:00")
                        )
                ),
                Arguments.of(
                        5,
                        today("10:00"),
                        today("11:00"),
                        List.of(
                                todayUnavailableSlot("10:00", "11:00")
                        )
                ),
                Arguments.of(
                        5,
                        today("10:00"),
                        today("11:00"),
                        List.of(
                                todayUnavailableSlot("10:00", "10:57")
                        )
                )
        );
    }

}