-- TASK 1:

-- A)- Show the number of lessons given per month during a specified year.
CREATE VIEW number_of_lesson_given_per_month AS
SELECT SUM(lessons_given_per_month), month 
FROM(
SELECT COUNT(*) AS lessons_given_per_month, EXTRACT(MONTH FROM lesson_date) AS month
FROM scheduled_lesson
WHERE EXTRACT(YEAR FROM lesson_date) = '2021'
GROUP BY EXTRACT(MONTH FROM lesson_date)
UNION ALL
SELECT COUNT(*) AS lessons_given_per_month, EXTRACT(MONTH FROM lesson_date) AS month
FROM individual_lesson
WHERE EXTRACT(YEAR FROM lesson_date) = '2021'
GROUP BY EXTRACT(MONTH FROM lesson_date)
) AS lessons_given_per_month
GROUP BY month

-- B)- It shall be possible to retrieve the total number of lessons (just one number) and the specific number of individual lessons, group lessons and ensembles:

CREATE VIEW total_number_of_lesson AS
SELECT SUM(lessons_given_per_year), year 
FROM(
SELECT COUNT(*) AS lessons_given_per_year, EXTRACT(YEAR FROM lesson_date) AS year
FROM scheduled_lesson
WHERE EXTRACT(YEAR FROM lesson_date)
GROUP BY EXTRACT(YEAR FROM lesson_date)
UNION ALL
SELECT COUNT(*) AS lessons_given_per_year, EXTRACT(YEAR FROM lesson_date) AS year
FROM individual_lesson
WHERE EXTRACT(YEAR FROM lesson_date) 
GROUP BY EXTRACT(YEAR FROM lesson_date)
) AS lessons_given_per_year
GROUP BY year


-- C)- The specific number of individual lessons, group lessons and ensembles:

CREATE VIEW specific_number_of_lesson AS
SELECT SUM(total_lessons), lesson_kind
FROM(
SELECT COUNT(*) AS total_lessons,'individual_lesson' AS lesson_kind
FROM individual_lesson
UNION ALL
SELECT COUNT(*) AS total_lessons,'ensembles' AS lesson_kind
FROM ensembles
UNION ALL
SELECT COUNT(*) AS total_lessons, 'group_lesson'  AS lesson_kind
FROM group_lesson
) AS total_lessons
GROUP BY lesson_kind


-- TASK 2:

-- The same as above, but retrieve the average number of lessons per month during the entire year, instead of the total for each month.

CREATE VIEW average_number_of_lesson_per_month AS
SELECT SUM(average_total_lesson)/12, month 
FROM(
SELECT COUNT(*) AS average_total_lesson, EXTRACT(MONTH FROM lesson_date) AS month
FROM individual_lesson
WHERE EXTRACT(YEAR FROM lesson_date) = '2021'
GROUP BY EXTRACT(MONTH FROM lesson_date)
UNION ALL
SELECT COUNT(*) AS average_total_lesson, EXTRACT(MONTH FROM lesson_date) AS month
FROM ensembles
WHERE EXTRACT(YEAR FROM lesson_date)='2021'
GROUP BY EXTRACT(MONTH FROM lesson_date)
UNION ALL
SELECT COUNT(*) AS average_total_lesson, EXTRACT(MONTH FROM lesson_date) AS month
FROM group_lesson
WHERE EXTRACT(YEAR FROM lesson_date) ='2021'
GROUP BY EXTRACT(MONTH FROM lesson_date)
) AS average_total_lesson
GROUP BY month


-- TASK 3:

-- A)- List all instructors who has given more than a specific number of lessons during the current month.

