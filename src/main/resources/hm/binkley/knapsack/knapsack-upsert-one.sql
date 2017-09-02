MERGE INTO knapsack AS a
USING (VALUES ?, ?, ?) b (layer, key, value)
ON a.layer = b.layer AND a.key = b.key
WHEN MATCHED THEN
UPDATE SET a.value = b.value
WHEN NOT MATCHED THEN
INSERT (layer, key, value) VALUES (b.layer, b.key, b.value);
