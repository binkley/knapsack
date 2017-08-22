MERGE INTO knapsack AS a
USING (VALUES ?, ?) b (key, value)
ON a.key = b.key
WHEN MATCHED THEN
UPDATE SET a.value = b.value
WHEN NOT MATCHED THEN
INSERT (key, value) VALUES (b.key, b.value);
