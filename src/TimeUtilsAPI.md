# TimeUtils API Documentation

## Overview

The `TimeUtils` class is a utility class for handling date and time operations in the Library Management System. It provides methods for parsing, formatting, and calculating time differences between dates in various formats.

## Date Formats

The class supports the following date formats:

1. **Date Only**: `yyyy-MM-dd` (e.g., "2023-01-15")
2. **Date with Pipe Separator**: `yyyy-MM-dd|HH:mm` (e.g., "2023-01-15|14:30")
3. **Date with T Separator**: `yyyy-MM-ddTHH:mm` (e.g., "2023-01-15T14:30")

## API Methods

### calculateHoursBetween

```java
public static long calculateHoursBetween(String start, String end)
```

Calculates the number of full hours between two date-time strings.

**Parameters:**
- `start`: The start date-time string
- `end`: The end date-time string

**Returns:**
- The number of hours between start and end

**Throws:**
- `IllegalArgumentException` if either string is not in a supported format

**Example:**
```java
long hours = TimeUtils.calculateHoursBetween("2023-01-01|10:00", "2023-01-02|14:30");
// Returns: 28 (full hours between the two times)
```

### calculateMinutesBetween

```java
public static long calculateMinutesBetween(String start, String end)
```

Calculates the number of full minutes between two date-time strings.

**Parameters:**
- `start`: The start date-time string
- `end`: The end date-time string

**Returns:**
- The number of minutes between start and end

**Throws:**
- `IllegalArgumentException` if either string is not in a supported format

**Example:**
```java
long minutes = TimeUtils.calculateMinutesBetween("2023-01-01|10:00", "2023-01-01|11:30");
// Returns: 90 (minutes between the two times)
```

### calculateDaysBetween

```java
public static double calculateDaysBetween(String start, String end)
```

Calculates the number of days (as a decimal) between two date-time strings.

**Parameters:**
- `start`: The start date-time string
- `end`: The end date-time string

**Returns:**
- The number of days between start and end as a decimal value

**Throws:**
- `IllegalArgumentException` if either string is not in a supported format

**Example:**
```java
double days = TimeUtils.calculateDaysBetween("2023-01-01|10:00", "2023-01-03|14:30");
// Returns: 2.1875 (days between the two times)
```

### addHours

```java
public static String addHours(String dateTimeStr, long hours)
```

Adds a given number of hours to a date-time string and returns the result.

**Parameters:**
- `dateTimeStr`: The input date-time string
- `hours`: Number of hours to add

**Returns:**
- The resulting date-time string in format "yyyy-MM-dd|HH:mm"

**Throws:**
- `IllegalArgumentException` if the input string is not in a supported format

**Example:**
```java
String newTime = TimeUtils.addHours("2023-01-01|10:00", 5);
// Returns: "2023-01-01|15:00"
```

### isValidDateTime

```java
public static boolean isValidDateTime(String dateTimeStr)
```

Validates if a string is in a supported date-time format.

**Parameters:**
- `dateTimeStr`: The string to validate

**Returns:**
- `true` if the string is in a supported format, `false` otherwise

**Example:**
```java
boolean isValid = TimeUtils.isValidDateTime("2023-01-01|10:00");
// Returns: true

boolean isInvalid = TimeUtils.isValidDateTime("2023/01/01 10:00");
// Returns: false
```

## Usage Examples

### Calculating Borrowing Period

```java
// Calculate days between borrow and return
double daysBorrowed = TimeUtils.calculateDaysBetween(borrowTime, returnTime);

// Check if overdue
if (user instanceof Student) {
    if (resource instanceof Book) {
        daysOverdue = daysBorrowed - 10; // 10 days allowed for students borrowing books
    } else if (resource instanceof Thesis) {
        daysOverdue = daysBorrowed - 7;  // 7 days allowed for students borrowing theses
    }
} else {
    if (resource instanceof Book) {
        daysOverdue = daysBorrowed - 14; // 14 days allowed for staff/professors borrowing books
    } else if (resource instanceof Thesis) {
        daysOverdue = daysBorrowed - 10; // 10 days allowed for staff/professors borrowing theses
    }
}
```

### Scheduling Reading Sessions

```java
// Schedule a 2-hour reading session
String startTime = "2023-01-01|10:00";
String endTime = TimeUtils.addHours(startTime, 2);
// endTime will be "2023-01-01|12:00"
```

### Reporting Passed Deadlines

```java
// Check if a resource is overdue
double daysOverdue = TimeUtils.calculateDaysBetween(currentTime, borrowTime) - allowedDays;
if (daysOverdue > 0) {
    // Resource is overdue
    overdueResources.add(resource.getId());
}
```

## Error Handling

The class throws `IllegalArgumentException` with descriptive messages when:

1. The input string is null or empty
2. The input string is not in any of the supported formats
3. The date components are invalid (e.g., month > 12, day > 31)

**Example:**
```java
try {
    TimeUtils.calculateDaysBetween("2023-13-01", "2023-01-02");
} catch (IllegalArgumentException e) {
    System.out.println("Invalid date: " + e.getMessage());
}
```

## Best Practices

1. **Always validate input**: Use `isValidDateTime()` before performing calculations
2. **Handle exceptions**: Wrap calls in try-catch blocks to handle invalid formats
3. **Use appropriate methods**: Choose the method that best fits your needs:
   - `calculateHoursBetween()` for hour-level precision
   - `calculateMinutesBetween()` for minute-level precision
   - `calculateDaysBetween()` for day-level precision (including fractions)
4. **Consistent format**: Use the same format throughout your application for consistency 