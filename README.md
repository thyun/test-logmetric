# Logmetric
Logmetric is log transforming tool from raw log to metric

# Use case
- Count log lines which includes specific log pattern (ex) Count response codes from access code
- Count log field values when field value is string type (ex) Count event values from event log
- Sum log field values when field value is integer type

# Configuration
- input
  - kafka topic
- output
  - kafka topic
- duration: metric creation period
