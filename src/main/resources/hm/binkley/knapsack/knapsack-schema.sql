CREATE TEXT TABLE IF NOT EXISTS knapsack (
  key   VARCHAR(24) PRIMARY KEY,
  value VARCHAR(24) NOT NULL
);
SET TABLE knapsack
SOURCE 'knapsack.csv;encoding=UTF-8';
