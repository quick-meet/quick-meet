package ru.imo.quickmeet.service.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.imo.quickmeet.dto.Participant;
import ru.imo.quickmeet.service.TimeSlotMerger;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static ru.imo.quickmeet.TestHelpers.todayParticipant;

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
    void testCalculatorValidCases(int meetingTimeMinutes, List<Participant> participants) {
        when(merger.merge(anyList()))
                .thenAnswer(it -> it.getArgument(0));

        var foundSlot = calculator.calculate(meetingTimeMinutes, participants);
        assertTrue(foundSlot.isPresent());

        var timeSlot = foundSlot.get();
        assertEquals(meetingTimeMinutes * 60L, timeSlot.getDuration().getSeconds());
    }

    @MethodSource("calculatorFailCases")
    @ParameterizedTest
    void testCalculatorFailCases(int meetingTimeMinutes, List<Participant> participants) {
        when(merger.merge(anyList()))
                .thenAnswer(it -> it.getArgument(0));

        var foundSlot = calculator.calculate(meetingTimeMinutes, participants);
        assertFalse(foundSlot.isPresent());
    }

    public static Stream<Arguments> calculatorValidCases() {
        return Stream.of(
                Arguments.of(
                        5,
                        List.of(
                                todayParticipant("10:00", "11:00"),
                                todayParticipant("10:05", "10:15"),
                                todayParticipant("10:03", "10:10")
                        )
                ),
                Arguments.of(
                        10,
                        List.of(
                                todayParticipant("10:00", "11:00", "11:01", "11:02", "11:10", "12:30"),
                                todayParticipant("11:00", "12:10"),
                                todayParticipant("12:00", "12:15")
                        )
                )
        );
    }

    public static Stream<Arguments> calculatorFailCases() {
        return Stream.of(
                Arguments.of(
                        5,
                        List.of(
                                todayParticipant("10:00", "10:05"),
                                todayParticipant("10:05", "10:15")
                        )
                ),
                Arguments.of(
                        5,
                        List.of(
                                todayParticipant("10:00", "10:05"),
                                todayParticipant("10:03", "10:08")
                        )
                )
        );
    }

}