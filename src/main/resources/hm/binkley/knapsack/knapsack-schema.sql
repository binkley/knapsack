CREATE TEXT TABLE IF NOT EXISTS knapsack (
  layer INTEGER,
  key   VARCHAR(24),
  value VARCHAR(24) CONSTRAINT knapsack_value_nn NOT NULL,
  CONSTRAINT knapsack_pk PRIMARY KEY (layer, key)
);
SET TABLE knapsack
SOURCE 'knapsack.csv;encoding=UTF-8';
