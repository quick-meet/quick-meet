package ru.imo.quickmeet.service.impl;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.imo.quickmeet.dto.TimeSlot;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.imo.quickmeet.TestHelpers.todaySlot;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@ExtendWith(MockitoExtension.class)
class SimpleTimeSlotMergerTest {
    @InjectMocks
    private SimpleTimeSlotMerger merger;

    @ParameterizedTest
    @MethodSource("mergeWithoutCollisionsCases")
    void testMergeWithoutCollisions(List<TimeSlot> timeSlots) {
        var actual = merger.merge(timeSlots);
        assertEquals(timeSlots.size(), actual.size());
        assertEquals(new HashSet<>(timeSlots), new HashSet<>(actual));
    }

    @ParameterizedTest
    @MethodSource("mergeCases")
    void testMerge(List<TimeSlot> timeSlots, List<TimeSlot> excepted) {
        var actual = merger.merge(timeSlots);
        assertEquals(excepted, actual);
    }

    public static Stream<Arguments> mergeWithoutCollisionsCases() {
        return Stream.of(
                Arguments.of(Collections.emptyList()),
                Arguments.of(
                        List.of(
                                todaySlot("10:00", "11:00")
                        )
                ),
                Arguments.of(
                        List.of(
                                todaySlot("10:00", "11:00"),
                                todaySlot("11:01", "12:00")
                        )
                ),
                Arguments.of(
                        List.of(
                                todaySlot("11:01", "12:00"),
                                todaySlot("10:00", "11:00")
                        )
                )
        );
    }

    public static Stream<Arguments> mergeCases() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                todaySlot("10:00", "11:00"),
                                todaySlot("11:00", "12:00")
                        ),
                        List.of(
                                todaySlot("10:00", "12:00")
                        )
                ),
                Arguments.of(
                        List.of(
                                todaySlot("10:00", "11:00"),
                                todaySlot("10:15", "10:45"),
                                todaySlot("11:00", "12:00")
                        ),
                        List.of(
                                todaySlot("10:00", "12:00")
                        )
                ),
                Arguments.of(
                        List.of(
                                todaySlot("10:00", "11:00"),
                                todaySlot("11:00", "12:00"),

                                todaySlot("12:05", "13:00"),
                                todaySlot("13:00", "23:00")
                        ),
                        List.of(
                                todaySlot("10:00", "12:00"),
                                todaySlot("12:05", "23:00")
                        )
                )
        );
    }

}