package run.backend.global.util;

import org.springframework.stereotype.Component;
import run.backend.global.dto.DateRange;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;

@Component
public class DateRangeUtil {

    public DateRange getWeekRange(LocalDate today) {

        return new DateRange(today.with(DayOfWeek.MONDAY), today.with(DayOfWeek.SUNDAY));
    }

    public DateRange getMonthRange(int year, int month) {

        YearMonth ym = YearMonth.of(year, month);
        return new DateRange(ym.atDay(1), ym.atEndOfMonth());
    }
}
