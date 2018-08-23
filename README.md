# Logmetric
Logmetric is log transforming tool from huge raw log date to small-size log metrics

# Use case
- Count log lines which includes specific log pattern (ex) Count response codes from web server's access code
- Count log field values when field value is string type (ex) Count event values from event log
- Sum log field values when field value is integer type

# Key concepts
- Transform huge raw log data to small-size log metrics and save your resources (like storage, network, ...)
- Support for various log format (ex) Popular web server writes combined access log, but the real access log format is a little different according to each web server

# Configuration
- input
  - kafka topic
- output
  - kafka topic
- duration: metric creation period