CREATE VIEW instructor_given_more_than_1_lesson AS
SELECT SUM(number_of_times)AS number_of_times , inst_id, month
FROM(
SELECT instructor_id AS inst_id, COUNT(instructor_id) AS number_of_times,
 'individual_lesson' AS lesson_kind, EXTRACT(MONTH FROM lesson_date) AS month
FROM individual_lesson
WHERE EXTRACT(MONTH FROM lesson_date)
GROUP BY instructor_id, month
HAVING COUNT(instructor_id) > 1
UNION ALL
SELECT instructor_id AS inst_id, COUNT(instructor_id) AS number_of_times,
 'group_lesson' AS lesson_kind, EXTRACT(MONTH FROM lesson_date) AS month
FROM group_lesson
GROUP BY instructor_id, month
HAVING COUNT(instructor_id) > 1
UNION ALL
SELECT instructor_id AS inst_id, COUNT(instructor_id) AS number_of_times,
 'ensembles' AS lesson_kind, EXTRACT(MONTH FROM lesson_date) AS month
FROM ensembles
GROUP BY instructor_id, month
HAVING COUNT(instructor_id) > 1
) 
AS number_of_times
GROUP BY inst_id, month


-- B)- Sum all lessons, independent of type

CREATE VIEW  sum_all_lessons_independent_of_type AS
SELECT SUM(number_of_times)AS number_of_times , inst_id, lesson_kind, month
FROM(
SELECT instructor_id AS inst_id, COUNT(instructor_id) AS number_of_times,
 'individual_lesson' AS lesson_kind, EXTRACT(MONTH FROM lesson_date) AS month
FROM individual_lesson
WHERE EXTRACT(MONTH FROM lesson_date)
GROUP BY instructor_id, lesson_kind, month
HAVING COUNT(instructor_id) 
UNION ALL
SELECT instructor_id AS inst_id, COUNT(instructor_id) AS number_of_times,
 'group_lesson' AS lesson_kind, EXTRACT(MONTH FROM lesson_date) AS month
FROM group_lesson
GROUP BY instructor_id, lesson_kind, month
HAVING COUNT(instructor_id)
UNION ALL
SELECT instructor_id AS inst_id, COUNT(instructor_id) AS number_of_times,
 'ensembles' AS lesson_kind, EXTRACT(MONTH FROM lesson_date) AS month
FROM ensembles
GROUP BY instructor_id, lesson_kind, month
HAVING COUNT(instructor_id) 
) 
AS number_of_times
GROUP BY inst_id, lesson_kind, month


-- C)- Also list the three instructors having given most lessons (independent of lesson type)

CREATE VIEW most_three_instructors_having_given_most_lessons  AS
SELECT SUM(number_of_times)AS number_of_times , inst_id, lesson_kind, month

FROM(
SELECT instructor_id AS inst_id, COUNT(instructor_id) AS number_of_times,
 'individual_lesson' AS lesson_kind, EXTRACT(MONTH FROM lesson_date) AS month
FROM individual_lesson
WHERE EXTRACT(MONTH FROM lesson_date)
GROUP BY instructor_id, lesson_kind, month
HAVING COUNT(instructor_id) 
UNION ALL
SELECT instructor_id AS inst_id, COUNT(instructor_id) AS number_of_times,
 'group_lesson' AS lesson_kind, EXTRACT(MONTH FROM lesson_date) AS month
FROM group_lesson
GROUP BY instructor_id, lesson_kind, month
HAVING COUNT(instructor_id) 
UNION ALL
SELECT instructor_id AS inst_id, COUNT(instructor_id) AS number_of_times,
 'ensembles' AS lesson_kind, EXTRACT(MONTH FROM lesson_date) AS month
FROM ensembles
GROUP BY instructor_id, lesson_kind, month
HAVING COUNT(instructor_id) 
) 
AS number_of_times
GROUP BY inst_id, lesson_kind, month
ORDER BY number_of_times
LIMIT 3


-- TASK 4: 

-- List all ensembles held during the next week, sorted by music genre and weekday. 
-- For each ensemble tell whether it's full booked, has 1-2 seats left or has more seats left.

CREATE VIEW list_of_all_ensembles_held_during_next_week AS
SELECT genre, DAYNAME(lesson_date) AS weekday, available_places
FROM ensembles
GROUP BY genre, weekday ,available_places
ORDER BY genre

